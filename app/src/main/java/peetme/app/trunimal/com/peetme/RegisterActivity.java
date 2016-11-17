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

        final String name = nameField.getText().toString().trim();
        String email = emailField .getText().toString().trim();
        Log.e(TAG, emailField .getText().toString().trim());
        String password = passwordField.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            //traducir
            mProgress.setMessage("Singing Up");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");
                        mProgress.dismiss();
                    } else {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthWeakPasswordException e) {
                            passwordField.setError("Password");
                            passwordField.requestFocus();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            emailField.setError("Email inválido");
                            emailField.requestFocus();
                        } catch(FirebaseAuthUserCollisionException e) {
                            emailField.setError("User exist");
                            emailField.requestFocus();
                        } catch(Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                }

            });

        }

    }
}
