package com.example.dormhelpmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.dormhelpmate.Adapter.MyViewPagerAdapter;
import com.example.dormhelpmate.Common.Common;
import com.example.dormhelpmate.Common.NonSwipeViewPager;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class booking extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    //Event
    @OnClick (R.id.btn_previous_step)
    void previousStep() {
        if(Common.step == 2 || Common.step > 0)
        {
            Common.step--;
            viewPager.setCurrentItem(Common.step);

            if(Common.step < 2) //always enable NEXT when step < 2
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }

        }
    }

    @OnClick (R.id.btn_next_step)
    void nextClick() {
        if(Common.step < 2 || Common.step == 0)
        {
            Common.step++;

            if(Common.step == 1) //choose machine -> pick time slot
            {
                if(Common.currentMachine != null)
                    loadTimeSlotOfMachine(Common.currentMachine.getMachineId());
            }

            if(Common.step == 2) //pick time slot -> confirm
            {
                if(Common.currentTimeSlot != -1)
                    confirmBooking();
            }

            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        //send broadcast to fragment step4
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfMachine(String machineId) {
        //send local broadcast to fragment step 2
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }


    //Broadcast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP,0);
            if(step == 0)
                Common.currentMachine = intent.getParcelableExtra(Common.KEY_MACHINE_SELECTED);
            else if(step == 1)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);

            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        ButterKnife.bind(booking.this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorButton();

        //view
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3); //3 fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int il) {

            }

            @Override
            public void onPageSelected(int i) {

                //show step
                stepView.go(i,true);

                if (i==0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                //set disable btn next
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void setColorButton() {
        if(btn_next_step.isEnabled()) {
            btn_next_step.setBackgroundResource(R.color.colorPrimaryDark);
        }
        else {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if(btn_previous_step.isEnabled()) {
            btn_previous_step.setBackgroundResource(R.color.colorPrimaryDark);
        }
        else {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("เลือกเครื่องซักผ้า");
        stepList.add("เลือกเวลา");
        stepList.add("ยืนยันการจอง");
        stepView.setSteps(stepList);
    }

}