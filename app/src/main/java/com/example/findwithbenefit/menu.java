/*package com.example.findwithbenefit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class menu extends FragmentPagerAdapter {

    private BottomNavigationView bottomNavigationView;

    public menu(@NonNull FragmentManager fm) {
        super(fm);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new
            BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment fragment=null;
            switch (menuItem.getItemId()){

                case R.id.booking:
                    fragment=new BookingFragment();
                    break;

                case R.id.chat:
                    fragment=new ChatsFragment();
                    break;

                case R.id.drink:
                    fragment=new DrinkFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

            return true;
        }
    };


    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                BookingFragment contactsFragment = new BookingFragment();
                return contactsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 2:
                DrinkFragment requestsFragment = new DrinkFragment();
                return requestsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
*/