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

public class AddMenuActivity extends AppCompatActivity {
    private Button UpdateFood, DeleteFood;
    private EditText foodName, foodCost;
    private ImageView foodImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String photoUrl;

    private static final int GalleryPick = 1;
    private StorageReference FoodImageRef;
    private ProgressDialog loadingBar;

    private Toolbar AddFoodToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        FoodImageRef = FirebaseStorage.getInstance().getReference().child("Food Images");

        InitializeFields();

        foodName.setVisibility(View.VISIBLE);

        UpdateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });

        DeleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete();
            }
        });

        RetrieveUserInfo();



        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(foodName.getText().toString())){
                    Toast.makeText(AddMenuActivity.this, "Please enter food name", Toast.LENGTH_SHORT).show();
                }
                else{
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(AddMenuActivity.this);
                }

            }
        });



    }




    private void InitializeFields() {
        UpdateFood = (Button) findViewById(R.id.update_food_button);
        DeleteFood = (Button) findViewById(R.id.delete_food_button);
        foodName = (EditText) findViewById(R.id.set_food_name);
        foodCost = (EditText) findViewById(R.id.set_food_cost);
        foodImage = (ImageView) findViewById(R.id.set_food_image);
        loadingBar = new ProgressDialog(this);
        AddFoodToolbar = (Toolbar) findViewById(R.id.add_menu_toolbar);
        setSupportActionBar(AddFoodToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Edit Food");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Set Food Image");
                loadingBar.setMessage("Food Image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                final Uri resultUri = result.getUri();
                StorageReference filePath = FoodImageRef.child(foodName.getText().toString() + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                //Realtime database.
                                RootRef.child("Foods").child(foodName.getText().toString()).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(AddMenuActivity.this, "Food Image Updated", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                        else {
                                            String message= task.getException().getMessage();
                                            Toast.makeText(AddMenuActivity.this, "ERROR: " + message, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void UpdateSetting() {

        String setFoodName = foodName.getText().toString();
        String setFoodCost = foodCost.getText().toString() + " .-";

        if (TextUtils.isEmpty(setFoodName)){
            Toast.makeText(this, "Please enter your user name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setFoodCost)){
            Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> profileMap = new HashMap<>();
            //profileMap.put("uid", currentUserID);
            profileMap.put("name", setFoodName);
            profileMap.put("cost", setFoodCost);
            //profileMap.put("image",photoUrl);
            RootRef.child("Foods").child(setFoodName).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(AddMenuActivity.this, "Food Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(AddMenuActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void Delete() {
        String setFoodName = foodName.getText().toString();

        if (TextUtils.isEmpty(setFoodName)){
            Toast.makeText(this, "Please enter your food name", Toast.LENGTH_SHORT).show();
        }

        else {

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", setFoodName);
            RootRef.child("Foods").child(setFoodName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            RootRef.child("Foods").child(setFoodName).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                //Toast.makeText(AddTableActivity.this, "Table Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(AddMenuActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    private void RetrieveUserInfo() {

        RootRef.child("Foods").child(foodName.getText().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("cost").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();
                            photoUrl=retrieveProfileImage;

                            foodName.setText(retrieveUserName);
                            foodCost.setText(retrieveStatus);
                            Picasso.get().load(retrieveProfileImage).into(foodImage);
                        }
                        else if ((snapshot.exists()) && (snapshot.hasChild("name"))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("cost").getValue().toString();

                            foodName.setText(retrieveUserName);
                            foodCost.setText(retrieveStatus);
                        }
                        else {
                            foodName.setVisibility(View.VISIBLE);
                            //Toast.makeText(AddMenuActivity.this, "Please set and update your food information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddMenuActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}