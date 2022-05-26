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

public class FoodsActivity extends AppCompatActivity {
    private Button orderBtn;
    private TextView foodName, foodCost;
    private EditText quantity;
    private ImageView foodImg;

    private DatabaseReference RootRef, UsersRef, currentUserNameRef, TableRef, FoodRef, OrderRef;


    private FirebaseAuth mAuth;
    private String currentUserID, currentUserName, receivedFood, Current_State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");
        TableRef = RootRef.child("Booking");
        FoodRef = RootRef.child("Foods");
        OrderRef = RootRef.child("Order");
        currentUserNameRef = UsersRef.child(currentUserID);
        receivedFood = getIntent().getExtras().get("visit_food").toString();
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


        InitializeFields();

        foodName.setVisibility(View.VISIBLE);


        RetrieveFoodInfo();

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order();
            }
        });


    }

    private void Order() {
        String foodName = FoodRef.child(receivedFood).getKey().toString();
        HashMap<String, Object> orderMap = new HashMap<>();

        if (Integer.parseInt(quantity.getText().toString()) <= 0) {
            Toast.makeText(this, "Please add quantity", Toast.LENGTH_SHORT).show();
        }
        else {
            UsersRef.child(currentUserID).child("Table").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String tableNo = snapshot.child("Table").getValue().toString();
                        OrderRef.child(tableNo).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int total;
                                int receivedQuantity;
                                if (snapshot.exists()){

                                    if (snapshot.hasChild(foodName)){
                                        receivedQuantity = Integer.parseInt(snapshot.child(foodName).getValue().toString());
                                    }
                                    else{
                                        receivedQuantity = 0;
                                    }
                                    if (snapshot.hasChild("total")){
                                        total = Integer.parseInt(snapshot.child("total").getValue().toString());
                                    }
                                    else {
                                        total = 0;
                                    }

                                    FoodRef.child(foodName).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                int Quantity = Integer.parseInt(quantity.getText().toString());
                                                int totalQuantity = receivedQuantity + Quantity;
                                                String quantityStr = Integer.toString(totalQuantity);
                                                int cost = Integer.parseInt(snapshot.child("cost").getValue().toString());
                                                int totalCost = total + (Integer.parseInt(quantity.getText().toString()) * cost);
                                                String totalStr = Integer.toString(totalCost);

                                                OrderRef.child(tableNo).child(foodName).setValue(quantityStr);
                                                OrderRef.child(tableNo).child("total").setValue(totalStr);
                                                quantity.setText(-1);
                                                Toast.makeText(FoodsActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();
                                                SendUserToMainActivity();

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                                else{
                                    total = 0;
                                    receivedQuantity = 0;
                                    FoodRef.child(foodName).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                int Quantity = Integer.parseInt(quantity.getText().toString());
                                                int totalQuantity = receivedQuantity + Quantity;
                                                String quantityStr = Integer.toString(totalQuantity);
                                                int cost = Integer.parseInt(snapshot.child("cost").getValue().toString());
                                                int totalCost = total + (Integer.parseInt(quantity.getText().toString()) * cost);
                                                String totalStr = Integer.toString(totalCost);

                                                OrderRef.child(tableNo).child(foodName).setValue(quantityStr);
                                                OrderRef.child(tableNo).child("total").setValue(totalStr);
                                                Toast.makeText(FoodsActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();
                                                SendUserToMainActivity();
                                                quantity.setText(0);




                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }
    }


    private void RetrieveFoodInfo() {

        FoodRef.child(receivedFood).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String setFoodName = snapshot.child("name").getValue().toString();
                    String setCost = snapshot.child("cost").getValue().toString();
                    String setImg = snapshot.child("image").getValue().toString();

                    foodName.setText(setFoodName);
                    foodCost.setText(setCost);
                    Picasso.get().load(setImg).placeholder(R.drawable.foodicon).into(foodImg);
                    //ManageTableBooking();
                    //userProfileStatus.setText(setTableStatus);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void InitializeFields() {
        orderBtn = (Button) findViewById(R.id.order_button);
        foodName = (TextView) findViewById(R.id.food_name_text);
        foodCost = (TextView) findViewById(R.id.food_cost_text);
        quantity = (EditText) findViewById(R.id.quantity);
        foodImg = (ImageView) findViewById(R.id.foodImage);
        //tableStatus = (EditText) findViewById(R.id.set_table_status);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setTitle("Add Table");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /*private void ManageTableBooking() {
        TableRef.child(receivedFood).addValueEventListener(new ValueEventListener() {
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
    */


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(FoodsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}