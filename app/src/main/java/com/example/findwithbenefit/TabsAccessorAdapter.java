package com.example.findwithbenefit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                BookingFragment bookingFragment = new BookingFragment();
                return bookingFragment;

            case 1:
                DrinkFragment drinkFragment = new DrinkFragment();
                return drinkFragment;

            case 2:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position){

            case 0:
                return "Booking";

            case 1:
                return "Menu";

            case 2:
                return "Chats";

            case 3:
                return "Requests";

            default:
                return null;
        }
    }
}
