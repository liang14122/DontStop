package sg.edu.rp.c346.dontstop;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WelcomeInfoActivity extends AppCompatActivity {

    public static final String HEIGHT_KEY = "height";
    public static final String DOB_KEY = "dob";
    public static final String WEIGHT_KEY = "weight";
    public static final String UID_KEY = "Uid";
    public static final String PHOTO_URL_KEY = "photoUrl";
    EditText etHeight, etWeight, etDob;
    Button logOutBtn;
    FirebaseAuth mAuth;
    private FirebaseUser user;
    Button btnNext;
    public static final String USERNAME_KEY = "userName";
    public static final String EMAIL_KEY = "email";


    @Override
    protected void onStart() {
        super.onStart();
//        if (FirebaseAuth.getInstance() != null) {
//            Log.i("LoggedIn", "LoggedIn");
//            startActivity(new Intent(WelcomeInfoActivity.this, HomeActivity.class));
//        } else {
//            startActivity(new Intent(WelcomeInfoActivity.this, SignInActivity.class));
//            Log.i("NotLoggedIn", "NotLoggedIn");
//        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            String uid = user.getUid();
            DocumentReference profileRef = FirebaseFirestore.getInstance().document(uid + "/Profile");
            profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Log.d("User Type", "Exist User");
                            startActivity(new Intent(WelcomeInfoActivity.this, HomeActivity.class));
                        } else {
                            Log.d("User Type", "New User");
                        }
                    }
                }
            });
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        AddTempData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_info);
        etDob = findViewById(R.id.editTextDob);
        etHeight = findViewById(R.id.editTextHeight);
        etWeight = findViewById(R.id.editTextWeight);
        logOutBtn = findViewById(R.id.buttonLogOut);
        btnNext = findViewById(R.id.buttonNext);
        etHeight.requestFocus();
        etDob.setShowSoftInputOnFocus(false);

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(etHeight.getWindowToken(), 0);

        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(etDob.getWindowToken(), 0);
                DatePickerDialog.OnDateSetListener myDateListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                etDob.setText(date);
                            }
                        };
                Calendar calendarMax = Calendar.getInstance();
                Calendar calendarMin = Calendar.getInstance();
                calendarMin.set(1900, 0, 1);
                DatePickerDialog myDateDialog = new DatePickerDialog(WelcomeInfoActivity.this,
                        myDateListener, calendarMax.get(Calendar.YEAR), calendarMax.get(Calendar.MONTH),
                        calendarMax.get(Calendar.DAY_OF_MONTH));
                myDateDialog.getDatePicker().setMaxDate(calendarMax.getTimeInMillis());
                myDateDialog.getDatePicker().setMinDate(calendarMin.getTimeInMillis());
                myDateDialog.show();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                mAuth.signOut();
                Intent intent = new Intent(WelcomeInfoActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    Log.i(UID_KEY, uid+ "");
                    DocumentReference myRef = FirebaseFirestore.getInstance().document(uid + "/Profile");
                    String userName = user.getDisplayName();
                    Log.i(USERNAME_KEY, userName+ "");
                    String email = user.getEmail();
                    Log.i(EMAIL_KEY, email+ "");
                    String url = user.getPhotoUrl().toString();
                    Log.i(PHOTO_URL_KEY, url+ "");
                    String height = etHeight.getText().toString();
                    String weight = etWeight.getText().toString();
                    String dob = etDob.getText().toString();
                    if(height.isEmpty() || weight.isEmpty() || dob.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please Enter all Field", Toast.LENGTH_SHORT).show();
                        return;}
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put(USERNAME_KEY,userName);
                    userInfo.put(EMAIL_KEY, email);
                    userInfo.put(HEIGHT_KEY, height);
                    userInfo.put(WEIGHT_KEY, weight);
                    userInfo.put(DOB_KEY, dob);
                    userInfo.put(PHOTO_URL_KEY, url);
                    myRef.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("UserInfo", "Saved successfully");
                            Intent intent = new Intent(WelcomeInfoActivity.this, WelcomeGoalActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("UserInfo", "Saved failed");
                            Log.i("ErrorInfo", e + "");

                        }
                    });
                }
            }
        });
    }

    private void AddTempData() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        //currentDay
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat  format = new SimpleDateFormat("dd.MM.yyyy");
//        String dateString = format.format(Calendar.getInstance().getTime());
//        Long currentDateInMills = Calendar.getInstance().getTimeInMillis();
//        DocumentReference goalRef = FirebaseFirestore.getInstance().document(user.getUid() + "/Goals/Time/" + dateString);
//        Map<String, Object> currentData = new HashMap<>();
//        currentData.put("Amount", 45);
//        currentData.put("DateInMillSecond", currentDateInMills);
//        goalRef.set(currentData).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.i("add", "Successful");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i("add", "Failed");
//            }
//        });

        //one
        Calendar one = Calendar.getInstance();
        one.add(Calendar.DAY_OF_YEAR, -1);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat  format = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = format.format(one.getTime());
        Long currentDateInMills =one.getTimeInMillis();
        DocumentReference goalRef = FirebaseFirestore.getInstance().document(user.getUid() + "/Goals/Time/" + dateString);
        Map<String, Object> currentData = new HashMap<>();
        currentData.put("Amount", 80);
        currentData.put("DateInMillSecond", currentDateInMills);
        goalRef.set(currentData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("add", "Successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("add", "Failed");
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
