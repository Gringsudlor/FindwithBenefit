package com.example.findwithbenefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodsFragment extends Fragment {
    private View FoodsView;
    private RecyclerView myFoodsList;

    private DatabaseReference FoodsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public FoodsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FoodsView = inflater.inflate(R.layout.fragment_foods, container, false);

        myFoodsList = (RecyclerView) FoodsView.findViewById(R.id.contacts_list);
        myFoodsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        FoodsRef = FirebaseDatabase.getInstance().getReference().child("Foods");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return FoodsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Foods>()
                        .setQuery(FoodsRef, Foods.class)
                        .build();

        FirebaseRecyclerAdapter<Foods, FoodsFragment.FoodsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Foods, FoodsFragment.FoodsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodsFragment.FoodsViewHolder holder, int position, @NonNull Foods model) {
                String foodIDs = getRef(position).getKey();

                FoodsRef.child(foodIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            if (snapshot.hasChild("image")){
                                String userImage = snapshot.child("image").getValue().toString();
                                String profileStatus = snapshot.child("cost").getValue().toString();
                                String profileName = snapshot.child("name").getValue().toString();

                                holder.foodName.setText(profileName);
                                holder.foodCost.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.foodImage);

                            }
                            else {
                                String profileStatus = snapshot.child("cost").getValue().toString();
                                String profileName = snapshot.child("name").getValue().toString();

                                holder.foodName.setText(profileName);
                                holder.foodCost.setText(profileStatus);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public FoodsFragment.FoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_display_layout, parent, false);
                FoodsFragment.FoodsViewHolder viewHolder = new FoodsFragment.FoodsViewHolder(view);
                return viewHolder;
            }
        };

        myFoodsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FoodsViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, foodCost;
        CircleImageView foodImage;

        public FoodsViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.user_profile_name);
            foodCost = itemView.findViewById(R.id.user_status);
            foodImage = itemView.findViewById(R.id.users_profile_image);
        }
    }


}
