package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginBtn, registerBtn;
    private TextView normalAcountTextView;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    final private static String TAG = "LOGIN_ACTIVITY";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        mProgress = new ProgressDialog(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    private void startSignIn() {

        if (!TextUtils.isEmpty(emailField.getText().toString().trim()) || !TextUtils.isEmpty(passwordField.getText().toString().trim())) {

            mProgress.setMessage(getResources().getString(R.string.logging_in));
            mProgress.show();

            mAuth.signInWithEmailAndPassword(emailField.getText().toString().trim(), passwordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthInvalidUserException e) {
                            emailField.setError(getResources().getString(R.string.invalid_email));
                            emailField.requestFocus();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            passwordField.setError(getResources().getString(R.string.invalid_password));
                            passwordField.requestFocus();
                        } catch(Exception e) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    mProgress.dismiss();
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.fields_empty), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}