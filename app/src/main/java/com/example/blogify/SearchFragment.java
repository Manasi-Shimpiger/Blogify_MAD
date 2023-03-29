package com.example.blogify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    EditText searchET;
    RecyclerView searchList;
    LinearLayoutManager layoutManager;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_search, container, false);
        searchET = v.findViewById(R.id.searchET);
        searchList = v.findViewById(R.id.searchList);

        searchList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        searchList.setLayoutManager(layoutManager);

        reference = FirebaseDatabase.getInstance().getReference();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString().trim();
                searchProduct(text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    private void searchProduct(String text) {
        Query query = reference.child("Blog").orderByChild("Title").startAt(text).endAt(text + "\uf8ff");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(query, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, HomeFragment.PostVH> adapter = new FirebaseRecyclerAdapter<Posts, HomeFragment.PostVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HomeFragment.PostVH holder, int position, @NonNull Posts model) {

                holder.likes.setVisibility(View.GONE);
                String n = model.getOwner();
                String profileP = String.valueOf(n.charAt(0));
                holder.avatar.setText(profileP);

                holder.date.setText("Posted on "+model.getDate());
                holder.title.setText(model.getTitle());
                holder.userName.setText(model.getOwner());
                Glide.with(holder.postImage).load(model.getImageURI()).into(holder.postImage);

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
        searchList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();

    }

}