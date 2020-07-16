package in.zerene.test;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

import static in.zerene.test.ContactsChange.ACCEPTED;
import static in.zerene.test.ContactsChange.BLOCK;
import static in.zerene.test.ContactsChange.BLOCKED;
import static in.zerene.test.ContactsChange.CLOSE_OPTIONS;
import static in.zerene.test.ContactsChange.CONTACT_OPTIONS;
import static in.zerene.test.ContactsChange.DOWNGRADED;
import static in.zerene.test.ContactsChange.ERROR;
import static in.zerene.test.ContactsChange.REJECTED;
import static in.zerene.test.ContactsChange.REMOVED;
import static in.zerene.test.ContactsChange.RESPOND;
import static in.zerene.test.ContactsChange.SEND_REQUEST;
import static in.zerene.test.ContactsChange.SENT;
import static in.zerene.test.ContactsChange.UPGRADED;

public class Account extends AppCompatActivity {

    final static int EDIT_PROFILE=8693,SAVED=86931;

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    ConstraintLayout cl;
    ImageView imageViewDP;
    TextView textView;
    View gender;
    TextView verified;
    Button editContact,helpBlock;



    String uid,username,name,about,hobbies;
    long g;
    boolean verifiedFlag,me,hasDP=false,flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        editContact = findViewById(R.id.doubleButtonMyPage).findViewById(R.id.childCL).findViewById(R.id.button1);
        helpBlock = findViewById(R.id.doubleButtonMyPage).findViewById(R.id.childCL).findViewById(R.id.button2);

        gender = findViewById(R.id.genderMyPage);
        verified = findViewById(R.id.verifiedMyPage);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorage.setMaxOperationRetryTimeMillis(6000);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(firestoreSettings);

        Intent ti = getIntent();
        uid = ti.getStringExtra("uid");
        username = ti.getStringExtra("username");
        name = ti.getStringExtra("name");
        about = ti.getStringExtra("about");
        hobbies = ti.getStringExtra("hobbies");
        g = ti.getLongExtra("gender",0);
        verifiedFlag = ti.getBooleanExtra("verified",false);
        me = Objects.equals(uid,firebaseUser.getUid());

        setAllText();


