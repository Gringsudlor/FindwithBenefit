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

public class AddTableActivity extends AppCompatActivity {
    private Button AddTable, DelTable;
    private EditText tableName;

    private DatabaseReference RootRef, UsersRef, currentUserNameRef, tableRef;

    private Toolbar AddTableToolbar;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private FirebaseAuth mAuth;
    private String currentUserID, currentUserName;

    private List<String> items;



    //String[] items =  {"Material","Design","Components","Android","5.0 Lollipop"};
    private ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");
        tableRef = RootRef.child("Booking");
        currentUserNameRef = UsersRef.child(currentUserID);

        Spinner tableSpinner = (Spinner) findViewById(R.id.table_dropdown);
        items = new ArrayList<>();

        //items.add(tableRef);
        //items.add("Rice");
        //items.add("Beans");
        //items.add("Meat");

        //autoCompleteTxt = findViewById(R.id.table_auto_txt);



        //mySpinner.setSelection(getIndex(mySpinner, ));
        tableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = tableSpinner.getSelectedItem().toString();
                tableName.setText(item);
                Toast.makeText(AddTableActivity.this, item, Toast.LENGTH_SHORT).show();
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
                    adapterItems = new ArrayAdapter<String>(AddTableActivity.this,android.R.layout.simple_spinner_item,items);
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

        AddTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });

        DelTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Delete();
            }
        });

        radioGroup = findViewById(R.id.radioGroup);


        RetrieveUserInfo();


    }
    private void getData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            String key = ds.getKey();
        }
    }

    private void Delete() {
        String setTableName = tableName.getText().toString();

        if (TextUtils.isEmpty(setTableName)){
            Toast.makeText(this, "Please enter your table name", Toast.LENGTH_SHORT).show();
        }

        else {

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", setTableName);
            RootRef.child("Booking").child(setTableName).addValueEventListener(new ValueEventListener() {
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
            RootRef.child("Booking").child(setTableName).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                //Toast.makeText(AddTableActivity.this, "Table Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(AddTableActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    public void checkButton(View view){
        int radioId = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioId);

        Toast.makeText(this, "Status set to: " + radioButton.getText(), Toast.LENGTH_SHORT).show();

    }


    private void InitializeFields() {
        AddTable = (Button) findViewById(R.id.add_table_button);
        DelTable = (Button) findViewById(R.id.del_table_button);
        tableName = (EditText) findViewById(R.id.set_table_name);
        //tableStatus = (EditText) findViewById(R.id.set_table_status);
        AddTableToolbar = (Toolbar) findViewById(R.id.add_table_toolbar);
        setSupportActionBar(AddTableToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Edit Table");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    private void UpdateSetting() {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        RadioButton av = (RadioButton) findViewById(R.id.available_button);
        //RadioButton av = (RadioButton) findViewById(R.id.available_button);
        String setTableName = tableName.getText().toString();
        String setTableStatus;
        if (radioButton == av){
            setTableStatus = radioButton.getText().toString();
        }
        else {
            setTableStatus = radioButton.getText() + " by " + currentUserName;
        }

        if (TextUtils.isEmpty(setTableName)){
            Toast.makeText(this, "Please enter your table name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setTableStatus)){
            Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
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
                                //Toast.makeText(AddTableActivity.this, "Table Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(AddTableActivity.this, "ERROR " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void RetrieveUserInfo() {

        RootRef.child("Booking").child(tableName.getText().toString())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("name"))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();

                            tableName.setText(retrieveUserName);
                            //tableStatus.setText(retrieveStatus);
                        }
                        else {
                            tableName.setVisibility(View.VISIBLE);
                            //Toast.makeText(AddTableActivity.this, "Please set and update table information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(AddTableActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}