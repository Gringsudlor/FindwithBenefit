package com.example.findwithbenefit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private Menu option;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, UserRef;
    private String currentUserID;
    private String adminID = "je896l1wU6TuNpCjlvazAx653B82";
    private String userTable;

    private int[]tabIcons={
            R.drawable.ic_action_name2,
            R.drawable.ic_drink,
            R.drawable.ic_chat,
            R.drawable.ic_face

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        RootRef = FirebaseDatabase.getInstance().getReference();
        UserRef = RootRef.child("Users");

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FindwithBenefit");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        option = (Menu) findViewById(R.id.myOption);



        setupTabIcons();


    }
    private void setupTabIcons() {
        myTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        myTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        myTabLayout.getTabAt(2).setIcon(tabIcons[2]);
        myTabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            updateUserStatus("online");

            VerifyUserExistance();
        }

        if (RootRef.child("Users").child(currentUserID).child("orders") == null){
            RootRef.child("Users").child(currentUserID).child("orders");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            updateUserStatus("offline");
        }

    }

    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("name").exists())){
                    //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option){
            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option){
            SendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.main_find_friends_option){
            SendUserToFindFriendsActivity();
        }
        if (item.getItemId() == R.id.main_checkIn_option){
            SendUserToCheckInActivity();
        }
        if (item.getItemId() == R.id.main_checkOut_option){
            SendUserToCheckedActivity();
        }
        if (item.getItemId() == R.id.main_add_menu_option){
            SendUserToAddMenuActivity();
        }
        if (item.getItemId() == R.id.main_add_table_option){
            SendUserToAddTableActivity();
        }
        if (item.getItemId() == R.id.main_order_history_option){
            SendUserToOrderHistoryActivity();
        }
        if (item.getItemId() == R.id.main_clear_option){
            SendUserToClearOrderActivity();
        }
        return true;
    }


    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem checkIn = menu.findItem(R.id.main_checkIn_option);
        MenuItem checkOut = menu.findItem(R.id.main_checkOut_option);
        MenuItem food = menu.findItem(R.id.main_add_menu_option);
        MenuItem table = menu.findItem(R.id.main_add_table_option);
        MenuItem orderHist = menu.findItem(R.id.main_order_history_option);
        MenuItem findFriends = menu.findItem(R.id.main_find_friends_option);
        MenuItem clearOrder = menu.findItem(R.id.main_clear_option);

        if(!currentUserID.equals(adminID)) {
            clearOrder.setVisible(false);
            food.setVisible(false);
            table.setVisible(false);
            //orderHist.setVisible(false);

            UserRef.child(currentUserID).child("Table").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        String userTable = snapshot.child("Table").getValue().toString();
                            checkIn.setVisible(false);
                            checkOut.setVisible(true);
                            RootRef.child("Order").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(userTable)){
                                        orderHist.setVisible(true);
                                    }
                                    else {
                                        orderHist.setVisible(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                    }
                    else{
                        checkIn.setVisible(true);
                        checkOut.setVisible(false);
                        orderHist.setVisible(false);
                        //Toast.makeText(MainActivity.this, "nooooo", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            clearOrder.setVisible(true);
            food.setVisible(true);
            table.setVisible(true);
            checkIn.setVisible(false);
            checkOut.setVisible(false);
            orderHist.setVisible(false);
        }
        return true;
    }

    private void SendUserToClearOrderActivity() {
        Intent clearOrderIntent = new Intent(MainActivity.this, ClearOrderActivity.class);
        startActivity(clearOrderIntent);
    }

    private void SendUserToOrderHistoryActivity() {
        Intent orderHistIntent = new Intent(MainActivity.this, OrderHistoryActivity.class);
        startActivity(orderHistIntent);
    }

    private void SendUserToCheckedActivity() {
        Intent checkedIntent = new Intent(MainActivity.this, CheckedActivity.class);
        startActivity(checkedIntent);
    }

    private void SendUserToCheckInActivity() {
        Intent checkInIntent = new Intent(MainActivity.this, CheckInActivity.class);
        startActivity(checkInIntent);
    }

    private void SendUserToAddTableActivity() {
        Intent addMenuIntent = new Intent(MainActivity.this, AddTableActivity.class);
        startActivity(addMenuIntent);
    }

    private void SendUserToAddMenuActivity() {
        Intent addMenuIntent = new Intent(MainActivity.this, AddMenuActivity.class);
        startActivity(addMenuIntent);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUserToFindFriendsActivity() {
        Intent FindFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(FindFriendsIntent);
    }

    private void updateUserStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd, MMM yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

}