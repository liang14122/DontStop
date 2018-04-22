package sg.edu.rp.c346.dontstop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    //    SignInButton googleBtn;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 369;
    FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private Button mFaceBookButton;
    private Button mGoogleButton;


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance() != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //Google Sign In
//        googleBtn = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        mFaceBookButton = findViewById(R.id.facebookLogInBtn);
        mGoogleButton = findViewById(R.id.googleSignInBtn);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {

                    Toast.makeText(SignInActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();
                    //Intent to another new activity or do things
                    //Redirect to the home page without need to log in again
                    Intent intent = new Intent(SignInActivity.this, WelcomeInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleButton.setEnabled(false);
                signIn();
            }
        });

        //Facebook Sign In with a normal button
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        mFaceBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFaceBookButton.setEnabled(false);
                LoginManager.getInstance()
                        .logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("TAG", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("TAG", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

    }

    //    @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            // Pass the activity result back to the Facebook SDK
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Failed", "Google sign in failed", e);
                Toast.makeText(SignInActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                // ...
            }
        } else {
            //Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:successGoogle");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            Intent intent = new Intent(SignInActivity.this, WelcomeInfoActivity.class);
//                            startActivity(intent);
                            Toast.makeText(SignInActivity.this, "Successfully signed in Google", Toast.LENGTH_SHORT).show();
                            mGoogleButton.setEnabled(true);

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.signin_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                            mGoogleButton.setEnabled(true);
                        }


                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:successFacebook");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(SignInActivity.this, "Successfully signed in Facebook", Toast.LENGTH_SHORT).show();
                            final DocumentReference docRef = FirebaseFirestore.getInstance().document(user.getUid() + "/LastLogin");
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()){
                                            Log.i("New User?", "No");
                                            Log.i("Document", document+ "");
                                            //Skip info and goal
                                        }else{
                                            Log.i("New User?", "Yes");
                                            //ask for more info and goal
                                        }
                                        updateLoginDate(docRef);
                                    } else {
                                        Log.d("Error", "get failed with ", task.getException());
                                    }
                                }
                            });

                            mFaceBookButton.setEnabled(true);
//                            Intent intent = new Intent(SignInActivity.this, WelcomeInfoActivity.class);
//                            startActivity(intent);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mFaceBookButton.setEnabled(true);
//                            updateUI(null);
                        }


                    }
                });
    }

    private void updateLoginDate(DocumentReference docRef){
        Map<String, Object> loginDate = new HashMap<>();
        long calendar = Calendar.getInstance().getTimeInMillis();
        loginDate.put("LastLoginDate", calendar);
        docRef.set(loginDate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("lastLogin", "Successful");
//                Intent intent = new Intent(SignInActivity.this, WelcomeInfoActivity.class);
//                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("lastLogin", "Failed");
            }
        });
    }
}
