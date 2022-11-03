package com.example.blogapp.Adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blogapp.Model.Comments;
import com.example.blogapp.Model.Users;
import com.example.blogapp.R;
import com.google.firebase.firestore.auth.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.commentViewHolder> {

    private Activity context;
    private List<Comments> commentsList;
    private List<Users> usersList;

    public commentAdapter(Activity context, List<Comments> commentsList, List<Users> usersList){
        this.context = context;
        this.commentsList = commentsList;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_comment, parent, false);
        return new commentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull commentViewHolder holder, int position) {
        Comments comments = commentsList.get(position);
        holder.setmComment(comments.getComment());
        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setProfilePic(users.getImage());

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class commentViewHolder extends RecyclerView.ViewHolder{

        TextView mComment, mUserName;
        CircleImageView circleImageView;

        View mView;
        public commentViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setmComment(String comment){
            mComment = mView.findViewById(R.id.comment_tv);
            mComment.setText(comment);

        }
        public void setmUserName(String userName){
            mUserName = mView.findViewById(R.id.comment_username_tv);
            mUserName.setText(userName);
        }
        public void setProfilePic(String pic){
            circleImageView = mView.findViewById(R.id.comment_profilepic);
            Glide.with(context).load(pic).into(circleImageView);
        }
    }
}
