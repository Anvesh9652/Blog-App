package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blogapp.Adapter.commentAdapter;
import com.example.blogapp.Model.Comments;
import com.example.blogapp.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Comments_Activity extends AppCompatActivity {
    private EditText commentEdit;
    private Button addComment;
    private RecyclerView comment_rv;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    private String postId;
    private String currentUserId;
    private commentAdapter adapter;
    private List<Comments> mList;
    private List<Users> usersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_comments);

        commentEdit = findViewById(R.id.make_comment);
        addComment = findViewById(R.id.add_btn);
        comment_rv = findViewById(R.id.comment_rv);

        comment_rv.setHasFixedSize(true);
        comment_rv.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mList = new ArrayList<Comments>();
        usersList = new ArrayList<>();

        adapter = new commentAdapter(Comments_Activity.this, mList, usersList);
        comment_rv.setAdapter(adapter);


        postId = getIntent().getStringExtra("postId");


        firestore.collection("Posts/" + postId + "/Comments").addSnapshotListener(Comments_Activity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        Comments comments = documentChange.getDocument().toObject(Comments.class);
                        String userId = documentChange.getDocument().getString("user");

                        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    Users users = task.getResult().toObject(Users.class);
                                    usersList.add(users);
                                    mList.add(comments);
                                    adapter.notifyDataSetChanged();
                                }
                                else {
                                    Toast.makeText(Comments_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comm = commentEdit.getText().toString();
                if (!comm.isEmpty()){
                    HashMap<String, Object> commentMap = new HashMap<>();
                    commentMap.put("comment", comm);
                    commentMap.put("time", FieldValue.serverTimestamp());
                    commentMap.put("user", currentUserId);
                    firestore.collection("Posts/" + postId + "/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Comments_Activity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Comments_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Comments_Activity.this, "Please write comment", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}