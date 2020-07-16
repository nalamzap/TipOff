package in.zerene.test;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Chat extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    Query queryTop;
    List<DocumentSnapshot> docList;
    ChatAdapter chatAdapter;

    TextView usernameTextView,nameTextView;
    Button verifiedButton, attach,send;
    ImageView imageViewDP;
    EditText messageBox;
    ConstraintLayout messageBoxCL;
    RecyclerView recyclerViewChat;

    String cUID,username,name,meUID,messageLocID,
            text;
    boolean verified,
            isLoading=false,
            hasImage=false,hasVideo=false,hasCard=false,hasText=false;
    DocumentSnapshot lastDoc,lastAddedDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        usernameTextView = findViewById(R.id.usernameChat);
        nameTextView = findViewById(R.id.nameChat);
        verifiedButton = findViewById(R.id.verified_chat);
        imageViewDP = findViewById(R.id.imageViewDPChat);
        attach = findViewById(R.id.attach_button_chat);
        send = findViewById(R.id.send_chat);
        messageBoxCL = findViewById(R.id.cl_message_chat);


        docList = new ArrayList<>();

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(docList);
        recyclerViewChat.setAdapter(chatAdapter);


        meUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        cUID = getIntent().getStringExtra("uid");
        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        verified = getIntent().getBooleanExtra("verified",false);

        usernameTextView.setText(username);
        nameTextView.setText(name);
        if(verified)verifiedButton.setVisibility(View.VISIBLE);

        messageBox = findViewById(R.id.message_editText_chat);
        messageBox.addTextChangedListener(messageBoxWatcher);
        messageBox.clearFocus();

        getMessageLocID();

    }

    private void getdp(){
        firebaseStorage.getReference("users").child(cUID).child("profilePic.jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(Chat.this)
                        .load(uri) // the uri you got from Firebase
                        .into(imageViewDP);
            }
        });
    }

    private void getMessageLocID(){

        firestore.collection("messages")
                .whereEqualTo("users",cUID+meUID)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    getMessageLocID2();
                }
                else{

                    messageLocID = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMessages();
                }
            }
        });
    }
    private void getMessageLocID2(){
            firestore.collection("messages")
                    .whereEqualTo("users",meUID+cUID)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){

                        createMessageCollectionDocument();

                    }
                    else{

                        messageLocID = queryDocumentSnapshots.getDocuments().get(0).getId();

                        getMessages();
                    }
                }
            });
    }

    private void getMessages() {
        queryTop = firestore.collection("messages").document(messageLocID).collection("messages").orderBy("sec", Query.Direction.DESCENDING);

        queryTop.limit(100).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    for (int i =0;i<queryDocumentSnapshots.size();i++){
                        docList.add(0,queryDocumentSnapshots.getDocuments().get(i));
                        chatAdapter.notifyItemInserted(0);
                    }
                    lastAddedDoc = queryDocumentSnapshots.getDocuments().get(0);
                    lastDoc = docList.get(0);

                    recyclerViewChat.smoothScrollToPosition(docList.size()-1);
                    addListener();
                    getdp();
                    recyclerViewChat.addOnScrollListener(scrollListener);
                } else {
                    Toast.makeText(Chat.this, "You haven't started a chat yet !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addListener(){

        if(lastAddedDoc != null)
            firestore.collection("messages").document(messageLocID)
                    .collection("messages").orderBy("sec", Query.Direction.ASCENDING)
                    .limitToLast(1).startAfter(lastAddedDoc).addSnapshotListener(eventListener);
        else
            firestore.collection("messages").document(messageLocID)
                    .collection("messages").orderBy("sec", Query.Direction.ASCENDING)
                    .limit(1).addSnapshotListener(eventListener);
        /*firestore.collection("messages").document(messageLocID)
                .collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

            }
        });*/
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            recyclerViewChat.requestFocus();
            messageBox.requestFocus();
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(!isLoading){
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewChat.getLayoutManager();
                assert linearLayoutManager != null;
                if(linearLayoutManager.findFirstVisibleItemPosition()<20){
                    loadMore();
                    isLoading=true;
                }
            }
        }
    };

    EventListener eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            assert queryDocumentSnapshots != null;
            if(!queryDocumentSnapshots.isEmpty()){
                docList.add(queryDocumentSnapshots.getDocuments().get(0));

                lastAddedDoc = queryDocumentSnapshots.getDocuments().get(0);
                chatAdapter.notifyItemInserted(docList.size()-1);
                recyclerViewChat.smoothScrollToPosition(docList.size()-1);

            }
        }
    };

    public void loadMore(){
        queryTop.startAfter(lastDoc).limit(100).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0){
                    for(int i=0;i<queryDocumentSnapshots.size();i++){
                        docList.add(0,queryDocumentSnapshots.getDocuments().get(i));
                        lastDoc = docList.get(0);
                        chatAdapter.notifyItemInserted(0);
                    }

                    isLoading = false;
                }
                else{
                    Toast.makeText(Chat.this, "You have reached the end of the road 😁", Toast.LENGTH_LONG).show();
                    isLoading = true;
                }

            }
        });
    }

    private void createMessageCollectionDocument(){
        Map<String,String> doc = new HashMap<>();
        doc.put("users", meUID+cUID);
        firestore.collection("messages").add(doc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        messageLocID = documentReference.getId();
                        getdp();
                        addListener();
                    }
                });


    }

    TextWatcher messageBoxWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()>0){
                attach.setVisibility(View.GONE);
                send.setVisibility(View.VISIBLE);
                hasText = true;
                text = s.toString();
            }
            else{
                attach.setVisibility(View.VISIBLE);
                send.setVisibility(View.GONE);
                hasText = false;
            }
        }
    };

    public void send(View view){
        Map<String,Object> doc = new HashMap<>();
        Calendar cal= Calendar.getInstance();
        Timestamp timestamp = Timestamp.now();
        cal.setTime(timestamp.toDate());

        String time = timestamp.toDate().toString();
        String[] temp = time.split(" ");
        String t = temp[3];
        int h = Integer.parseInt(t.split(":")[0]);
        String m = t.split(":")[1];
        String s = t.split(":")[2];
        if(h>12){
            h%=12;
            t=" pm";
        }else if(h==0){
            h=12;
            t=" am";
        }else t=" am";

        t = h + ":" + m + ":" + s + t;
        t = t + "  " + temp[1] + " " + temp[2] + ", " + temp[5] + " (" + temp[0] + ")";
        long sec = timestamp.getSeconds();

        doc.put("timestamp",t);
        doc.put("sec",sec);
        doc.put("hasText",hasText);
        if(hasText){
            doc.put("text",text);
        }
        messageBox.setText("");
        doc.put("hasImage",hasImage);
        doc.put("hasCard",hasCard);
        doc.put("hasVideo",hasVideo);
        doc.put("sender",meUID);





        firestore.collection("messages").document(messageLocID)
                .collection("messages").add(doc)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Chat.this, "Message sending failed\nTry again", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
