package com.example.findwithbenefit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment {
    private ImageButton b1,b2, b3, b4;
    private ImageView book1, book2, book3, book4;


    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, TableListRef, BookedRef, NotBookedRef;


    public BookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        TableListRef = FirebaseDatabase.getInstance().getReference().child("Table List");
        b1 = (ImageButton) getView().findViewById(R.id.button1);
        b2 = (ImageButton) getView().findViewById(R.id.button2);
        b3 = (ImageButton) getView().findViewById(R.id.button3);
        b4 = (ImageButton) getView().findViewById(R.id.button4);

        book1 = (ImageView) getView().findViewById(R.id.imageView7);

        book2 = (ImageView) getView().findViewById(R.id.imageView9);
        book3 = (ImageView) getView().findViewById(R.id.imageView10);
        book4 = (ImageView) getView().findViewById(R.id.imageView11);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
        TableListRef.child("table1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Booked")) {
                    b1.setEnabled(false);
                    book1.setVisibility(View.VISIBLE);
                } else if (snapshot.hasChild("NotBooked")) {
                    b1.setEnabled(true);
                    book1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TableListRef.child("table2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Booked")) {
                    b2.setEnabled(false);
                    book2.setVisibility(View.VISIBLE);
                } else if (snapshot.hasChild("NotBooked")) {
                    b2.setEnabled(true);
                    book2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TableListRef.child("table3").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Booked")) {
                    b3.setEnabled(false);
                    book3.setVisibility(View.VISIBLE);
                } else if (snapshot.hasChild("NotBooked")) {
                    b3.setEnabled(true);
                    book3.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        TableListRef.child("table4").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Booked")) {
                    b4.setEnabled(false);
                    book4.setVisibility(View.VISIBLE);
                } else if (snapshot.hasChild("NotBooked")) {
                    b4.setEnabled(true);
                    book4.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog2();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog3();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog4();
            }
        });
    }
    public void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TableListRef.child("table1").child("Booked").setValue("");
                TableListRef.child("table1").child("NotBooked").removeValue();
                Toast.makeText(BookingFragment.this.getContext(), "Booked", Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
    public void createDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TableListRef.child("table2").child("Booked").setValue("");
                TableListRef.child("table2").child("NotBooked").removeValue();
                Toast.makeText(BookingFragment.this.getContext(), "Booked", Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
    public void createDialog3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TableListRef.child("table3").child("Booked").setValue("");
                TableListRef.child("table3").child("NotBooked").removeValue();
                Toast.makeText(BookingFragment.this.getContext(), "Booked", Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
    public void createDialog4(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TableListRef.child("table4").child("Booked").setValue("");
                TableListRef.child("table4").child("NotBooked").removeValue();
                Toast.makeText(BookingFragment.this.getContext(), "Booked", Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
}