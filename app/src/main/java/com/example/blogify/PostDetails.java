package com.example.blogify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostDetails extends AppCompatActivity {

    Intent i;
    String postID= null;
    TextView title,DateTime,Name,Body,likes, avat;
    ImageView blogimage;
    FloatingActionButton likefab;
    FirebaseUser user;
    private DatabaseReference reference;
    String likeCount;

    boolean isLiked = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        user = FirebaseAuth.getInstance().getCurrentUser();
        likefab = findViewById(R.id.likefab);
        title=findViewById(R.id.title);
        DateTime=findViewById(R.id.dateTime);
        Name=findViewById(R.id.name);
        Body=findViewById(R.id.body);
        blogimage=findViewById(R.id.blogImage);
        likes=findViewById(R.id.likes);
        avat = findViewById(R.id.avat);
        i = getIntent();
        postID = i.getStringExtra("postID");
        reference= FirebaseDatabase.getInstance().getReference().child("Blog").child(postID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      reference.child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeCount = String.valueOf(snapshot.getChildrenCount());
                if (snapshot.hasChild(user.getUid())){
                    isLiked = true;
                    likefab.setImageResource(R.drawable.baseline_favorite_24);
                }else {
                    isLiked = false;
                    likefab.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
      });

      likefab.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (isLiked){
//                  remove Likes
                  reference.child("Likes").child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          finish();
                          startActivity(getIntent());
                      }
                  });
              }else{
//                  add Likes
                  Calendar calendar = Calendar.getInstance();
                  SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss aa");
                  String saveCurrentTime = currentTime.format(calendar.getTime());

                  Map<String, String> m = new HashMap<>();
                  m.put("Time", saveCurrentTime);

                  reference.child("Likes").child(user.getUid()).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          finish();
                          startActivity(getIntent());
                      }
                  });
              }
          }
      });

      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()){
                  String Content = snapshot.child("Content").getValue().toString();
                  String Date = snapshot.child("Date").getValue().toString();
                  String ID = snapshot.child("ID").getValue().toString();
                  String Owner = snapshot.child("Owner").getValue().toString();
                  String Title = snapshot.child("Title").getValue().toString();
                  String imageURI = snapshot.child("imageURI").getValue().toString();
                  String profileP = String.valueOf(Owner.charAt(0));

                  avat.setText(profileP);
                  title.setText(Title);
                  Body.setText(Content);
                  Name.setText(Owner);
                  DateTime.setText("Posted on "+Date);

                  likes.setText(likeCount);
                  Glide.with(getApplicationContext()).load(imageURI).into(blogimage);

              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }
}