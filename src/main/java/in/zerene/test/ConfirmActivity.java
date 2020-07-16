package in.zerene.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class ConfirmActivity extends AppCompatActivity {

    byte type;
    TextView tvct,name,username;
    ImageView imgProfile;
    Button button1,button2,verified;
    EditText msg;
    ContactsChange cc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        tvct = findViewById(R.id.textViewConfirm);
        name = findViewById(R.id.textViewNameConfirm);
        username = findViewById(R.id.textViewUsernameConfirm);
        imgProfile = findViewById(R.id.imageViewConfirm);

        button1 = findViewById(R.id.button1Confirm);
        button2 = findViewById(R.id.button2Confirm);
        verified = findViewById(R.id.verified_account_confirm);
        msg = findViewById(R.id.reqMessage);

        cc = new ContactsChange();

        type = getIntent().getByteExtra("type",(byte)0);
        setProperties();


    }

    private void setProperties(){
        switch (type){
            case 1: tvct.setText(R.string.contact_request_confirmation_text);
                    username.setText(getIntent().getStringExtra("username"));
                    name.setText(getIntent().getStringExtra("name"));
                    if(getIntent().getBooleanExtra("verified",false))verified.setVisibility(View.VISIBLE);
                    button1.setText(R.string.yes);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            case1Button1();
                        }
                    });
                    button2.setText(R.string.cancel);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelButton2();
                        }
                    });
                FirebaseStorage.getInstance().getReference("users")
                        .child(getIntent().getStringExtra("targetUID")).child("profilePic.jpg")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide
                                .with(ConfirmActivity.this)
                                .load(uri) // the uri you got from Firebase
                                .into(imgProfile);
                    }
                });
                break;
            case 2:
                FirebaseDatabase.getInstance().getReference(getIntent().getStringExtra("myUID")
                        +"/received/"+getIntent().getStringExtra("targetUID"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String data = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                if(Objects.equals(data,"!@#$"))
                                    msg.setVisibility(View.GONE);
                                else msg.setText(data.substring(0,data.length()-4));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                msg.setEnabled(false);
                tvct.setText(R.string.accept_reject_text);
                username.setText(getIntent().getStringExtra("username"));
                name.setText(getIntent().getStringExtra("name"));
                if(getIntent().getBooleanExtra("verified",false))verified.setVisibility(View.VISIBLE);
                button1.setText(R.string.accept);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case2Button1();
                    }
                });
                button2.setText(R.string.reject);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case2Button2();
                    }
                });

                FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("targetUID")).child("profile")
                        .getBytes(10240000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    }
                });
                break;
            case 3:
                msg.setVisibility(View.GONE);
                tvct.setText(R.string.upgrade_remove_text);
                username.setText(getIntent().getStringExtra("username"));
                name.setText(getIntent().getStringExtra("name"));
                if(getIntent().getBooleanExtra("verified",false))verified.setVisibility(View.VISIBLE);
                button1.setText(R.string.upgrade);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case3Button1();
                    }
                });
                button2.setText(R.string.remove);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case3Button2();
                    }
                });
                FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("targetUID")).child("profile")
                        .getBytes(10240000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    }
                });
                break;
            case 4:
                msg.setVisibility(View.GONE);
                tvct.setText(R.string.downgrade_remove_text);
                username.setText(getIntent().getStringExtra("username"));
                name.setText(getIntent().getStringExtra("name"));
                if(getIntent().getBooleanExtra("verified",false))verified.setVisibility(View.VISIBLE);
                button1.setText(R.string.downgrade);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case4Button1();
                    }
                });
                button2.setText(R.string.remove);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case4Button2();
                    }
                });
                FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("targetUID")).child("profile")
                        .getBytes(10240000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    }
                });
                break;
            case 5:
                msg.setVisibility(View.GONE);
                tvct.setText(R.string.block_confirm_text);
                username.setText(getIntent().getStringExtra("username"));
                name.setText(getIntent().getStringExtra("name"));
                if(getIntent().getBooleanExtra("verified",false))verified.setVisibility(View.VISIBLE);
                button1.setText(R.string.block);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        case5Button1();
                    }
                });
                button2.setText(R.string.cancel);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelButton2();
                    }
                });
                FirebaseStorage.getInstance().getReference(getIntent().getStringExtra("targetUID")).child("profile")
                        .getBytes(10240000).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    }
                });

        }
    }

    private void case5Button1() {
        if(cc.block(this,getIntent().getStringExtra("myUID")
                ,getIntent().getStringExtra("targetUID"))){
            setResult(ContactsChange.BLOCKED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }

    private void case4Button2() {
        if(cc.deleteRequest(this,getIntent().getStringExtra("myUID"),3
                ,getIntent().getStringExtra("targetUID"))) {
            setResult(ContactsChange.REMOVED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }

    private void case4Button1() {
        if(cc.downgradeContact(this,getIntent().getStringExtra("myUID")
                ,getIntent().getStringExtra("targetUID"))){

            setResult(ContactsChange.DOWNGRADED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }

    //case3 button listeners
    private void case3Button2() {
        if(cc.deleteRequest(this,getIntent().getStringExtra("myUID"),2
                ,getIntent().getStringExtra("targetUID"))) {
            setResult(ContactsChange.REMOVED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }

    private void case3Button1() {

        if(cc.upgradeContact(this,getIntent().getStringExtra("myUID")
                ,getIntent().getStringExtra("targetUID"))){

            setResult(ContactsChange.UPGRADED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }


    }

    //case2 button listeners
    private void case2Button2() {
        if(cc.deleteRequest(this,getIntent().getStringExtra("myUID"),0
                ,getIntent().getStringExtra("targetUID"))){
            setResult(ContactsChange.REJECTED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }

    private void case2Button1() {
        if(cc.confirmRequest(this,getIntent().getStringExtra("myUID")
                ,getIntent().getStringExtra("targetUID"))){
            setResult(ContactsChange.ACCEPTED);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }

    //case1 button listeners
    private void cancelButton2() {
        setResult(ContactsChange.CANCELLED);
        finish();
    }

    private void case1Button1() {

        String extra = (msg.getText().toString()+"").trim();
        if(cc.sendRequest(this,getIntent().getStringExtra("myUID")
                ,getIntent().getStringExtra("targetUID"),extra)){
            setResult(ContactsChange.SENT);
            finish();
        }
        else{
            setResult(ContactsChange.ERROR);
            finish();
        }
    }


}
