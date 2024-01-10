package com.example.josechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.josechat.ChatActivity;
import com.example.josechat.R;
import com.example.josechat.Utils.AndroidUtil;
import com.example.josechat.Utils.FirebaseUtil;
import com.example.josechat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
         holder.usernameText.setText(model.getUserName());
         holder.phoneText.setText(model.getPhone());

         if(model.getUserId().equals(FirebaseUtil.currentUserID())){
             holder.usernameText.setText(model.getUserName()+" (Me)");
         }

        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
            if(t.isSuccessful()){
                Uri uri =t.getResult();
                AndroidUtil.setProfilePic(context, uri, holder.profilePic);
            }
        });

         holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
             AndroidUtil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
         });

    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sear_user_recycler_row,parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText=itemView.findViewById(R.id.user_name_text);
            phoneText=itemView.findViewById(R.id.phone_text);
            profilePic=itemView.findViewById(R.id.profile_pìc_image_view);

        }
    }
}
