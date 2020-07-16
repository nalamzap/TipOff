package in.zerene.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    Map<String,Object> user = new HashMap<>();
    Map<String,Object> userE = new HashMap<>();

    DocumentReference documentReference;
    DocumentReference documentReference2;

    ImageView imageViewNewDP;
    EditText usernameEdit,nameEdit,aboutEdit,hobbiesEdit;
    RadioGroup genderEditRadioGroup;

    Intent intent;
    Uri imageUriDP;
    String username,name,about,hobbies;
    long g,gEdit;
    boolean hasDP=false,changedDP=false,removeDP=false,flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        imageViewNewDP = findViewById(R.id.imageViewNewDP);
        usernameEdit = findViewById(R.id.usernameEdit);
        nameEdit = findViewById(R.id.nameEdit);
        genderEditRadioGroup = findViewById(R.id.genderEditRadioGroup);
        aboutEdit = findViewById(R.id.aboutText_edit);
        hobbiesEdit = findViewById(R.id.hobbiesText_edit);

        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        removeDP = false;
        changedDP = false;

        FirebaseStorage.getInstance().getReference("users/"+getIntent()
                .getStringExtra("uid")).child("profilePic.jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hasDP = true;
                Glide
                        .with(EditProfile.this)
                        .load(uri) // the uri you got from Firebase
                        .into(imageViewNewDP);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageViewNewDP.setImageDrawable(getDrawable(R.drawable.profile));
            }
        });
        username = getIntent().getStringExtra("username");
        usernameEdit.setText(username);
        name = getIntent().getStringExtra("name");
        nameEdit.setText(name);
        about = getIntent().getStringExtra("about");
        aboutEdit.setText(about);
        hobbies = getIntent().getStringExtra("hobbies");
        hobbiesEdit.setText(hobbies);
        g = getIntent().getLongExtra("gender",0);
        if(g==0)genderEditRadioGroup.check(R.id.male_editProfile);
        else if(g==1)genderEditRadioGroup.check(R.id.female_editProfile);
        else genderEditRadioGroup.check(R.id.lgbtq_editProfile);
        addListeners();
    }

    public void change_dp(View view){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                changedDP = true;
                removeDP = false;
                imageUriDP = resultUri;
                imageViewNewDP.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                try {
                    throw (error);
                } catch (Exception ignore) {

                }
            }
        }
    }

    public void removeDP(View view){
        if(hasDP){
            removeDP = true;
            imageViewNewDP.setImageResource(android.R.color.transparent);
        }
        changedDP = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners(){
        usernameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    for(char ch:s.toString().toCharArray()) {
                        if (!((Character.isLowerCase(ch) && Character.isLetterOrDigit(ch)) || ch == '~' || ch == '_')){
                            usernameEdit.setText("");
                            usernameEdit.setError("Only lower case letters, digits, (_) and (~) are allowed");
                        }

                    }
                }
            }
        });
        aboutEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle Seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        hobbiesEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (null != hobbiesEdit.getLayout() && hobbiesEdit.getLayout().getLineCount() > 2)
                    hobbiesEdit.getText().delete(hobbiesEdit.getText().length() - 1, hobbiesEdit.getText().length());
            }
        });
    }

    public void gButton_editProfile_onClick(View view){
        switch(view.getId()) {
            case R.id.male_editProfile:
                gEdit = 0;
                break;
            case R.id.female_editProfile:
                gEdit = 1;
                break;
            case R.id.lgbtq_editProfile:
                gEdit = 2;
        }
    }

    public void checkUserName(String username){
        if(username.length()<1){
            usernameEdit.setError("username cannot be empty!");
        }
        else{
            FirebaseDatabase.getInstance().getReference("usernames").child(usernameEdit.getText().charAt(0)+"").child(usernameEdit.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) userNameExists(true);
                            else userNameExists(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void userNameExists(boolean exists){
        if(exists){
            usernameEdit.setError("Username is unavailable!");
            usernameEdit.requestFocus();
        }
        else{
            save();
        }
    }

    public void save_profile(View view){
        view.setEnabled(false);


        String name = nameEdit.getText().toString().trim();
        if(name.isEmpty()){
            nameEdit.setError("Oops! Name cannot be empty");
            nameEdit.requestFocus();
        }
        else if(name.length()<2){
            nameEdit.setError("Oops! Name must contain at least 2 characters");
            nameEdit.requestFocus();
        }else{
            if(!(username.equals(usernameEdit.getText().toString()))){
                checkUserName(usernameEdit.getText().toString());
            }
            else{
                save();
            }
        }


    }

    private void save(){
        final Task task = new Task();
        findViewById(R.id.progressEditProfile).setVisibility(View.VISIBLE);
        intent = new Intent();
        flag = true;
        if(changedDP){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


            ((BitmapDrawable)imageViewNewDP.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG,14,outputStream);




            firebaseStorage.getReference("users").child(firebaseUser.getUid()).child("profilePic.jpg")
                    .putBytes(outputStream.toByteArray()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, "Picture uploading failed!", Toast.LENGTH_SHORT).show();
                    task.t1Complete();
                    flag = false;
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    intent.putExtra("picFlag",true);
                    intent.putExtra("picUri",imageUriDP);
                    task.t1Complete();

                }
            });
        }
        else if(removeDP){
            firebaseStorage.getReference("users").child(firebaseUser.getUid()).child("profilePic.jpg").delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            flag = false;
                            task.t1Complete();
                            Toast.makeText(EditProfile.this, "Pic failed to be removed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    task.t1Complete();
                    intent.putExtra("picR",true);
                }
            });
        }
        else task.t1Complete();
        if(flag){

            documentReference = db.collection("users").document(firebaseUser.getUid());
            documentReference2 = db.collection("userExtras").document(firebaseUser.getUid());
            if(!(username.equals(usernameEdit.getText().toString()))) {
                user.put("username", usernameEdit.getText().toString());
            }
            if(!name.equals(nameEdit.getText().toString())) {
                user.put("name",nameEdit.getText().toString());
                user.put("namesmall",nameEdit.getText().toString().toLowerCase());
            }

            String temp = aboutEdit.getText().toString();
            temp = temp.trim();
            if(temp.equals(""))temp="~#~#";
            if(!about.equals(temp)) userE.put("about",temp);

            temp = hobbiesEdit.getText().toString();
            temp = temp.trim();
            if(temp.equals(""))temp="~#~#";
            if(!hobbies.equals(temp)) userE.put("hobbies",temp);

            if(g!=gEdit) userE.put("gender",gEdit);

            documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    documentReference2.update(userE).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            about = aboutEdit.getText().toString();
                            hobbies = hobbiesEdit.getText().toString();
                            g = gEdit;
                            username = usernameEdit.getText().toString();
                            name = nameEdit.getText().toString();
                            intent.putExtra("username",username);
                            intent.putExtra("name",name);
                            intent.putExtra("about",about);
                            intent.putExtra("hobbies",hobbies);
                            intent.putExtra("g",gEdit);
                            task.t2Complete();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    findViewById(R.id.saveProfile_button).setEnabled(true);
                    findViewById(R.id.progressEditProfile).setVisibility(View.GONE);
                    Toast.makeText(EditProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });



        }

    }

    public void closeEditCL(View view){
        setResult(ContactsChange.CANCELLED);
        finish();
    }
    class Task{
        boolean t1=false;
        boolean t2=false;

        void t1Complete(){
            t1 = true;
            if(t2)task();
        }
        void t2Complete(){
            t2 = true;
            if(t1)task();
        }

        private void task() {
            findViewById(R.id.progressEditProfile).setVisibility(View.GONE);
            setResult(Account.SAVED,intent);
            finish();
        }
    }
}
