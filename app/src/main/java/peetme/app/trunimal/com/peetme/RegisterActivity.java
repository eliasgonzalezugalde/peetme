package peetme.app.trunimal.com.peetme;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "REGISTER_ACTIVITY";
    private EditText nameField, emailField, passwordField;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        mProgress = new ProgressDialog(this);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

    }

    private void startRegister() {

        if (!TextUtils.isEmpty(nameField.getText().toString().trim()) && !TextUtils.isEmpty(emailField .getText().toString().trim()) && !TextUtils.isEmpty(passwordField.getText().toString().trim())) {

            mProgress.setMessage(getResources().getString(R.string.signing_up));
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(emailField .getText().toString().trim(), passwordField.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(nameField.getText().toString().trim());
                        current_user_db.child("image").setValue("default");
                        //mProgress.dismiss();
                    } else {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthWeakPasswordException e) {
                            passwordField.setError(getResources().getString(R.string.invalid_password));
                            passwordField.requestFocus();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            emailField.setError(getResources().getString(R.string.invalid_email));
                            emailField.requestFocus();
                        } catch(FirebaseAuthUserCollisionException e) {
                            emailField.setError(getResources().getString(R.string.email_taken));
                            emailField.requestFocus();
                        } catch(Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
                        }
                    }
                    mProgress.dismiss();

                }

            });

        } else {
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.fields_empty), Toast.LENGTH_SHORT).show();
        }

    }
}
