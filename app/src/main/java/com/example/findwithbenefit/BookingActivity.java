package com.example.findwithbenefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookingActivity extends AppCompatActivity {
    private Button bookTable;
    private TextView tableName;

    private DatabaseReference RootRef, UsersRef, currentUserNameRef, TableRef;


    private FirebaseAuth mAuth;
    private String currentUserID, currentUserName, receivedTable, Current_State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        TableRef = FirebaseDatabase.getInstance().getReference().child("Booking");
        currentUserNameRef = UsersRef.child(currentUserID);
        receivedTable = getIntent().getExtras().get("visit_table").toString();
        Current_State = "new";


        currentUserNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("name")){
                    currentUserName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        tableName.setVisibility(View.VISIBLE);


        RetrieveTableInfo();


    }

    private void RetrieveTableInfo() {

        TableRef.child(receivedTable).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String setTableName = snapshot.child("name").getValue().toString();
                    String setTableStatus = snapshot.child("status").getValue().toString();

                    tableName.setText(setTableName);
                    ManageTableBooking();
                    //userProfileStatus.setText(setTableStatus);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void InitializeFields() {
        bookTable = (Button) findViewById(R.id.book_button);
        tableName = (TextView) findViewById(R.id.table_name_text);
        //tableStatus = (EditText) findViewById(R.id.set_table_status);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setTitle("Add Table");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void ManageTableBooking() {
        TableRef.child(receivedTable).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String setTableName = snapshot.child("name").getValue().toString();
                String setTableStatus = snapshot.child("status").getValue().toString();

                if(!setTableStatus.equals("Available")){
                    Current_State = "booked";
                    bookTable.setText("Booked");
                    Toast.makeText(BookingActivity.this, "Booked", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Current_State.equals("new")) {
                    UpdateSetting();
                    Toast.makeText(BookingActivity.this, "Reserved Successfully", Toast.LENGTH_SHORT).show();
                }
                if (Current_State.equals("booked")) {
                    Toast.makeText(BookingActivity.this, "Booked", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }


    private void UpdateSetting() {
        String setTableName = tableName.getText().toString();
        String setTableStatus = "Reserved by " + currentUserName;

        if (TextUtils.isEmpty(setTableName)){
            //Toast.makeText(this, "Please enter your table name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setTableStatus)){
            //Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", setTableName);
            profileMap.put("status", setTableStatus);
            RootRef.child("Booking").child(setTableName).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                //Toast.makeText(BookingActivity.this, "Table Updated", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                String message = task.getException().toString();
                                //Toast.makeText(BookingActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(BookingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}