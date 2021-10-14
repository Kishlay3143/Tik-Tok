package com.example.videostreaming;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myViewHolder extends RecyclerView.ViewHolder {

    SimpleExoPlayerView simpleExoplayerView;
    SimpleExoPlayer simpleExoplayer;
    TextView vTitleView,likeTxt;
    ImageView likeBtn;
    ImageView commentBtn;
    DatabaseReference likeReference;

    public myViewHolder(@NonNull final View itemView) {
        super(itemView);
        vTitleView=itemView.findViewById(R.id.vTitleView);
        simpleExoplayerView=itemView.findViewById(R.id.exoplayerView);
        likeBtn=itemView.findViewById(R.id.likeBtn);
        likeTxt=itemView.findViewById(R.id.likeTxt);
        commentBtn=itemView.findViewById(R.id.commentBtn);
    }
    public void likeButtonStatus(final String postKey,final String userId){
        likeReference= FirebaseDatabase.getInstance().getReference("likes");
        likeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(userId)){
                    int likeCount=(int)snapshot.child(postKey).getChildrenCount();
                    likeTxt.setText(likeCount+" likes");
                    likeBtn.setImageResource(R.drawable.heart);
                }
                else {
                    int likeCount=(int)snapshot.child(postKey).getChildrenCount();
                    likeTxt.setText(likeCount+" likes");
                    likeBtn.setImageResource(R.drawable.like);
                }
            }

            @Override
            public void onCancelled(@NonNull final DatabaseError error) {

            }
        });
    }

    void prepareExoplayer(Application application,String videoTitle,String videoUrl){
        try{

            vTitleView.setText(videoTitle);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            simpleExoplayer =(SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application,trackSelector);

            Uri videoURI = Uri.parse(videoUrl);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            simpleExoplayerView.setPlayer(simpleExoplayer);
            simpleExoplayer.prepare(mediaSource);
            simpleExoplayer.setPlayWhenReady(false);

        }catch (Exception exception)
        {
            Log.d("Exoplayer Crashed",exception.getMessage().toString());
        }
    }
}
