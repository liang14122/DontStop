package sg.edu.rp.c346.dontstop;
 import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.NumberPicker;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.util.HashMap;
        import java.util.Map;
        import java.util.Objects;

public class CustomizeGoalActivity extends AppCompatActivity {

    public static final String STEP_TARGET_FOR_EVERYDAY = "Step Target for Everyday(Steps)";
    public static final String STEP = "Step";
    public static final String TIME = "Time";
    public static final String FREQUECY = "Frequency";
    public static final String[] TYPE = {"Step", "Time", "Frequency"};
    public static final String ACTIVTED_TIME_FOR_EVERYDAY_MINS = "Activated Time for Everyday(Mins)";
    public static final String WORKOUT_FREQUENCY_FOR_EVERY_WEEK_TIMES = "Workout Frequency for EveryWeek(Times)";
    Spinner spnType;
    TextView tvType;
    NumberPicker npAmount;
    Button btnOK;
    int spnPosition = 0;
    FirebaseUser user;
    int goalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_goal);

        spnType = findViewById(R.id.spinnerType);
        tvType = findViewById(R.id.textViewGoalType);
        npAmount = findViewById(R.id.numberPickerAmount);
        btnOK = findViewById(R.id.buttonOK);

        setnp(TYPE[spnPosition]);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                setnp(TYPE[position]);
                spnPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        npAmount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
//                npData = String.valueOf(newVal);

            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOK.setEnabled(false);
                String goalType = TYPE[spnPosition];
//                String amount = String.valueOf(npAmount.getValue());

                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    DocumentReference myRef = FirebaseFirestore.getInstance().document(user.getUid() + "/Goals");

                    Map<String, Object> goal = new HashMap<>();
                    goalAmount = Integer.parseInt(returnArray(goalType)[npAmount.getValue()]);
                    goal.put(goalType, goalAmount);

                        myRef.update(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CustomizeGoalActivity.this, "Create your goal successfully " + goalAmount + "  " + npAmount.getValue(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CustomizeGoalActivity.this, "Fail to create your goal", Toast.LENGTH_SHORT).show();
                                Log.i("ErrorInsert", "" + e);
                                btnOK.setEnabled(true);
                            }
                        });
                        Intent intent = new Intent(CustomizeGoalActivity.this, HomeActivity.class);
                        startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomizeGoalActivity.this, SignInActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private String[] returnArray(String goalType) {
        switch (goalType) {
            case STEP:
                return step();
            case TIME:
                return time();
            default:
                return frequency();
        }
    }


    private void setnp(String option) {


        if (Objects.equals(option, STEP)) {
            tvType.setText(STEP_TARGET_FOR_EVERYDAY);
            npAmount.setDisplayedValues(step());
            npAmount.setValue(9);
            npAmount.setMaxValue(step().length - 1);
        } else if (Objects.equals(option, TIME)) {
            tvType.setText(ACTIVTED_TIME_FOR_EVERYDAY_MINS);
            npAmount.setDisplayedValues(time());
            npAmount.setValue(9);

        } else if (Objects.equals(option, FREQUECY)) {
            tvType.setText(WORKOUT_FREQUENCY_FOR_EVERY_WEEK_TIMES);
//            npAmount.setDisplayedValues(frequency());
            npAmount.setValue(3);
            npAmount.setMaxValue(6);
        }
        npAmount.setMinValue(0);
        npAmount.setWrapSelectorWheel(true);
    }

    private String[] step() {
        String[] stepArray = new String[100];
        for (int a = 0; a < stepArray.length; a++) {
            stepArray[a] = Integer.toString((a + 1) * 1000);
        }

        return stepArray;
    }

    private String[] time() {
        String[] time = new String[22];
        for (int a = time.length - 1; a >= 0; a--) {
            time[(time.length - 1) - a] = Integer.toString((a + 3) * 10);
        }
        return time;
    }

    private String[] frequency() {
        return new String[]{"1", "2", "3", "4", "5", "6", "7"};
    }

}

