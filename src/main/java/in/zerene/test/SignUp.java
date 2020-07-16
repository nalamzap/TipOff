package in.zerene.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    EditText nameText;
    EditText emailText;
    EditText passwordText;
    EditText confPasswordText;
    EditText uName;
    Button signButton;
    ProgressBar progressBar;

    int g=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameText = findViewById(R.id.nameSignUp);
        emailText = findViewById(R.id.emailSignUp);
        passwordText = findViewById(R.id.passwordSignUp);
        confPasswordText = findViewById(R.id.confPasswordSignUp);
        uName = findViewById(R.id.usernameSignUp);
        signButton = findViewById(R.id.signUp);

        uName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    for(char ch:s.toString().toCharArray()) {
                        if (!((Character.isLowerCase(ch) && Character.isLetterOrDigit(ch)) || ch == '~' || ch == '_')){
                            uName.setText("");
                            uName.setError("Only lower case letters, digits, (_) and (~) are allowed");
                        }

                    }
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void loginActivity(View view){
        Intent intent = new Intent(this,LogIn.class);
        startActivity(intent);
        finish();
    }

    public void checkUsername(View view){
        checkUserName(uName.getText().toString(),false);
    }

    public void checkUserName(String username,final boolean sign){
        if(username.length()<1){
            uName.setError("username cannot be empty!");
        }
        else{
            db.collection("users").whereEqualTo("username",username).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(SignUp.this, "Available", Toast.LENGTH_SHORT).show();
                                if(sign) sign();
                            }
                            else uName.setError("Username is unavailable!");
                        }
                    });

        }
    }


    public void sign(){

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        progressBar = findViewById(R.id.progressBarSignUp);
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = task.getResult().getUser();
                    setDefaultProfileInfo();
                }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    progressBar.setVisibility(View.GONE);
                    signButton.setEnabled(true);
                    Toast.makeText(SignUp.this,"Error:User with this E-mail already exists!",Toast.LENGTH_LONG).show();
                }else{
                    progressBar.setVisibility(View.GONE);
                    signButton.setEnabled(true);
                    Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setDefaultProfileInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference("usernames").child(uName.getText().toString());
        databaseReference.setValue(firebaseUser.getUid());

        DocumentReference documentReference = db.collection("users").document(firebaseUser.getUid());
        Map<String,Object> user = new HashMap<>();
        user.put("username",uName.getText().toString());
        user.put("namesmall",nameText.getText().toString().toLowerCase());
        user.put("email",emailText.getText().toString());
        user.put("name",nameText.getText().toString());
        user.put("verified",false);
        documentReference.set(user);
        documentReference = db.collection("userExtras").document(firebaseUser.getUid());
        user = new HashMap<>();
        user.put("about","~#~#");
        user.put("hobbies","~#~#");
        user.put("gender",g);
        documentReference.set(user);
        profile();

    }

    private void profile(){
        OpenAccount openAccount = new OpenAccount(
                this,
                Account.class,
                firebaseUser.getUid(),
                uName.getText().toString(),
                nameText.getText().toString(),
                "~#~#",
                "~#~#",
                g,
                false);
        Intent intent = openAccount.getIntent();
        startActivity(intent);
        finish();
    }

    public void signup_onClick(View view){
        signButton.setEnabled(false);
        String name = nameText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String confPassword = confPasswordText.getText().toString().trim();


        if(email.isEmpty()){
            emailText.setError("Oops! E-Mail cannot be empty");
            emailText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Yikes! E-Mail is not valid");
            emailText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(password.isEmpty()){
            passwordText.setError("Uh oh! Password cannot be empty");
            passwordText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(confPassword.isEmpty()){
            confPasswordText.setError("Zoinks! Password cannot be empty");
            confPasswordText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(password.length()<6){
            passwordText.setError("Blimey! Password length cannot be less than 6");
            passwordText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(!password.equals(confPassword)){
            passwordText.setError("Oops! Passwords don't match. Please check them");
            passwordText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(name.isEmpty()){
            nameText.setError("Oops! Name cannot be empty");
            nameText.requestFocus();
            signButton.setEnabled(true);
        }
        else if(name.length()<2){
            nameText.setError("Oops! Name must contain at least 2 characters");
            nameText.requestFocus();
            signButton.setEnabled(true);
        }else{
            checkUserName(uName.getText().toString(),true);
        }

    }

    public void gButton_onClick(View view){
        switch(view.getId()){
            case R.id.male : g= 0;break;
            case R.id.female : g= 1;break;
            case R.id.lgbtq : g= 2;
        }
    }

}
