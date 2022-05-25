package com.example.findwithbenefit;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class DrinkFragment extends Fragment{
    private View DrinksView;
    private RecyclerView recyclerView;

    private DatabaseReference FoodsRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DrinksView = inflater.inflate(R.layout.fragment_drinks, container, false);

        recyclerView = (RecyclerView) DrinksView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FoodsRef = FirebaseDatabase.getInstance().getReference().child("Foods");

        return DrinksView;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Foods>()
                        .setQuery(FoodsRef, Foods.class)
                        .build();

        FirebaseRecyclerAdapter<Foods, DrinkFragment.FoodsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Foods, DrinkFragment.FoodsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DrinkFragment.FoodsViewHolder holder, int position, @NonNull Foods model) {
                String foodIDs = getRef(position).getKey();

                FoodsRef.child(foodIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            if (snapshot.hasChild("image")){
                                String Image = snapshot.child("image").getValue().toString();
                                String Cost = snapshot.child("cost").getValue().toString();
                                String Name = snapshot.child("name").getValue().toString();

                                holder.foodName.setText(Name);
                                holder.foodCost.setText(Cost);
                                //holder.foodImage.setImageResource(Image);
                                Picasso.get().load(Image).placeholder(R.drawable.foodicon).into(holder.foodImage);

                            }
                            else {
                                String Cost = snapshot.child("cost").getValue().toString();
                                String Name = snapshot.child("name").getValue().toString();

                                holder.foodName.setText(Name);
                                holder.foodCost.setText(Cost);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.foodLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_food = getRef(position).getKey();

                        Intent intent = new Intent(getActivity(), FoodsActivity.class);
                        intent.putExtra("visit_food", visit_food);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public DrinkFragment.FoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_display_layout, parent, false);
                DrinkFragment.FoodsViewHolder viewHolder = new DrinkFragment.FoodsViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FoodsViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, foodCost;
        ImageView foodImage;
        ConstraintLayout foodLayout;

        public FoodsViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.myText1);
            foodCost = itemView.findViewById(R.id.myText2);
            foodImage = itemView.findViewById(R.id.myImageView);
            foodLayout = itemView.findViewById(R.id.foodLayout);
        }
    }


}
