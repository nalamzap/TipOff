package in.zerene.test;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Message extends AppCompatActivity {

    DatabaseReference db;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    NewMessageAdapter newMessageAdapter;
    RecyclerView newMessageContactsContainer;

    Map<String,String[]> contacts;
    boolean generalMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        newMessageContactsContainer = findViewById(R.id.newMessageContactsContainer);
        newMessageContactsContainer.setLayoutManager(new LinearLayoutManager(this));
        contacts = new HashMap<>();
        newMessageAdapter = new NewMessageAdapter(contacts);
        newMessageContactsContainer.setAdapter(newMessageAdapter);


        db = FirebaseDatabase.getInstance().getReference(firebaseUser.getUid());
        firestore = FirebaseFirestore.getInstance();

        generalMessage=true;

    }

    public void showScrollViewNewMessage(View view){

            ScrollView scrollView = findViewById(R.id.scrollViewNewMessage);
            if (scrollView.getVisibility() == View.GONE) {
                if (generalMessage) {
                    db.child("contacts").addListenerForSingleValueEvent(valueEventListener);
                }
                db.child("close").addListenerForSingleValueEvent(valueEventListener);
                scrollView.setVisibility(View.VISIBLE);
            } else {
                contacts = new HashMap<>();
                newMessageAdapter = new NewMessageAdapter(contacts);
                newMessageContactsContainer.setAdapter(newMessageAdapter);
                scrollView.setVisibility(View.GONE);
                RecyclerView.LayoutManager layoutManager = newMessageContactsContainer.getLayoutManager();
                layoutManager.removeAllViews();

            }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    getContacts(data.getKey());
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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

                    newMessageAdapter.notifyItemInserted(contacts.size()-1);
                }

            }
        });
    }
}
