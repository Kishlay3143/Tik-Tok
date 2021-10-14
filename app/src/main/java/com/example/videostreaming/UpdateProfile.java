package com.example.videostreaming;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.gesture.GestureLibraries;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {

    EditText uName;
    CircleImageView uImage;
    Button btnUpdate;

    DatabaseReference dbReference;
    StorageReference storageReference;

    Uri filepath;
    Bitmap bitmap;
    String UserId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        uName=findViewById(R.id.uName);
        uImage=findViewById(R.id.uImage);
        btnUpdate=findViewById(R.id.btnUpdate);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        UserId=user.getUid();

        dbReference= FirebaseDatabase.getInstance().getReference().child("userProfile");
        storageReference= FirebaseStorage.getInstance().getReference();

        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Dexter.withContext(UpdateProfile.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(final PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Please Select the Image"),1);
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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                updateToFirebase();
            }
        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if (requestCode==1 && resultCode==RESULT_OK){
            filepath=data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                uImage.setImageBitmap(bitmap);
            }catch (Exception exception){
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void updateToFirebase(){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("Your Image is Uploading... ");
        dialog.show();


        final StorageReference uploader=storageReference.child("profileImages/"+"img"+System.currentTimeMillis());
        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Toast.makeText(UpdateProfile.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                        // Code to download the url of the image
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {

                                // Yha pe data ko firebase me store karne ka kaam ho raha hai.
                                final Map<String,Object> map=new HashMap<>();
                                map.put("uImage",uri.toString());
                                map.put("uName",uName.getText().toString());

                                dbReference.child(UserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                        if (snapshot.exists())
                                            dbReference.child(UserId).updateChildren(map);
                                        else
                                            dbReference.child(UserId).setValue(map);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull final DatabaseError error) {

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
                        dialog.setMessage("Uploaded :"+(int)percent+ "%");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Yaha pe data ko views me set karne ka kaam ho raha hai.
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        UserId=user.getUid();
        dbReference.child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()){
                    uName.setText(snapshot.child("uName").getValue().toString());
                    Glide.with(getApplicationContext()).load(snapshot.child("uImage").getValue().toString()).into(uImage);
                }
            }

            @Override
            public void onCancelled(@NonNull final DatabaseError error) {

            }
        });

    }
}