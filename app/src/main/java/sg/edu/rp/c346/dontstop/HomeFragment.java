package sg.edu.rp.c346.dontstop;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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


}
