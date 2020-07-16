package in.zerene.test;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Map;

public class NewMessageAdapter extends RecyclerView.Adapter<NewMessageAdapter.NewMessageViewHolder> {

    FirebaseFirestore firestore;

    private Map<String,String[]> data;
    public NewMessageAdapter(Map<String,String[]> data){
        this.data = data;
    }

    public class NewMessageViewHolder extends RecyclerView.ViewHolder{
        ImageView imgView;
        TextView textViewUsername;
        TextView textViewName;
        Button verified;
        public NewMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imageViewSSR);
            textViewUsername = itemView.findViewById(R.id.usernameViewSSR);
            textViewName = itemView.findViewById(R.id.nameViewSSR);
            verified = itemView.findViewById(R.id.verifiedSSR);
        }
    }

    @NonNull
    @Override
    public NewMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.search_sent_received_view,parent,false);
        return new NewMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewMessageViewHolder holder, int position) {
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
        firestore = FirebaseFirestore.getInstance();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChat(uid,v);
            }
        });

    }
    private void openChat(final String uid, final View view){

        Intent intent = new Intent(view.getContext(),Chat.class);
        intent.putExtra("uid",uid);
        intent.putExtra("username",data.get(uid)[0]);
        intent.putExtra("name",data.get(uid)[1]);
        intent.putExtra("verified",Boolean.parseBoolean(data.get(uid)[2]));
        view.getContext().startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
