package com.example.blogapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blogapp.Comments_Activity;
import com.example.blogapp.Model.Post;
import com.example.blogapp.Model.Users;
import com.example.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class postAdapter extends RecyclerView.Adapter<postAdapter.PostViewHolder> {

    private List<Post> mlist;
    private List<Users> usersList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public postAdapter(Activity context, List<Post> mlist, List<Users> usersList){
        this.mlist = mlist;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_post, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = mlist.get(position);
        holder.setPostpic(post.getImage());
        holder.setPostCaption(post.getCaption());
        holder.setDescription(post.getDescription());

        long milliseconds = post.getTime().getTime();
        String date = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setPostDate(date);



        String username = usersList.get(position).getName();
        String image = usersList.get(position).getImage();
        holder.setProfilepic(image);
        holder.setPostUsername(username);




        //Like pic
        String postId = post.postId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.likepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            HashMap<String, Object> likemap = new HashMap<>();
                            likemap.put("timestamp", FieldValue.serverTimestamp());
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).set(likemap);
                        }
                        else {
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });
        // Likes color change
        firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
              if (error == null){
                  if (value.exists()){
                      holder.likepic.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_favorite_24));
                  }
                  else {
                      holder.likepic.setImageDrawable(context.getDrawable(R.drawable.before_liked));
                  }
              }
            }
        });
        //Like count
        firestore.collection("Posts/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (!value.isEmpty()){
                       int count = value.size();
                       holder.setPostLikes(count);
                    }
                    else {
                        holder.setPostLikes(0);
                    }
                }
            }
        });

        //comments implementation
        holder.commentspic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentInt = new Intent(context, Comments_Activity.class);
                commentInt.putExtra("postId", postId);
                context.startActivity(commentInt);
            }
        });

        if (currentUserId.equals(post.getUser())) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setClickable(true);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete").setMessage("Are Your Sure").setNegativeButton("No", null).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firestore.collection("Posts/" + postId + "/Comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                                        firestore.collection("Posts/" + postId + "/Comments").document(snapshot.getId()).delete();
                                    }
                                }
                            });
                            firestore.collection("Posts/" + postId + "/Likes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                                        firestore.collection("Posts/" + postId + "/Likes").document(snapshot.getId()).delete();
                                    }
                                }
                            });
                            firestore.collection("Posts").document(postId).delete();
                            mlist.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        ImageView postpic, commentspic, likepic;
        CircleImageView profilepic;
        TextView postUsername, postDate, postCaption, postLikes, post_description;
        ImageButton delete;
        View mview;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            likepic = mview.findViewById(R.id.like_btn);
            commentspic = mview.findViewById(R.id.comments_post);
            delete = mview.findViewById(R.id.delete_btn);
        }
        public void setPostLikes(int count) {
            postLikes = mview.findViewById(R.id.like_count_tv);
            postLikes.setText(count + " Likes");
        }

        public void setPostpic(String urlPost){
            postpic = mview.findViewById(R.id.user_post);
            Glide.with(context).load(urlPost).into(postpic);
        }
        public void setProfilepic(String urlProfile){
            profilepic = mview.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilepic);
        }
        public void setPostUsername(String username){
            postUsername = mview.findViewById(R.id.comment_username_tv);
            postUsername.setText(username);
        }
        public void setPostDate(String date){
            postDate = mview.findViewById(R.id.date_tv);
            postDate.setText(date);
        }
        public void setDescription(String description){
            post_description = mview.findViewById(R.id.p_description);
            post_description.setText(description);
            post_description.setMovementMethod(new ScrollingMovementMethod());

            post_description.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
            });
            post_description.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }
        public void setPostCaption(String caption){
            postCaption = mview.findViewById(R.id.caption_tv);
            postCaption.setText(caption);
        }

    }
}
