package in.zerene.test;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Contacts extends AppCompatActivity {

    DatabaseReference db;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    Map<String,String[]> close,contacts,sent,received,blocked;
    ContactsCloseAdapter contactsAdapter,closeAdapter;
    SentReceivedBlockedAdapter sentAdapter ,receivedAdapter ,blockedAdapter;

    RecyclerView closeContainer, contactsContainer, sentContainer,
            receivedContainer, blockedContainer;

    ScrollView viewClose, viewContacts, viewSent,
            viewReceived, viewBlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsContainer = findViewById(R.id.contactsContainer);
        contactsContainer.setLayoutManager(new LinearLayoutManager(this));

        closeContainer = findViewById(R.id.closeContainer);
        closeContainer.setLayoutManager(new LinearLayoutManager(this));

        receivedContainer = findViewById(R.id.receivedContainer);
        receivedContainer.setLayoutManager(new LinearLayoutManager(this));

        sentContainer = findViewById(R.id.sentContainer);
        sentContainer.setLayoutManager(new LinearLayoutManager(this));

        blockedContainer = findViewById(R.id.blockedContainer);
        blockedContainer.setLayoutManager(new LinearLayoutManager(this));

        viewClose = findViewById(R.id.scrollViewClose);
        viewContacts = findViewById(R.id.scrollViewContacts);
        viewSent = findViewById(R.id.scrollViewSent);
        viewReceived = findViewById(R.id.scrollViewReceived);
        viewBlocked = findViewById(R.id.scrollViewBlocked);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        contacts = new HashMap<>();
        close = new HashMap<>();
        sent = new HashMap<>();
        received = new HashMap<>();
        blocked = new HashMap<>();

        contactsAdapter = new ContactsCloseAdapter(contacts);
        closeAdapter = new ContactsCloseAdapter(close);
        sentAdapter = new SentReceivedBlockedAdapter(sent);
        receivedAdapter = new SentReceivedBlockedAdapter(received);
        blockedAdapter = new SentReceivedBlockedAdapter(blocked);

        contactsContainer.setAdapter(contactsAdapter);
        closeContainer.setAdapter(closeAdapter);
        sentContainer.setAdapter(sentAdapter);
        receivedContainer.setAdapter(receivedAdapter);
        blockedContainer.setAdapter(blockedAdapter);


        getEverything();


    }

    void getEverything(){
        db = db.child(firebaseUser.getUid());

        db.child("contacts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) getContacts(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i;
                for(i=0;i<contacts.size();i++){
                    if(contacts.keySet().toArray()[i].toString().equals(dataSnapshot.getKey()))break;
                }
                contacts.remove(dataSnapshot.getKey());

                contactsAdapter.notifyItemRemoved(i);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("close").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    getClose(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i;
                for(i=0;i<close.size();i++){
                    if(close.keySet().toArray()[i].toString().equals(dataSnapshot.getKey()))break;
                }
                close.remove(dataSnapshot.getKey());

                closeAdapter.notifyItemRemoved(i);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("sent").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    getSent(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i;
                for(i=0;i<sent.size();i++){
                    if(sent.keySet().toArray()[i].toString().equals(dataSnapshot.getKey()))break;
                }
                sent.remove(dataSnapshot.getKey());

                sentAdapter.notifyItemRemoved(i);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("received").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    getReceived(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i;
                for(i=0;i<received.size();i++){
                    if(received.keySet().toArray()[i].toString().equals(dataSnapshot.getKey()))break;
                }
                received.remove(dataSnapshot.getKey());

                receivedAdapter.notifyItemRemoved(i);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child("blocked").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    getBlocked(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int i;
                for(i=0;i<blocked.size();i++){
                    if(blocked.keySet().toArray()[i].toString().equals(dataSnapshot.getKey()))break;
                }
                blocked.remove(dataSnapshot.getKey());

                blockedAdapter.notifyItemRemoved(i);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getClose(final String uid){
        firestore.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String[] temp=new String[3];
                    temp[0]=(String)documentSnapshot.get("username");
                    temp[1]=(String)documentSnapshot.get("name");
                    temp[2]=documentSnapshot.getBoolean("verified").toString();
                    close.put(uid,temp);

                    closeAdapter.notifyItemInserted(close.size()-1);
                }

            }
        });
    }

    void getContacts(final String uid){
        firestore.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String[] temp=new String[3];
                    temp[0]=(String)documentSnapshot.get("username");
                    temp[1]=(String)documentSnapshot.get("name");
                    temp[2]=documentSnapshot.getBoolean("verified").toString();
                    contacts.put(uid,temp);

                    contactsAdapter.notifyItemInserted(contacts.size()-1);
                }

            }
        });
    }

    void getSent(final String uid){
        firestore.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String[] temp=new String[3];
                    temp[0]=(String)documentSnapshot.get("username");
                    temp[1]=(String)documentSnapshot.get("name");
                    temp[2]=documentSnapshot.getBoolean("verified").toString();
                    sent.put(uid,temp);

                    sentAdapter.notifyItemInserted(sent.size()-1);
                }

            }
        });
    }

    void getReceived(final String uid){
        firestore.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String[] temp=new String[3];
                    temp[0]=(String)documentSnapshot.get("username");
                    temp[1]=(String)documentSnapshot.get("name");
                    temp[2]=documentSnapshot.getBoolean("verified").toString();
                    received.put(uid,temp);

                    receivedAdapter.notifyItemInserted(received.size()-1);
                }

            }
        });
    }

    void getBlocked(final String uid){
        firestore.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String[] temp=new String[3];
                    temp[0]=(String)documentSnapshot.get("username");
                    temp[1]=(String)documentSnapshot.get("name");
                    temp[2]=documentSnapshot.getBoolean("verified").toString();
                    blocked.put(uid,temp);

                    blockedAdapter.notifyItemInserted(blocked.size()-1);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view){
        TextView contactLabel = findViewById(R.id.textViewContacts);
        findViewById(R.id.contacts_close).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgcolorcard,null)));
        viewClose.setVisibility(View.GONE);
        findViewById(R.id.contacts_c).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgcolorcard,null)));
        viewContacts.setVisibility(View.GONE);
        findViewById(R.id.contacts_s).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgcolorcard,null)));
        viewSent.setVisibility(View.GONE);
        findViewById(R.id.contacts_r).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgcolorcard,null)));
        viewReceived.setVisibility(View.GONE);
        findViewById(R.id.contacts_b).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bgcolorcard,null)));
        viewBlocked.setVisibility(View.GONE);
        view.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent,null)));
        switch(view.getTag().toString()){
            case "cl": viewClose.setVisibility(View.VISIBLE);
                contactLabel.setText(R.string.close_contacts);break;
            case "c": viewContacts.setVisibility(View.VISIBLE);
                contactLabel.setText(R.string.contacts);break;
            case "s": viewSent.setVisibility(View.VISIBLE);
                contactLabel.setText(R.string.request_sent);break;
            case "r": viewReceived.setVisibility(View.VISIBLE);
                contactLabel.setText(R.string.request_received);break;
            case "b": viewBlocked.setVisibility(View.VISIBLE);
                contactLabel.setText(R.string.blocked);
        }
    }

    public void searchUsers(View view){
        Intent intent = new Intent(this,Search.class);
        startActivity(intent);
    }

    public void search(View view){

    }

}
