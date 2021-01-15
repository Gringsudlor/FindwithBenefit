 package com.example.findwithbenefit;

 import android.app.ProgressDialog;
 import android.content.Intent;
 import android.net.Uri;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
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

 public class SettingActivity extends AppCompatActivity {
    private Button UpdateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
     private String photoUrl;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;

    private Toolbar SettingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        InitializeFields();

        userName.setVisibility(View.VISIBLE);

        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });

        RetrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SettingActivity.this);
            }
        });

    }




     private void InitializeFields() {
        UpdateAccountSetting = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        loadingBar = new ProgressDialog(this);
        SettingsToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
             CropImage.ActivityResult result = CropImage.getActivityResult(data);
             if (resultCode == RESULT_OK) {
                 loadingBar.setTitle("Set Profile Image");
                 loadingBar.setMessage("Your Profile Image is updating...");
                 loadingBar.setCanceledOnTouchOutside(false);
                 loadingBar.show();

                 final Uri resultUri = result.getUri();
                 StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                 filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                         filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 final String downloadUrl = uri.toString();
                                 //Realtime database.
                                 RootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()){
                                             Toast.makeText(SettingActivity.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                             loadingBar.dismiss();
                                         }
                                         else {
                                             String message= task.getException().getMessage();
                                             Toast.makeText(SettingActivity.this, "ERROR: " + message, Toast.LENGTH_SHORT).show();
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

        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please enter your user name", Toast.LENGTH_SHORT).show();
        }
         if (TextUtils.isEmpty(setUserStatus)){
             Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
         }
         else {
             HashMap<String, Object> profileMap = new HashMap<>();
             profileMap.put("uid", currentUserID);
             profileMap.put("name", setUserName);
             profileMap.put("status", setUserStatus);
             profileMap.put("image",photoUrl);
             RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 SendUserToMainActivity();
                                 Toast.makeText(SettingActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                             }
                             else{
                                 String message = task.getException().toString();
                                 Toast.makeText(SettingActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
         }

     }

     private void RetrieveUserInfo() {

        RootRef.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();
                            photoUrl=retrieveProfileImage;

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }
                        else if ((snapshot.exists()) && (snapshot.hasChild("name"))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();

                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveStatus);
                        }
                        else {
                            userName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingActivity.this, "Please set and update your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

     }

     private void SendUserToMainActivity() {
         Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
         mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(mainIntent);
         finish();
     }

 }