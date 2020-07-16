package in.zerene.test;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Map;

public class SentReceivedBlockedAdapter extends RecyclerView.Adapter<SentReceivedBlockedAdapter.SentReceivedBlockedViewHolder> {

    FirebaseFirestore db;

    private Map<String,String[]> data;
    public SentReceivedBlockedAdapter(Map<String,String[]> data){
        this.data = data;
    }

    public class SentReceivedBlockedViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView;
        TextView textViewUsername;
        TextView textViewName;
        Button verified;
        public SentReceivedBlockedViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imageViewSSR);
            textViewUsername = itemView.findViewById(R.id.usernameViewSSR);
            textViewName = itemView.findViewById(R.id.nameViewSSR);
            verified = itemView.findViewById(R.id.verifiedSSR);
        }
    }

    @NonNull
    @Override
    public SentReceivedBlockedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.search_sent_received_view,parent,false);
        return new SentReceivedBlockedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SentReceivedBlockedViewHolder holder, int position) {
        final String uid = data.keySet().toArray()[position].toString();
        final ImageView img = holder.imgView;
        FirebaseStorage.getInstance().getReference("users/"+uid+"/profilePic.jpg")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(img.getContext())
                        .load(uri) // the uri you got from Firebase
                        .into(img);
            }
        });
        String username = data.get(uid)[0];
        String name = data.get(uid)[1];
        boolean V = Boolean.parseBoolean(data.get(uid)[2]);
        holder.textViewUsername.setText(username);
        holder.textViewName.setText(name);
        if(V)holder.verified.setVisibility(View.VISIBLE);
        holder.itemView.setTag(uid);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccount(uid,v);
            }
        });

    }
    private void openAccount(final String uid, final View view){
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String username = documentSnapshot.getString("username");
                        String name = documentSnapshot.getString("name");
                        boolean v = documentSnapshot.getBoolean("verified");
                        getUserExtras(uid,username,name,v,view);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,
                                "Something went wrong!\nTry reopening app",
                                BaseTransientBottomBar.LENGTH_LONG).show();

                    }
                });
    }

    private void getUserExtras(final String uid,final String username, final String name,final boolean v,final View view) {
        db.collection("userExtras").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        OpenAccount openAccount = new OpenAccount(
                                view.getContext(),
                                Account.class,
                                uid,
                                username,
                                name,
                                documentSnapshot.getString("about"),
                                documentSnapshot.getString("hobbies"),
                                documentSnapshot.getLong("gender"),
                                v);
                        Intent intent = openAccount.getIntent();
                        view.getContext().startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view,
                                "Something went wrong!\nTry reopening app",
                                BaseTransientBottomBar.LENGTH_LONG).show();

                    }
                });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
