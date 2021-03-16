package com.example.dormhelpmate.Fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dormhelpmate.Common.Common;
import com.example.dormhelpmate.Model.BookingInformation;
import com.example.dormhelpmate.R;
import com.example.dormhelpmate.home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep3Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    Unbinder unbinder;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();;
    AlertDialog dialog;

    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_machine_number_text)
    TextView txt_machine_number_text;

    @OnClick(R.id.btn_confirm)
    void confirmBooking(){

        dialog.show();

        //process timestamp
        //use timestamp to filter all booking with date id greater today
        //for only display all future booking
        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime = startTime.split("-"); //split 06:00 - 07:00

        //get start time : get 06:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); //get 06
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); //get 00

        Calendar bookingDateWithourHouse = Calendar.getInstance();
        bookingDateWithourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY,startHourInt);
        bookingDateWithourHouse.set(Calendar.MINUTE,startMinInt);

        //create timestamp obj apply to BookingInformation
        Timestamp timestamp = new Timestamp(bookingDateWithourHouse.getTime());



        //create booking info
        final BookingInformation bookingInformation = new BookingInformation();

        bookingInformation.setTimestamp(timestamp);
        bookingInformation.setDone(false);

        bookingInformation.setMachineName(Common.currentMachine.getName());
        bookingInformation.setMachineId(Common.currentMachine.getMachineId());
        bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append("  วันที่ : ")
                .append(simpleDateFormat.format(bookingDateWithourHouse.getTime())).toString());

        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

        //submit to machine document
        DocumentReference bookingData = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด")
                .document("123456")
                .collection("WashingMachine")
                .document(Common.currentMachine.getMachineId())
                .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        //write data
        bookingData.set(bookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //here we can write an function to check
                        //if already exist an booking, we will prevent new booking

                        addToUserBook(bookingInformation);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addToUserBook(BookingInformation bookingInformation) {


        //first, create new collection
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(fAuth.getCurrentUser().getUid())
                .collection("Booking");

        //check if exist doc in this collection
        //get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.getResult().isEmpty())
                        {
                            //set data
                            userBooking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            if(dialog.isShowing())
                                                dialog.dismiss();

                                            addToCalendar(Common.bookingDate,
                                                    Common.convertTimeSlotToString(Common.currentTimeSlot));

                                            resetStaticData();

                                            //close activity
                                            getActivity().finish();
                                            System.exit(0);

                                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(dialog.isShowing())
                                        dialog.dismiss();

                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            if(dialog.isShowing())
                                dialog.dismiss();

                            resetStaticData();
                            getActivity().finish(); //close activity
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime = startTime.split("-"); //split 06:00 - 07:00

        //get start time : get 06:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); //get 06
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); //get 00

        String[] endTimeConvert = convertTime[1].split(":");
        int endHourInt = Integer.parseInt(endTimeConvert[0].trim()); //get 07
        int endMinInt = Integer.parseInt(endTimeConvert[1].trim()); //get 00

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY,startHourInt); //set event start hour
        startEvent.set(Calendar.MINUTE,startMinInt); //set event start min

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY,endHourInt); //set event end hour
        endEvent.set(Calendar.MINUTE,endMinInt); //set event end min

        //after we have startEvent and endEvent, convert it to string
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime =  calendarDateFormat.format(startEvent.getTime());
        String endEventTime =  calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime,endEventTime,"Washing Machine Booking",
                new StringBuilder("ตั้งแต่เวลา ")
                .append(startTime)
                .append(" เครื่องที่ ")
                .append(Common.currentMachine.getName()).toString());


    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description) {

        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try{
            Date start = calendarDateFormat.parse(startEventTime);
            Date end = calendarDateFormat.parse(endEventTime);

            ContentValues event = new ContentValues();

            //put
            event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION,description);

            //time
            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,0);
            event.put(CalendarContract.Events.HAS_ALARM,1);

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);

            Uri calendars;
            if(Build.VERSION.SDK_INT >= 8)
                calendars = Uri.parse("content://com.android.calendar/events");
            else
                calendars = Uri.parse("content://calendar/events");

            getActivity().getContentResolver().insert(calendars,event);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getCalendar(Context context) {

        //get default calendar ID of Calendar of Gmail
        String gmailIdCalendar = "";
        String projection[] = {"_id","calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        //select all calendar
        Cursor managedCursor = contentResolver.query(calendars,projection,null,null,null);

        if(managedCursor.moveToFirst())
        {
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);

            do{
                calName = managedCursor.getString(nameCol);
                if(calName.contains("@gmail.com"))
                {
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break; //exit as soon as have id
                }
            }while (managedCursor.moveToNext());
            managedCursor.close();
        }

        return gmailIdCalendar;

    }

    private void resetStaticData() {

        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentMachine = null;
        Common.bookingDate.add(Calendar.DATE,0); //current date added

    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        txt_machine_number_text.setText(Common.currentMachine.getName());
        txt_booking_time_text.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append(" วันที่ ")
        .append(simpleDateFormat.format(Common.bookingDate.getTime())));

    }


    static BookingStep3Fragment instant;

    public static BookingStep3Fragment getInstance() {
        if(instant == null)
            instant = new BookingStep3Fragment();
        return instant;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver,new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false)
                .build();

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView =  inflater.inflate(R.layout.fragment_booking_step_three,container,false);
        unbinder = ButterKnife.bind(this,itemView);

        return itemView;

    }
}
