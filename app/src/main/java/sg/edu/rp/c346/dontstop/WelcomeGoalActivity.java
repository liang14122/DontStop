package sg.edu.rp.c346.dontstop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeGoalActivity extends AppCompatActivity {

    public static final String STEP = "StepTarget";
    public static final String TIME = "TimeTarget";
    public static final String FREQUENCY = "FrequencyTarget";
    public static final String TYPE = "Type";
    public static final String Amount = "Amount";
    public static final int W1 = 10000;
    public static final int H1 = 60;
    public static final int T3 = 3;
    Button btnStep, btnTime, btnFrequency, btnCustomize, btnNext;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_goal);

        btnStep = findViewById(R.id.buttonSteps);
        btnTime = findViewById(R.id.buttonTime);
        btnFrequency = findViewById(R.id.buttonFrequency);
        btnCustomize = findViewById(R.id.buttonCustomize);
        btnNext = findViewById(R.id.buttonNext);

        btnStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertGoal(STEP);

            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertGoal(TIME);
            }
        });

        btnFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertGoal(FREQUENCY);
            }
        });

        btnCustomize.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                customizeGoal();
            }
        });

    }

    private void customizeGoal() {
        Intent intent = new Intent(getApplicationContext(), CustomizeGoalActivity.class);
        startActivity(intent);
    }

    private void insertGoal(String type) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String uid = user.getUid();
            DocumentReference myRef = FirebaseFirestore.getInstance().document(uid + "/Goals");
            Map<String, Object> goal = new HashMap<>();
            goal.put(TYPE, type);
            switch (type) {
                case STEP:
                    goal.put(Amount, W1);
                    break;
                case TIME:
                    goal.put(Amount, H1);
                    break;
                case FREQUENCY:
                    goal.put(Amount, T3);
                    break;
            }

            if(!goal.isEmpty()){
                myRef.set(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("UserGoal", "Saved successfully");
                        Intent intent = new Intent(WelcomeGoalActivity.this, HomeActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Goal Inserted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("UserGoal", "Saved failed");
                        Log.i("ErrorGoal", e + "");
                    }
                });
            }else{
                Log.i("Goals", "Empty");
            }

        }else{
            Intent intent = new Intent(WelcomeGoalActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }
}
