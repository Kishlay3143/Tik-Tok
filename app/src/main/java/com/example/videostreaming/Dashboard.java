package com.example.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    FloatingActionButton addVideo;
    RecyclerView recView;
    Boolean testClick =false;
    DatabaseReference likeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   // Hide Status Bar
        addVideo=findViewById(R.id.addVideo);
        recView=findViewById(R.id.recView);


        likeReference=FirebaseDatabase.getInstance().getReference("likes");

        recView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<fileModel> options =
                new FirebaseRecyclerOptions.Builder<fileModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("myVideos"), fileModel.class)
                        .build();

        FirebaseRecyclerAdapter<fileModel,myViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<fileModel, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final myViewHolder holder, final int position, @NonNull final fileModel model) {
                holder.prepareExoplayer(getApplication(),model.getTitle(),model.getvUrl());

                //Taking userId of loggedIn user and and videoId of the video which we are going to like
                FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                String userId=firebaseUser.getUid();
                String postKey=getRef(position).getKey();

                holder.likeButtonStatus(postKey,userId);

                holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        testClick=true;

                        likeReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                if (testClick==true){
                                    if (snapshot.child(postKey).hasChild(userId)){
                                        likeReference.child(postKey).removeValue();
                                        testClick=false;
                                    }
                                    else {
                                        likeReference.child(postKey).child(userId).setValue(true);
                                        testClick=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull final DatabaseError error) {

                            }
                        });
                    }
                });

                holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Intent intent=new Intent(getApplicationContext(),CommentPanel.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
                return new myViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recView.setAdapter(firebaseRecyclerAdapter);

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(getApplicationContext(),AddVideo.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.app_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout: FirebaseAuth.getInstance().signOut();
                              startActivity(new Intent(Dashboard.this,Login.class));
                              finish();
                              break;

            case R.id.manageProfile: startActivity(new Intent(Dashboard.this,UpdateProfile.class));
                                     break;

        }
        return super.onOptionsItemSelected(item);
    }
}