        imageViewDP = findViewById(R.id.profilePicMyPage);
        final ProgressBar progressBarDP = findViewById(R.id.progressBarDP);
        progressBarDP.setVisibility(View.VISIBLE);
        firebaseStorage.getReference("users").child(uid).child("profilePic.jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(Account.this)
                        .load(uri) // the uri you got from Firebase
                        .into(imageViewDP);
                hasDP = true;
                progressBarDP.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBarDP.setVisibility(View.GONE);
                imageViewDP.setImageDrawable(getDrawable(R.drawable.profile));
                hasDP = false;
            }
        });

        Snackbar.make(findViewById(R.id.clAcc),
                "Welcome!!!",
                BaseTransientBottomBar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.colorAccent,null)).setTextColor(Color.WHITE).show();

        if(me){
            editContact.setText(R.string.edit_profile);
            editContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditCL();
                }
            });
        }
        else{

            notme();
        }

    }

    private void setAllText(){
        textView = findViewById(R.id.usernameMyPage);
        textView.setText(username);
        textView = findViewById(R.id.nameMyPage);
        textView.setText(name);
        textView = findViewById(R.id.myPageInfo).findViewById(R.id.about);
        textView.setText(about.equals("~#~#")?"":about);
        textView = findViewById(R.id.myPageInfo).findViewById(R.id.hobbies);
        textView.setText(hobbies.equals("~#~#")?"":hobbies);
        if(g==1)gender.setBackground(getDrawable(R.drawable.female));
        else if(g==2)gender.setBackground(getDrawable(R.drawable.lgbtq));
        else gender.setBackground((getDrawable(R.drawable.male)));
        if(verifiedFlag)verified.setVisibility(View.VISIBLE);

    }

    private void notme(){
        flag = true;
        if(flag) {
            FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/contacts/"+uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                flag = false;
                                editContact.setText(R.string.options);
                                editContact.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        contactOptions();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        if(flag){
            FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/received/"+uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                flag = false;
                                editContact.setText(R.string.receivedRespond);
                                editContact.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        receivedRespond();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        if(flag){
            FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/sent/"+uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                flag = false;
                                editContact.setText(R.string.cancel_request);
                                editContact.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelRequest();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        if(flag){
            FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/close/"+uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                flag = false;
                                editContact.setText(R.string.star_options);
                                editContact.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        closeOptions();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        if(flag){
            FirebaseDatabase.getInstance().getReference(firebaseUser.getUid()+"/blocked/"+uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                flag = false;
                                editContact.setText(R.string.blocked);
                                editContact.setEnabled(false);
                                helpBlock.setText(R.string.unblock);
                                helpBlock.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        unblock();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        if(flag){
            helpBlock.setText(R.string.block);
            helpBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    block();
                }
            });
            editContact.setText(R.string.add_contact);
            editContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addContact();
                }
            });
        }
    }

    private void unblock() {
        ContactsChange cc = new ContactsChange();
        if(cc.unblock(this,firebaseUser.getUid(),uid)){
            editContact.setText(R.string.add_contact);
            editContact.setEnabled(true);
            editContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addContact();
                }
            });
            helpBlock.setText(R.string.block);
            helpBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    block();
                }
            });
        }
    }

    private void closeOptions() {
        Intent intent = new Intent(this,ConfirmActivity.class);
        intent.putExtra("type",(byte)4);
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("myUID",firebaseUser.getUid());
        intent.putExtra("targetUID",uid);
        intent.putExtra("verified",verifiedFlag);
        startActivityForResult(intent,CLOSE_OPTIONS);
    }

    private void receivedRespond() {
        Intent intent = new Intent(this,ConfirmActivity.class);
        intent.putExtra("type",(byte)2);
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("myUID",firebaseUser.getUid());
        intent.putExtra("targetUID",uid);
        intent.putExtra("verified",verifiedFlag);
        startActivityForResult(intent,RESPOND);
    }

    private void contactOptions() {
        Intent intent = new Intent(this,ConfirmActivity.class);
        intent.putExtra("type",(byte)3);
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("myUID",firebaseUser.getUid());
        intent.putExtra("targetUID",uid);
        intent.putExtra("verified",verifiedFlag);
        startActivityForResult(intent,CONTACT_OPTIONS);
    }

    private void addContact(){
        Intent intent = new Intent(this,ConfirmActivity.class);
        intent.putExtra("type",(byte)1);
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("myUID",firebaseUser.getUid());
        intent.putExtra("targetUID",uid);
        intent.putExtra("verified",verifiedFlag);
        startActivityForResult(intent,SEND_REQUEST);
    }

    private void cancelRequest(){
        ContactsChange cc = new ContactsChange();
        if(cc.deleteRequest(this,firebaseUser.getUid(),1,uid)){
            editContact.setText(R.string.add_contact);
            editContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addContact();
                }
            });
        }
    }

    private void block(){
        Intent intent = new Intent(this,ConfirmActivity.class);
        intent.putExtra("type",(byte)5);
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("myUID",firebaseUser.getUid());
        intent.putExtra("targetUID",uid);
        intent.putExtra("verified",verifiedFlag);
        startActivityForResult(intent,BLOCK);
    }

    public void showEditCL(){
        Intent intent = new Intent(this,EditProfile.class);
        intent.putExtra("uid",firebaseUser.getUid());
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("about",about);
        intent.putExtra("hobbies",hobbies);
        intent.putExtra("gender",g);
        startActivityForResult(intent,EDIT_PROFILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEND_REQUEST){
            if(resultCode == SENT){
                editContact.setText(R.string.cancel_request);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelRequest();
                    }
                });
            }
            else if(resultCode == ERROR){
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == RESPOND){
            if(resultCode == ACCEPTED){
                editContact.setText(R.string.options);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactOptions();
                    }
                });
            }
            else if(resultCode == REJECTED){
                editContact.setText(R.string.add_contact);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addContact();
                    }
                });
            }
            else if(resultCode == ERROR){
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == CONTACT_OPTIONS){
            if(resultCode == UPGRADED){
                editContact.setText(R.string.star_options);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeOptions();
                    }
                });
            }
            else if(resultCode == REMOVED){
                editContact.setText(R.string.add_contact);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addContact();
                    }
                });
            }
            else if(resultCode == ERROR){
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == CLOSE_OPTIONS){
            if(resultCode == DOWNGRADED){
                editContact.setText(R.string.options);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactOptions();
                    }
                });
            }
            else if(resultCode == REMOVED){
                editContact.setText(R.string.add_contact);
                editContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addContact();
                    }
                });
            }
            else if(resultCode == ERROR){
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == BLOCK){
            if(resultCode == BLOCKED){
                editContact.setText(R.string.blocked);
                editContact.setEnabled(false);
                helpBlock.setText(R.string.unblock);
                helpBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unblock();
                    }
                });
            }
            else if(resultCode == ERROR){
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == EDIT_PROFILE){

            if(resultCode == SAVED){

                if(data.getBooleanExtra("picFlag",false)){
                    imageViewDP.setImageURI((Uri) data.getParcelableExtra("picUri"));
                }
                else if(data.getBooleanExtra("picR",false))
                    imageViewDP.setImageDrawable(getDrawable(R.drawable.profile));

                username = data.getStringExtra("username");
                name = data.getStringExtra("name");
                about = data.getStringExtra("about");
                hobbies = data.getStringExtra("hobbies");
                g = data.getLongExtra("g",0);
                setAllText();
            }
        }

    }

    /**
     * Opens the Options for the User.
     * Admin options for the current user and general options for other users.
     */
    public void show_menu(View view){
        findViewById(R.id.optionsButton_Account).setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when log out button in clicked.
     * It logs out the user and starts the MainActivity (flash screen).
     */
    public void logout_onClick(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Closes the Options for the user.
     */
    public void closeMenu(View view){
        findViewById(R.id.optionsButton_Account).setVisibility(View.GONE);
    }

    public void openContactsActivity(View view){
        Intent intent = new Intent(this,Contacts.class);
        startActivity(intent);
    }

    public void openMessagesActivity(View view){
        Intent intent = new Intent(this, Message.class);
        startActivity(intent);
    }
}
