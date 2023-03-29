package com.example.blogify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddNewPost extends AppCompatActivity {
    EditText title,blogBody;
    Button post;
    ImageView image;
    DatabaseReference reference, userRef;
    FirebaseUser user;
    Uri imageUri;
    StorageReference storagePostPictureRef;
    String currentTime,Title,currentDate;
    private String myUrl1 = "";
    private String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);
        title=findViewById(R.id.title);
        blogBody=findViewById(R.id.blogbody);
        post=findViewById(R.id.post);
        image=findViewById(R.id.image);
        user= FirebaseAuth.getInstance().getCurrentUser();
        post.setVisibility(View.GONE);
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("PostMedia");
        userRef = FirebaseDatabase.getInstance().getReference("User");

        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Title=title.getText().toString();
                String Body=blogBody.getText().toString();


                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDateT = new SimpleDateFormat("MMM dd, YYYY");
                currentDate = currentDateT.format(calendar.getTime());
                SimpleDateFormat currentTimeT = new SimpleDateFormat("HH:mm:ss a");
                currentTime = currentTimeT.format(calendar.getTime());

                reference= FirebaseDatabase.getInstance().getReference().child("Blog");


                String key = currentDate+currentTime+user.getUid();

                HashMap<String,Object> map=new HashMap<>();
                map.put("Title",Title);
                map.put("Content",Body);
                map.put("ID",key);
                map.put("Date",currentDate);
                map.put("Time",currentTime);
                map.put("imageURI", myUrl1);
                map.put("Likes", 0);
                map.put("Owner", userName);
                map.put("OwnerID", user.getUid());

                reference.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            userRef.child(user.getUid())
                                    .child("Blogs")
                                    .child(key)
                                    .setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AddNewPost.this, "Posting...", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                        }
                    }
                });
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data.getData() != null ){
            imageUri= data.getData();
            image.setImageURI(imageUri);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());
            String ProductRandomKey = user.getUid() + saveCurrentDate + saveCurrentTime;

            StorageReference filePath = storagePostPictureRef.child(ProductRandomKey + ".jpg");

            filePath.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        myUrl1 = task.getResult().toString();
                        post.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}