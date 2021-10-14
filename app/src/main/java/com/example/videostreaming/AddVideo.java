package com.example.videostreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class AddVideo extends AppCompatActivity {

    VideoView videoView;
    Button browse,upload;
    EditText vTitle;
    Uri videoUri;
    MediaController mediaController;
    StorageReference storagereference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        videoView=findViewById(R.id.videoView);
        browse=findViewById(R.id.browse);
        upload=findViewById(R.id.upload);
        vTitle=findViewById(R.id.vTitle);

        storagereference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("myVideos");


        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Dexter.withContext(AddVideo.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(final PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,101);
                            }

                            @Override
                            public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(final PermissionRequest permissionRequest, final PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                processVideoUploading();
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101 && resultCode==RESULT_OK){
            videoUri=data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private String getExtension() {
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(videoUri));
    }

    public void processVideoUploading(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Media Uploader");
        pd.show();

        StorageReference uploader=storagereference.child("myVideos/"+System.currentTimeMillis()+"."+getExtension());
        uploader.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                fileModel obj=new fileModel(vTitle.getText().toString(),uri.toString());
                                databaseReference.child(databaseReference.push().getKey()).setValue(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(final Void aVoid) {
                                                pd.dismiss();
                                                vTitle.setText("");
                                                Toast.makeText(AddVideo.this, "Sucessfully uploaded", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull final Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(AddVideo.this, "Something wrong happened", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull final UploadTask.TaskSnapshot snapshot) {
                        float percent=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        pd.setMessage("Uploaded: "+(int)percent+"%");
                    }
                });
    }


}