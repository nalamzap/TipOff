package in.zerene.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LogIn extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    EditText emailText;
    EditText passwordText;

    String username,name;
    boolean v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(firestoreSettings);

        emailText = findViewById(R.id.emailLogIn);
        passwordText = findViewById(R.id.passwordLogIn);
    }

    public void signupActivity(View view){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
        finish();
    }

    public void login_onClick(View view){

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if(email.isEmpty()){
            emailText.setError("Oops!E-Mail cannot be empty");
            emailText.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Oops!E-Mail is not valid");
            emailText.requestFocus();
        }
        else if(password.isEmpty()){
            passwordText.setError("Oops!Password cannot be empty");
            passwordText.requestFocus();
        }
        else{
            final ProgressBar progressBar = findViewById(R.id.progressBarLogIn);
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        getUser();

                    }else{
                        Toast.makeText(LogIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getUser(){

        db.collection("users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        username = documentSnapshot.getString("username");
                        name = documentSnapshot.getString("name");
                        v = documentSnapshot.getBoolean("verified");
                        getUserExtras();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LogIn.this, "Something went wrong!\nTry reopening app", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }
                        }, 3000);

                    }
                });
    }

    private void getUserExtras() {
        db.collection("userExtras").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        OpenAccount openAccount = new OpenAccount(
                                LogIn.this,
                                Account.class,
                                firebaseUser.getUid(),
                                username,
                                name,
                                documentSnapshot.getString("about"),
                                documentSnapshot.getString("hobbies"),
                                documentSnapshot.getLong("gender"),
                                v);
                        Intent intent = openAccount.getIntent();
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }
                        }, 3000);

                    }
                });

    }
}
