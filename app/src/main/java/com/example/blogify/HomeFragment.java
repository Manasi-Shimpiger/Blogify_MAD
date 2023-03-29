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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FloatingActionButton addPost;
    RecyclerView postList;
    LinearLayoutManager layoutManager;
    FirebaseUser user;
    private DatabaseReference postRef;
    ProgressBar progressBar;
    String month="";
    Spinner spinner;
    TextView noPost;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        addPost = v.findViewById(R.id.addPost);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddNewPost.class));
            }
        });

        noPost = v.findViewById(R.id.noPost);
        noPost.setVisibility(View.GONE);
        spinner = v.findViewById(R.id.spinner);
        postList = v.findViewById(R.id.postList);
        progressBar = v.findViewById(R.id.progressBar);
        postList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postList.setLayoutManager(layoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        postRef = FirebaseDatabase.getInstance().getReference("Blog");

        ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource(getContext(), R.array.numbers, android.R.layout.simple_spinner_item);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adap);
        spinner.setOnItemSelectedListener(this);

        displayPosts("All");

        return v;
    }

    private void displayPosts(String mon) {

        Query query = postRef.orderByChild("Date");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(query, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostVH> adapter = new FirebaseRecyclerAdapter<Posts, PostVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostVH holder, int position, @NonNull Posts model) {

                String date = model.getDate();

                if (mon.equals("All")){
                    String n = model.getOwner();
                    String profileP = String.valueOf(n.charAt(0));
                    holder.avatar.setText(profileP);
                    noPost.setVisibility(View.GONE);
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
                }else{
                    if (date.contains(mon)){
                        noPost.setVisibility(View.GONE);
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
                    }else {
                        noPost.setVisibility(View.VISIBLE);
                        holder.layoutParent.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.layoutParent.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        holder.layoutParent.setLayoutParams(params);
                    }
                }
            }

            @NonNull
            @Override
            public PostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, parent, false);
                return new PostVH(v);
            }
        };
        postList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        month = adapterView.getItemAtPosition(i).toString();
        displayPosts(month);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static class PostVH extends RecyclerView.ViewHolder{
        ImageView postImage;
        TextView userName, title, date, likes,avatar;
        LinearLayout layoutParent;

        public PostVH(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            postImage = itemView.findViewById(R.id.postImage);
            userName = itemView.findViewById(R.id.userName);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            likes = itemView.findViewById(R.id.likes);
            layoutParent = itemView.findViewById(R.id.layoutParent);
        }
    }
}

