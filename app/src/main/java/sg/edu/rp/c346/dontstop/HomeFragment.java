package sg.edu.rp.c346.dontstop;


import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{

    private static String TAG = "HomeFragment";

    private static final int REFRESH_STEP_WHAT = 0;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new HomeFragment.TodayStepCounterCall());
    private int mStepSum;

    private ISportStepInterface iSportStepInterface;


    public HomeFragment() {
        // Required empty public constructor
    }

    GoalCustomAdapter gca;
    ArrayList<GoalItem> goalList = new ArrayList<>();
    ListView lvGoal;

    boolean recording = false;
    private static final String STEP = "Steps";
    private static final String TIME = "Time";
    private static final String FREQUENCY = "Frequency";
    private GoalItem goalItem;
    private String stepArray;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    FirebaseFirestore fs = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lvGoal = view.findViewById(R.id.lvGoals);
        gca = new GoalCustomAdapter(getActivity(), R.layout.goal_row, goalList);
        lvGoal.setAdapter(gca);
        goalItem = new GoalItem();


        //初始化计步模块
        TodayStepManager.init(getActivity().getApplication());


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
                    try {
                        jsonArray = new JSONArray(iSportStepInterface.getTodaySportStepArray());
                        jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                        Log.d(TAG, "onServiceConnected: " + iSportStepInterface.getTodaySportStepArray());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference myRef = FirebaseFirestore.getInstance().document(uid + "/Goals");
            myRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            goalItem.setGoalType(documentSnapshot.getString("Type"));
                            goalItem.setTargetAmount((Long) documentSnapshot.get("Amount"));
                            try {
                                if (jsonObject != null){
                                    goalItem.setCurrentAmount((int)jsonObject.get("stepNum"));
                                    Log.d(TAG, "onComplete: " + (int)jsonObject.get("stepNum"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            goalList.add(goalItem);
                            gca.notifyDataSetChanged();

                            Log.d(TAG, (String) task.getResult().get("Type"));
                            Log.d(TAG, task.getResult().get("Amount") + "");
                        } else {
                            Log.d(TAG, "Not exist");
                        }
                    }
                }
            });
        }

        return view;
    }

    private String getDate(Long dateInMill) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM");
        return format.format(dateInMill);
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
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }


}
