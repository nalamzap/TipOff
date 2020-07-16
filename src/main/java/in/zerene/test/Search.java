package in.zerene.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Vector;

public class Search extends AppCompatActivity {

    FirebaseFirestore db;
    EditText searchBox;
    LinearLayout linearLayout;
    Query q1,q2,q3,q4,q5;
    DocumentSnapshot dsq1,dsq2,dsq3,dsq4,dsq5;
    Vector<String> uids;
    Switch switchEmail;
    Button su,nextQ;
    String previousSearch;
    int n;
    boolean switchOn,q4Flag=false,q1C,q2C,q3C,q4C,q5C;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBox = findViewById(R.id.editTextSearch);
        linearLayout = findViewById(R.id.linearLayoutSearch);
        switchEmail = findViewById(R.id.switchEmailSearch);
        su = findViewById(R.id.su);
        nextQ = findViewById(R.id.nextQ);
        nextQ.setVisibility(View.INVISIBLE);
        switchOn = false;
        previousSearch ="";

        dsq1 = null;dsq2 = null;dsq3 = null;dsq4 = null;dsq5 = null;

        su.setOnClickListener(searchClickListener);

        switchEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!switchOn) && switchEmail.isChecked()){
                    switchOn = true;
                    previousSearch = "";
                    uids.removeAllElements();
                    linearLayout.removeAllViews();
                    showAd();
                    su.setOnClickListener(searchClickListener2);
                }
                else if(switchOn && (!switchEmail.isChecked())){
                    uids.removeAllElements();
                    previousSearch = "";
                    linearLayout.removeAllViews();
                    switchOn = false;
                    su.setOnClickListener(searchClickListener);
                }
            }
        });

        nextQ.setOnClickListener(nextQ_ClickListener);
        db= FirebaseFirestore.getInstance();
        uids = new Vector<>();
    }

    private void showAd() {
        //code to show ad

    }

    View.OnClickListener nextQ_ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nextSearch();
        }
    };

    private void nextSearch() {
        n = uids.size();

        if(dsq1!=null)
        q1.startAfter(dsq1).limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        dsq1 = documentSnapshot;
                        if (!uids.contains(documentSnapshot.getId())) {
                            uids.add(documentSnapshot.getId());
                            newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                    documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                        }
                    }
                    q1C=true;
                    if(q2C&&q3C&&q4C&&q5C){
                        q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                        if(n==uids.size()){
                            Snackbar.make(findViewById(R.id.clSearch),
                                    "End of search!",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                            nextQ.setVisibility(View.INVISIBLE);
                        }
                        else if(uids.size()<25)nextSearch();
                    }
                }
                else{
                    q1C=true;
                    if(q2C&&q3C&&q4C&&q5C){
                        q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                        if(n==uids.size()){
                            Snackbar.make(findViewById(R.id.clSearch),
                                    "End of search!",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                            nextQ.setVisibility(View.INVISIBLE);
                        }
                        else if(uids.size()<25)nextSearch();
                    }

                }
            }
        });
        else{
            q1C=true;
            if(q2C&&q3C&&q4C&&q5C){
                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                if(n==uids.size()){
                    Snackbar.make(findViewById(R.id.clSearch),
                            "End of search!",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                    nextQ.setVisibility(View.INVISIBLE);
                }
                else if(uids.size()<25)nextSearch();
            }

        }


        if(dsq2!=null)
        q2.startAfter(dsq2).limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        dsq2 = documentSnapshot;
                        if (!uids.contains(documentSnapshot.getId())) {
                            uids.add(documentSnapshot.getId());
                            newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                    documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                        }
                    }
                    q2C=true;
                    if(q1C&&q3C&&q4C&&q5C){
                        q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                        if(n==uids.size()){
                            Snackbar.make(findViewById(R.id.clSearch),
                                    "End of search!",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                            nextQ.setVisibility(View.INVISIBLE);
                        }
                        else if(uids.size()<25)nextSearch();
                    }
                }
                else{
                    q2C=true;
                    if(q1C&&q3C&&q4C&&q5C){
                        q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                        if(n==uids.size()){
                            Snackbar.make(findViewById(R.id.clSearch),
                                    "End of search!",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                            nextQ.setVisibility(View.INVISIBLE);
                        }
                        else if(uids.size()<25)nextSearch();
                    }

                }
            }
        });
        else{
            q2C=true;
            if(q1C&&q3C&&q4C&&q5C){
                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                if(n==uids.size()){
                    Snackbar.make(findViewById(R.id.clSearch),
                            "End of search!",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                    nextQ.setVisibility(View.INVISIBLE);
                }
                else if(uids.size()<25)nextSearch();
            }

        }

        if(dsq3!=null)
        q3.startAfter(dsq3).limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        dsq3 = documentSnapshot;
                        if (!uids.contains(documentSnapshot.getId())) {
                            uids.add(documentSnapshot.getId());
                            newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                    documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                        }
                    }
                    q3C=true;
                    if(q1C&&q2C&&q4C&&q5C){
                        q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                        if(n==uids.size()){
                            Snackbar.make(findViewById(R.id.clSearch),
                                    "End of search!",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                            nextQ.setVisibility(View.INVISIBLE);
                        }
                        else if(uids.size()<25)nextSearch();
                    }
                }
                else{
                    q3C=true;
                    if(q2C&&q1C&&q4C&&q5C){
                        q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                        if(n==uids.size()){
                            Snackbar.make(findViewById(R.id.clSearch),
                                    "End of search!",
                                    BaseTransientBottomBar.LENGTH_LONG).show();
                            nextQ.setVisibility(View.INVISIBLE);
                        }
                        else if(uids.size()<25)nextSearch();
                    }

                }
            }
        });
        else{
            q3C=true;
            if(q2C&&q1C&&q4C&&q5C){
                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                if(n==uids.size()){
                    Snackbar.make(findViewById(R.id.clSearch),
                            "End of search!",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                    nextQ.setVisibility(View.INVISIBLE);
                }
                else if(uids.size()<25)nextSearch();
            }

        }


        if(q4Flag){
            if(dsq4!=null)
            q4.startAfter(dsq4).limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            dsq4 = documentSnapshot;
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        }
                        q4C=true;
                        if(q1C&&q2C&&q3C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(n==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "End of search!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()<25)nextSearch();
                        }
                    }
                    else{
                        q4C=true;
                        if(q2C&&q3C&&q1C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(n==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "End of search!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()<25)nextSearch();
                        }

                    }
                }
            });
            else{
                q4C=true;
                if(q2C&&q3C&&q1C&&q5C){
                    q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                    if(n==uids.size()){
                        Snackbar.make(findViewById(R.id.clSearch),
                                "End of search!",
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        nextQ.setVisibility(View.INVISIBLE);
                    }
                    else if(uids.size()<25)nextSearch();
                }

            }

            if(dsq5!=null)
            q5.startAfter(dsq5).limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            dsq5 = documentSnapshot;
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        }
                        q5C=true;
                        if(q1C&&q2C&&q3C&&q4C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(n==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "End of search!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()<25)nextSearch();
                        }
                    }
                    else{
                        q5C=true;
                        if(q2C&&q3C&&q4C&&q1C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(n==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "End of search!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()<25)nextSearch();
                        }

                    }
                }
            });
            else{
                q5C=true;
                if(q2C&&q3C&&q4C&&q1C){
                    q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                    if(n==uids.size()){
                        Snackbar.make(findViewById(R.id.clSearch),
                                "End of search!",
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        nextQ.setVisibility(View.INVISIBLE);
                    }
                    else if(uids.size()<25)nextSearch();
                }

            }
        }
        else{
            q4C=true;q5C=true;
            if(q1C&&q2C&&q3C){
                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                if(n==uids.size()){
                    Snackbar.make(findViewById(R.id.clSearch),
                            "End of search!",
                            BaseTransientBottomBar.LENGTH_LONG).show();
                    nextQ.setVisibility(View.INVISIBLE);
                }
                else if(uids.size()<25)nextSearch();
            }
        }

    }

    View.OnClickListener searchClickListener2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            suClick2();
        }
    };

    View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            suClick();
        }
    };

    private void suClick2(){
        String searchWord = searchBox.getText().toString().trim();
        if(!previousSearch.equals(searchWord)){
            nextQ.setVisibility(View.INVISIBLE);
            previousSearch = searchWord;
            uids.removeAllElements();
            if (Patterns.EMAIL_ADDRESS.matcher(searchWord).matches()) {
                linearLayout.removeAllViews();
                uids.removeAllElements();
                FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("email", searchWord)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        } else
                            Toast.makeText(Search.this, "No users found!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                searchBox.setError("Not a valid email");
            }
        }
    }

    public void suClick(){
        q1C=false;q2C=false;q3C=false;q4C=false;q5C=false;
        String searchWord = searchBox.getText().toString().trim().toLowerCase();
        if(!previousSearch.equals(searchWord)) {
            nextQ.setVisibility(View.INVISIBLE);
            previousSearch = searchWord;
            uids.removeAllElements();
            linearLayout.removeAllViews();
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("username", searchWord)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        if (!uids.contains(documentSnapshot.getId())) {
                            uids.add(documentSnapshot.getId());
                            newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                    documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                        }

                    }
                }
            });
            q1 = FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("namesmall", searchWord);
            q1.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            dsq1 = documentSnapshot;
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }

                        }
                        q1C=true;
                        if(q2C&&q3C&&q4C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(0==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "No users found!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                            else nextSearch();
                        }
                    }
                    else{
                        q1C=true;
                        if(q2C&&q3C&&q4C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(0==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "No users found!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                }
            });
            q2 = FirebaseFirestore.getInstance().collection("users")
                    .whereGreaterThan("namesmall", searchWord)
                    .whereLessThan("namesmall", searchWord + "\uf8ff");

            q2.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            dsq2 = documentSnapshot;
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        }
                        q2C=true;
                        if(q1C&&q3C&&q4C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(0==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "No users found!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                            else nextSearch();
                        }
                    }
                    else{
                        q2C=true;
                        if(q1C&&q3C&&q4C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(0==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "No users found!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                }
            });

            q3 = FirebaseFirestore.getInstance().collection("users")
                    .whereGreaterThan("username", searchWord)
                    .whereLessThan("username", searchWord + "\uf8ff");
            q3.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            dsq3 = documentSnapshot;
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        }
                        q3C=true;
                        if(q1C&&q2C&&q4C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(0==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "No users found!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                            else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                            else nextSearch();
                        }
                    }
                    else{
                        q3C=true;
                        if(q2C&&q1C&&q4C&&q5C){
                            q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                            if(0==uids.size()){
                                Snackbar.make(findViewById(R.id.clSearch),
                                        "No users found!",
                                        BaseTransientBottomBar.LENGTH_LONG).show();
                                nextQ.setVisibility(View.INVISIBLE);
                            }
                        }

                    }
                }
            });
            String temp;
            temp = searchWord.split(" ")[0];
            if (!temp.equals(searchWord)) {
                q4Flag=true;
                FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("username", temp)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        }
                    }
                });
                q4 = FirebaseFirestore.getInstance().collection("users")
                        .whereGreaterThan("username", temp)
                        .whereLessThan("username", temp+ "\uf8ff");
                q4.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                dsq4 = documentSnapshot;
                                if (!uids.contains(documentSnapshot.getId())) {
                                    uids.add(documentSnapshot.getId());
                                    newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                            documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                                }
                            }
                            q4C=true;
                            if(q1C&&q2C&&q3C&&q5C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                                else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                                else nextSearch();
                            }
                        }
                        else{
                            q4C=true;
                            if(q2C&&q3C&&q1C&&q5C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    }
                });
                q5 = FirebaseFirestore.getInstance().collection("users")
                        .whereGreaterThanOrEqualTo("username", searchWord.split(" ")[1])
                        .whereLessThan("username", searchWord.split(" ")[1] + "\uf8ff");
                q5.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                dsq5 = documentSnapshot;
                                if (!uids.contains(documentSnapshot.getId())) {
                                    uids.add(documentSnapshot.getId());
                                    newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                            documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                                }
                            }
                            q5C=true;
                            if(q1C&&q2C&&q3C&&q4C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                                else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                                else nextSearch();
                            }
                        }
                        else{
                            q5C=true;
                            if(q2C&&q3C&&q4C&&q1C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    }
                });
            } else if (temp.length() > 2) {
                q4Flag=true;
                String t = temp.substring(0, temp.length() - 1);
                FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("username", t)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            if (!uids.contains(documentSnapshot.getId())) {
                                uids.add(documentSnapshot.getId());
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                        }
                    }
                });
                q4 = FirebaseFirestore.getInstance().collection("users")
                        .whereGreaterThan("username", t)
                        .whereLessThan("username", searchWord);
                q4.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                dsq4 = documentSnapshot;
                                if (!uids.contains(documentSnapshot.getId())) {
                                    uids.add(documentSnapshot.getId());
                                    newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                            documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                                }
                            }
                            q4C=true;
                            if(q1C&&q2C&&q3C&&q5C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                                else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                                else nextSearch();
                            }
                        }
                        else{
                            q4C=true;
                            if(q2C&&q3C&&q1C&&q5C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    }
                });
                t = temp.substring(1);
                q5 = FirebaseFirestore.getInstance().collection("users")
                        .whereGreaterThanOrEqualTo("username", t)
                        .whereLessThan("username", t + "\uf8ff");
                q5.limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                dsq5 = documentSnapshot;
                                newSearchView(documentSnapshot.getId(), documentSnapshot.get("username").toString(),
                                        documentSnapshot.get("name").toString(), documentSnapshot.getBoolean("verified"));
                            }
                            q5C=true;
                            if(q1C&&q2C&&q3C&&q4C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                                else if(uids.size()>25)nextQ.setVisibility(View.VISIBLE);
                                else nextSearch();
                            }
                        }
                        else{
                            q5C=true;
                            if(q2C&&q3C&&q4C&&q1C){
                                q1C = false;q2C = false;q3C = false;q4C = false;q5C = false;
                                if(0==uids.size()){
                                    Snackbar.make(findViewById(R.id.clSearch),
                                            "No users found!",
                                            BaseTransientBottomBar.LENGTH_LONG).show();
                                    nextQ.setVisibility(View.INVISIBLE);
                                }
                            }

                        }
                    }
                });
            }
            else q4Flag= false;
        }
    }

    private void newSearchView(String uid, String username, String name, boolean verified){
        View view = getLayoutInflater().inflate(R.layout.search_sent_received_view,null);
        TextView text =view.findViewById(R.id.usernameViewSSR);
        text.setText(username);
        text =view.findViewById(R.id.nameViewSSR);
        text.setText(name);
        Button v = view.findViewById(R.id.verifiedSSR);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Search.this,"Verified Account",Toast.LENGTH_SHORT).show();
            }
        });
        if(verified)v.setVisibility(View.VISIBLE);
        view.setTag(uid);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccount(v.getTag().toString());
            }
        });
        final ImageView profile = view.findViewById(R.id.imageViewSSR);
        FirebaseStorage.getInstance().getReference("users").child(uid).child("profilePic.jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(Search.this)
                        .load(uri) // the uri you got from Firebase
                        .into(profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        linearLayout.addView(view);
    }

    private void openAccount(final String uid){
        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String name = documentSnapshot.getString("name");
                        boolean v = documentSnapshot.getBoolean("verified");
                        getUserExtras(uid,username,name,v);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.clSearch),
                                "Something went wrong!\nTry reopening app",
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }
                        }, 3000);

                    }
                });
    }

    private void getUserExtras(final String uid,final String username, final String name,final boolean v) {
        db.collection("userExtras").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        OpenAccount openAccount = new OpenAccount(
                                Search.this,
                                Account.class,
                                uid,
                                username,
                                name,
                                documentSnapshot.getString("about"),
                                documentSnapshot.getString("hobbies"),
                                documentSnapshot.getLong("gender"),
                                v);
                        Intent intent = openAccount.getIntent();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.clSearch),
                                "Something went wrong!\nTry reopening app",
                                BaseTransientBottomBar.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                finish();
                            }
                        }, 3000);

                    }
                });

    }
}
