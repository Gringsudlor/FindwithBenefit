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

import com.example.findwithbenefit.R;
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
public class BookingFragment extends Fragment{
    private View BookingView;
    private RecyclerView recyclerView;

    private DatabaseReference BookingRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BookingView = inflater.inflate(R.layout.fragment_booking, container, false);

        recyclerView = (RecyclerView) BookingView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        BookingRef = FirebaseDatabase.getInstance().getReference().child("Booking");

        return BookingView;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Booking>()
                        .setQuery(BookingRef, Booking.class)
                        .build();

        FirebaseRecyclerAdapter<Booking, BookingFragment.BookingViewHolder> adapter
                = new FirebaseRecyclerAdapter<Booking, BookingFragment.BookingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BookingFragment.BookingViewHolder holder, int position, @NonNull Booking model) {
                String bookIDs = getRef(position).getKey();

                BookingRef.child(bookIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            String Status = snapshot.child("status").getValue().toString();
                            String Name = snapshot.child("name").getValue().toString();

                            holder.tableName.setText(Name);
                            holder.tableStatus.setText(Status);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.tableLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_table = getRef(position).getKey();

                        Intent intent = new Intent(getActivity(), BookingActivity.class);
                        intent.putExtra("visit_table", visit_table);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public BookingFragment.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_display_layout, parent, false);
                BookingFragment.BookingViewHolder viewHolder = new BookingFragment.BookingViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder{

        TextView tableName, tableStatus;
        ConstraintLayout tableLayout;
        //ImageView foodImage;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            tableName = itemView.findViewById(R.id.myText1);
            tableStatus = itemView.findViewById(R.id.myText2);
            tableLayout = itemView.findViewById(R.id.tableLayout);
            //foodImage = itemView.findViewFId(R.id.myImageView);
        }
    }


}
