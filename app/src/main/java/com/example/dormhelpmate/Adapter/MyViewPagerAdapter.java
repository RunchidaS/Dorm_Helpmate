package com.example.dormhelpmate.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dormhelpmate.Fragment.BookingStep1Fragment;
import com.example.dormhelpmate.Fragment.BookingStep2Fragment;
import com.example.dormhelpmate.Fragment.BookingStep3Fragment;

public class MyViewPagerAdapter  extends FragmentPagerAdapter {

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return BookingStep1Fragment.getInstance();
            case 1:
                return BookingStep2Fragment.getInstance();
            case 2:
                return BookingStep3Fragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
