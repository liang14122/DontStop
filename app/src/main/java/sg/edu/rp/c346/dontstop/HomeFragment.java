package sg.edu.rp.c346.dontstop;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.constraint.solver.Goal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SensorEventListener {

    private static String TAG = "MainActivity";

    private static final int REFRESH_STEP_WHAT = 0;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new HomeFragment.TodayStepCounterCall());
    private int mStepSum;

    private ISportStepInterface iSportStepInterface;

    private TextView mStepArrayTextView;

    public HomeFragment() {
        // Required empty public constructor
    }

    GoalCustomAdapter gca;
    ArrayList<GoalItem> goalList = new ArrayList<>();
    ListView lvGoal;
    SensorManager sensorManager;

    boolean recording = false;
    private static final String STEP = "Steps";
    private static final String TIME = "Time";
    private static final String FREQUENCY = "Frequency";

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        goalList.add(setGoalItem(STEP));
//        goalList.add(setGoalItem(TIME));
//        goalList.add(setGoalItem(FREQUENCY))
        if (user != null) {
            String uid = user.getUid();
            CollectionReference goalRef = fs.collection(uid + "/Goals/Steps");
            Calendar sevenDaysAgo = Calendar.getInstance();
            sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -7);
            final Long dateInMill = sevenDaysAgo.getTimeInMillis();
            goalRef.whereGreaterThan("DateInMillSecond", dateInMill).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    ArrayList<Map> currentWeek = new ArrayList<>();
                    for (DocumentSnapshot doc : documentSnapshots){
                        Map<String, Integer> currentData = new HashMap<>();
                        int amount = (Integer) doc.get("Amount");
                        Long dateInMillis = (Long) doc.get("DateInMillSecond");
                        String date = getDate(dateInMillis);
                        currentData.put(date, amount);
                        currentWeek.add(currentData);
                        Log.i("date", amount + "");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                 Log.e("Error", "" + e.getMessage());
                }
            });
        }


        lvGoal = view.findViewById(R.id.lvGoals);
        gca = new GoalCustomAdapter(getActivity(), R.layout.goal_row, goalList);
        lvGoal.setAdapter(gca);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);



        //初始化计步模块
        TodayStepManager.init(getActivity().getApplication());

        mStepArrayTextView = view.findViewById(R.id.stepArrayTextView);

        //开启计步Service，同时绑定Activity进行aidl通信
        Intent intent = new Intent(getContext(), TodayStepService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Activity和Service通过aidl进行通信
                iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                try {
                    mStepSum = iSportStepInterface.getCurrentTimeSportStep();
                    updateStepCount();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        return view;
    }

    private String getDate(Long dateInMill) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM");
        return format.format(dateInMill);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        if (recording) {
//            //upload data to fire base abd ui elements
//            Toast.makeText(getActivity(), sensorEvent.values[0] + "", Toast.LENGTH_SHORT).show();
//            if (sensorEvent.values[0] < Integer.MAX_VALUE) {
//                steps = (int) sensorEvent.values[0];
//                updateUI();
//            } else {
//                Log.i("Amount of sensor", "too big");
//            }
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
        recording = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            Log.i("Find sensor", "Sensor");
        } else {
            Log.i("Error find sensor", "No such Sensor");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                sm.unregisterListener(this);
            }

        } catch (Exception e) {
            Log.i("Error", Arrays.toString(e.getStackTrace()) + "");
        }
    }

    class TodayStepCounterCall implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //每隔500毫秒获取一次计步数据刷新UI
                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (mStepSum != step) {
                            mStepSum = step;
//                            updateStepCount();
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }

    private void updateStepCount() {
        Log.e(TAG, "updateStepCount : " + mStepSum);
//        TextView stepTextView = (TextView) findViewById(R.id.stepTextView);
//        stepTextView.setText(mStepSum + "Step");
    }

//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.stepArrayButton: {
//                //显示当天计步数据详细，步数对应当前时间
//                if (null != iSportStepInterface) {
//                    try {
//                        String stepArray = iSportStepInterface.getTodaySportStepArray();
//                        mStepArrayTextView.setText(stepArray);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            }
//            default:
//                break;
//        }
//    }
}
