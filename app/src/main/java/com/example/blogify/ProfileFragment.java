package com.example.blogify;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {


    RecyclerView postList;
    LinearLayoutManager layoutManager;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private DatabaseReference postRef, userRef;
    TextView name, email;
    Button logout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        postList = v.findViewById(R.id.postList);
        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email);
        postList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postList.setLayoutManager(layoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        postRef = FirebaseDatabase.getInstance().getReference("Blog");
        userRef = FirebaseDatabase.getInstance().getReference("User");
        v.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        displayPosts();


        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    name.setText(snapshot.child("name").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }

    private void displayPosts() {
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postRef, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, HomeFragment.PostVH> adapter = new FirebaseRecyclerAdapter<Posts, HomeFragment.PostVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.PostVH holder, int position, @NonNull Posts model) {

                if (!model.getOwnerID().equals(user.getUid())){
//                    Hide view
                    holder.layoutParent.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = holder.layoutParent.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.layoutParent.setLayoutParams(params);
                }


                //                    Show View
                String n = model.getOwner();
                String profileP = String.valueOf(n.charAt(0));
                holder.avatar.setText(profileP);

                holder.date.setText("Posted on "+model.getDate());
//                holder.likes.setText(String.valueOf(model.Likes));
                holder.title.setText(model.getTitle());
                holder.userName.setText(model.getOwner());
                Glide.with(holder.postImage).load(model.getImageURI()).into(holder.postImage);

                postRef.child(model.getID()).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String likeCount = String.valueOf(snapshot.getChildrenCount());
                        holder.likes.setText(likeCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), PostDetails.class);
                        i.putExtra("postID", model.getID());
                        startActivity(i);
                    }
                });


            }

            @NonNull
            @Override
            public HomeFragment.PostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, parent, false);
                return new HomeFragment.PostVH(v);
            }
        };
        postList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
    }
}