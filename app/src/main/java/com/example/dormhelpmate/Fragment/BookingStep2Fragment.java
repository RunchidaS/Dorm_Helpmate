package com.example.dormhelpmate.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormhelpmate.Adapter.MyTimeSlotAdapter;
import com.example.dormhelpmate.Common.Common;
import com.example.dormhelpmate.Common.SpacesItemDecoration;
import com.example.dormhelpmate.Interface.ITimeSlotLoadListener;
import com.example.dormhelpmate.Model.TimeSlot;
import com.example.dormhelpmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class BookingStep2Fragment extends Fragment implements ITimeSlotLoadListener {

    //var
    DocumentReference machineDoc;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;

    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;

    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE,0); //add current date
            loadAvailableTimeSlotOfMachine(Common.currentMachine.getMachineId(),
                    simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailableTimeSlotOfMachine(String machineId, final String bookDate) {
        dialog.show();

        machineDoc = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด")
                .document("123456")
                .collection("WashingMachine")
                .document(Common.currentMachine.getMachineId());

        machineDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                     if(documentSnapshot.exists()) {
                         //get info of booking
                         CollectionReference date = FirebaseFirestore.getInstance()
                                 .collection("หอพักทั้งหมด")
                                 .document("123456")
                                 .collection("WashingMachine")
                                 .document(Common.currentMachine.getMachineId())
                                 .collection(bookDate);

                         date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if(task.isSuccessful())
                                 {
                                     QuerySnapshot querySnapshot = task.getResult();
                                     if(querySnapshot.isEmpty())//don't have booking
                                     {
                                         iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                     }

                                     else //if have
                                      {
                                         List<TimeSlot> timeSlots = new ArrayList<>();
                                         for(QueryDocumentSnapshot document:task.getResult())
                                             timeSlots.add(document.toObject(TimeSlot.class));
                                         iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                     }
                                 }
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                             }
                         });
                     }
                }
            }
        });
    }


    static BookingStep2Fragment instant;

    public static BookingStep2Fragment getInstance() {
        if(instant == null)
            instant = new BookingStep2Fragment();
        return instant;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListener = this;

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_two,container,false);
        unbinder = ButterKnife.bind(this,itemView);
        init(itemView);
        return itemView;

    }

    private void init(View itemView) {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));

        //Calendar
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE,0);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE,1);  //1 day left

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(itemView,R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.bookingDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.bookingDate = date; //this code will not load again if you select new day same with day selected
                    loadAvailableTimeSlotOfMachine(Common.currentMachine.getMachineId(),
                            simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(),timeSlotList);
        recycler_time_slot.setAdapter(adapter);
        recycler_time_slot.setVisibility(View.VISIBLE);
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);
        dialog.dismiss();
    }
}
