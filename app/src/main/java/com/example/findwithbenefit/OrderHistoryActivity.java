package com.example.findwithbenefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class OrderHistoryActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView OrderHistList;

    private DatabaseReference RootRef, UsersRef, OrderRef, FoodsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");
        FoodsRef = RootRef.child("Foods");
        OrderRef = RootRef.child("Order");

        OrderHistList = (RecyclerView) findViewById(R.id.order_history_list);
        OrderHistList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.order_history_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        UsersRef.child(currentUserID).child("Table").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String tableNo = snapshot.child("Table").getValue().toString();
                    OrderRef.child(tableNo).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String total = snapshot.child("total").getValue().toString();
                            getSupportActionBar().setTitle("Total: " + total + " .-");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    getSupportActionBar().setTitle("Order History");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Foods>()
                        .setQuery(FoodsRef, Foods.class)
                        .build();

        FirebaseRecyclerAdapter<Foods, OrderHistViewHolder> adapter
                = new FirebaseRecyclerAdapter<Foods, OrderHistViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderHistViewHolder holder, int position, @NonNull Foods model) {
                String foodIDs = getRef(position).getKey();

                UsersRef.child(currentUserID).child("Table").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String currentUserTable = snapshot.child("Table").getValue().toString();
                            OrderRef.child(currentUserTable).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        if (snapshot.hasChild(foodIDs)){
                                            String Quantity = snapshot.child(foodIDs).getValue().toString();
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
                                                            holder.foodQuantity.setText(Quantity);
                                                            Picasso.get().load(Image).placeholder(R.drawable.foodicon).into(holder.foodImage);

                                                        }
                                                        else {
                                                            String Cost = snapshot.child("cost").getValue().toString();
                                                            String Name = snapshot.child("name").getValue().toString();

                                                            holder.foodName.setText(Name);
                                                            holder.foodCost.setText(Cost);
                                                            //getSupportActionBar().setTitle("Hi");
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                        else {
                                            holder.itemView.setVisibility(View.GONE);
                                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                            params.height = 0;
                                            params.width = 0;
                                            holder.itemView.setLayoutParams(params);
                                        }

                                    }
                                    else {
                                        holder.itemView.setVisibility(View.GONE);
                                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                        params.height = 0;
                                        params.width = 0;
                                        holder.itemView.setLayoutParams(params);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            holder.itemView.setVisibility(View.GONE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            params.width = 0;
                            holder.itemView.setLayoutParams(params);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public OrderHistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_display_layout, parent, false);
                OrderHistViewHolder viewHolder = new OrderHistViewHolder(view);
                return viewHolder;
            }
        };



        OrderHistList.setAdapter(adapter);

        adapter.startListening();


    }

    public static class OrderHistViewHolder extends RecyclerView.ViewHolder{

        TextView foodName, foodCost, foodQuantity;
        ImageView foodImage;

        public OrderHistViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.foodName);
            foodCost = itemView.findViewById(R.id.foodCost);
            foodQuantity = itemView.findViewById(R.id.quantity_txt);
            foodImage = itemView.findViewById(R.id.foodImg);

        }
    }

}