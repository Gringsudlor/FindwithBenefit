package com.example.findwithbenefit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.firebase.database.ChildEventListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckInActivity extends AppCompatActivity {
    private Button CheckIn;
    private TextView tableName;

    private DatabaseReference RootRef, UsersRef, currentUserNameRef, tableRef;

    private Toolbar CheckInToolbar;

    private FirebaseAuth mAuth;
    private String currentUserID, currentUserName;

    private List<String> items;

    private ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");
        tableRef = RootRef.child("Booking");
        currentUserNameRef = UsersRef.child(currentUserID);

        Spinner tableSpinner = (Spinner) findViewById(R.id.table_list_dropdown);
        items = new ArrayList<>();

        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = tableSpinner.getSelectedItem().toString();
                tableName.setText(item);
                Toast.makeText(CheckInActivity.this, item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tableRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    items.add(key);
                    adapterItems = new ArrayAdapter<String>(CheckInActivity.this,android.R.layout.simple_spinner_item,items);
                    adapterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tableSpinner.setAdapter(adapterItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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

        tableName.setVisibility(View.VISIBLE);

        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });



        //RetrieveUserInfo();


    }


    private void InitializeFields() {
        CheckIn = (Button) findViewById(R.id.check_in_button);
        tableName = (TextView) findViewById(R.id.table_name);
        CheckInToolbar = (Toolbar) findViewById(R.id.check_in_toolbar);
        setSupportActionBar(CheckInToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Check In");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    private void UpdateSetting() {
        String setTableName = tableName.getText().toString();
        if (TextUtils.isEmpty(setTableName)){
            Toast.makeText(this, "Please select your table name", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("Table", setTableName);
            UsersRef.child(currentUserID).child("Table").updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                //Toast.makeText(AddTableActivity.this, "Table Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(CheckInActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(CheckInActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}