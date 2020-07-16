package in.zerene.test;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {

    FirebaseFirestore db;
    Snackbar snack;

    
    private List<DocumentSnapshot> data;
    public ChatAdapter(List<DocumentSnapshot> data){
        this.data = data;
    }

    @Override
    public int getItemViewType(int position){
        if(Objects.equals(data.get(position).getString("sender"), FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return 0;
        }
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType==0){
            view = inflater.inflate(R.layout.message_view_me,parent,false);
            return new ViewHolderMe(view);
        }
        view = inflater.inflate(R.layout.message_view_other_user,parent,false);

        return new ViewHolderOtherUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DocumentSnapshot doc = data.get(position);
        if(Objects.equals(data.get(position).getString("sender"), FirebaseAuth.getInstance().getCurrentUser().getUid())){
            final ViewHolderMe viewHolderMe =(ViewHolderMe) holder;
            viewHolderMe.attachment.setVisibility(View.GONE);
            viewHolderMe.text.setVisibility(View.GONE);
            if(doc.getBoolean("hasText")){
                viewHolderMe.text.setText(doc.getString("text"));
                viewHolderMe.text.setVisibility(View.VISIBLE);
            }
            if(doc.getBoolean("hasImage")){
                viewHolderMe.attachment.setText(doc.getString("View Image"));
                viewHolderMe.text.setVisibility(View.VISIBLE);
            }
            else if(doc.getBoolean("hasCard")){
                viewHolderMe.text.setText(doc.getString("View Card"));
                viewHolderMe.text.setVisibility(View.VISIBLE);
            }
            else if(doc.getBoolean("hasVideo")){
                viewHolderMe.text.setText(doc.getString("View Video"));
                viewHolderMe.text.setVisibility(View.VISIBLE);
            }
            viewHolderMe.info.setTag(doc.getString("timestamp"));
            viewHolderMe.info.setVisibility(View.GONE);
            viewHolderMe.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snack = Snackbar.make(v,v.getTag().toString(),BaseTransientBottomBar.LENGTH_INDEFINITE);
                    snack
                            .setDuration(3000)
                            .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
            viewHolderMe.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolderMe.info.getVisibility()==View.GONE)
                        viewHolderMe.info.setVisibility(View.VISIBLE);
                    else
                        viewHolderMe.info.setVisibility(View.GONE);
                }
            });


        }
        else{
            final ViewHolderOtherUser viewHolderOtherUser =(ViewHolderOtherUser) holder;
            if(doc.getBoolean("hasText")){
                viewHolderOtherUser.text.setText(doc.getString("text"));
                viewHolderOtherUser.text.setVisibility(View.VISIBLE);
            }
            if(doc.getBoolean("hasImage")){
                viewHolderOtherUser.attachment.setText(doc.getString("View Image"));
                viewHolderOtherUser.text.setVisibility(View.VISIBLE);
            }
            else if(doc.getBoolean("hasCard")){
                viewHolderOtherUser.text.setText(doc.getString("View Card"));
                viewHolderOtherUser.text.setVisibility(View.VISIBLE);
            }
            else if(doc.getBoolean("hasVideo")){
                viewHolderOtherUser.text.setText(doc.getString("View Video"));
                viewHolderOtherUser.text.setVisibility(View.VISIBLE);
            }
            viewHolderOtherUser.info.setTag(doc.getString("timestamp"));
            viewHolderOtherUser.info.setVisibility(View.GONE);
            viewHolderOtherUser.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snack = Snackbar.make(v,v.getTag().toString(),BaseTransientBottomBar.LENGTH_INDEFINITE);
                    snack.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
            viewHolderOtherUser.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolderOtherUser.info.getVisibility()==View.GONE)
                        viewHolderOtherUser.info.setVisibility(View.VISIBLE);
                    else
                        viewHolderOtherUser.info.setVisibility(View.GONE);
                }
            });
            viewHolderOtherUser.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus)
                        viewHolderOtherUser.info.setVisibility(View.GONE);
                    snack.dismiss();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolderMe extends RecyclerView.ViewHolder{

        TextView text;
        Button info;
        Button attachment;
        public ViewHolderMe(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.info);
            attachment = itemView.findViewById(R.id.attachment);
            text = itemView.findViewById(R.id.textViewChatMessage);
        }
    }

    class ViewHolderOtherUser extends RecyclerView.ViewHolder{

        TextView text;
        Button info;
        Button attachment;
        public ViewHolderOtherUser(@NonNull View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.info);
            attachment = itemView.findViewById(R.id.attachment);
            text = itemView.findViewById(R.id.textViewChatMessage);
        }
    }

}
