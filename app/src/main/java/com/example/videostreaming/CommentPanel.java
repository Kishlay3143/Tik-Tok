package com.example.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class CommentPanel extends AppCompatActivity {

    EditText commentText;
    Button commentSubmit;
    DatabaseReference userRef,commentRef;
    String postKey;
    RecyclerView commentRecview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_panel);
        commentText=findViewById(R.id.commentText);
        commentSubmit=findViewById(R.id.commentSubmit);
        commentRecview=findViewById(R.id.commentRecview);

        commentRecview.setLayoutManager(new LinearLayoutManager(this));


        postKey=getIntent().getStringExtra("postKey");

        userRef= FirebaseDatabase.getInstance().getReference().child("userProfile");
        commentRef=FirebaseDatabase.getInstance().getReference().child("myVideos").child(postKey).child("comments");

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String userId=user.getUid();

        commentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String userName=snapshot.child("uName").getValue().toString();
                            String userImage=snapshot.child("uImage").getValue().toString();
                            processComment(userName,userImage);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError error) {

                    }
                });
            }

            private void processComment(String userName,String userImage){
                String commentPost=commentText.getText().toString();
                String randomPostKey=userId+""+new Random().nextInt(1000);

                Calendar dateValue=Calendar.getInstance();
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mm-yy");
                String cDate=dateFormat.format(dateValue.getTime());

                SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
                String cTime=timeFormat.format(dateValue.getTime());

                HashMap cmnt=new HashMap();
                cmnt.put("uId",userId);
                cmnt.put("userName",userName);
                cmnt.put("userImage",userImage);
                cmnt.put("userMsg",commentPost);
                cmnt.put("date",cDate);
                cmnt.put("time",cTime);

                commentRef.child(randomPostKey).updateChildren(cmnt).
                        addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull final Task task) {
                                if (task.isSuccessful())
                                    Toast.makeText(CommentPanel.this, "Comment Added", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(CommentPanel.this, task.toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull final Exception e) {

                            }
                        });


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(commentRef, CommentModel.class)
                        .build();

        FirebaseRecyclerAdapter<CommentModel,commentViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<CommentModel, commentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final commentViewHolder holder, final int position, @NonNull final CommentModel model) {
                holder.cuName.setText(model.getUserName());
                holder.cuMessage.setText(model.getUserMsg());
                holder.cuDate.setText("Date :"+model.getDate()+" Time :"+model.getTime());
                Glide.with(holder.cuImage.getContext()).load(model.getUserImage()).into(holder.cuImage);
            }

            @NonNull
            @Override
            public commentViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_single_row,parent,false);
                return new commentViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        commentRecview.setAdapter(firebaseRecyclerAdapter);

    }
}