package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormhelpmate.Common.Common;
import com.example.dormhelpmate.Interface.IBookingInfoLoadListener;
import com.example.dormhelpmate.Model.BookingInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import dmax.dialog.SpotsDialog;

public class Washing_Machine extends AppCompatActivity implements IBookingInfoLoadListener{

    private Button bookButton, deleteButton;
    private BottomNavigationView bottomNavigationView;
    private CardView card_booking_info;
    private TextView txt_machine;
    private TextView txt_time;
    private TextView txt_time_remain;

    private AlertDialog dialog;

    private FirebaseAuth fAuth;
    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_machine);

        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        bookButton = findViewById(R.id.bookingBtn);
        deleteButton = findViewById(R.id.btn_delete_booking);

        card_booking_info = findViewById(R.id.card_booking_info);
        txt_machine = findViewById(R.id.txt_machine);
        txt_time = findViewById(R.id.txt_time);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        //เปิดหน้าจอง
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), booking.class);
                startActivity(intent1);
            }
        });

        //ลบการจอง
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
//                deleteBookingFromMachine();
            }
        });

        //data
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID)
                .collection("Booking");

        //get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //select booking info from firebase with done=false and timestamp greater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            if(!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                    BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                    onBookingInfoLoadSuccess(bookingInformation,queryDocumentSnapshot.getId());
                                    break; //exit loop as soon as


                                }
                            }
                            else
                                onBookingInfoLoadEmpty();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onBookingInfoLoadFailed(e.getMessage());
            }
        });



        //Set bottom bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(),user.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.dorm:
                        startActivity(new Intent(getApplicationContext(),dorm.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

    }

    private void showConfirmDialog() {
        //show dialog confirm
        AlertDialog confirmDialog = new AlertDialog.Builder(Washing_Machine.this).create();
        confirmDialog.setCancelable(false);
        confirmDialog.setMessage("คุณต้องการลบการจองคิวนี้ใช่หรือไม่ ?");

        confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ยกเลิก",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ตกลง",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookingFromMachine();
                    }
                });

        confirmDialog.show();
    }

    private void deleteBookingFromMachine() {
        /* to delete booking , first we need delete from machine collection
        after that, we will delete from user booking collection
        and after, delete event
         */


        //we need load Common.currentBooking because we need some data from BookingInformation
        if(Common.currentBooking != null)
        {

            dialog.show();

            //get booking info in machine obj
            DocumentReference machineBookingInfo = FirebaseFirestore.getInstance()
                    .collection("หอพักทั้งหมด")
                    .document("123456")
                    .collection("WashingMachine")
                    .document(Common.currentBooking.getMachineId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());

            //when we have document , just delete it
            machineBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Washing_Machine.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //after delete on machine done
                    //we will start delete from user
                    deleteBookingFromUser();

                }
            });

        }
        else
        {
            Toast.makeText(this, "Current Booking must not be null", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingFromUser() {
        //first, get info from user obj
        if(!TextUtils.isEmpty(Common.currentBookingId))
        {
            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("หอพักทั้งหมด")
                    .document("123456")
                    .collection("users")
                    .document(fAuth.getCurrentUser().getUid())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            //delete
            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Washing_Machine.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(Washing_Machine.this, "Success delete booking !", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                    //refresh
                    card_booking_info.setVisibility(View.GONE);
                }
            });
        }
        else
        {
            dialog.dismiss();
            Toast.makeText(this, "Booking Information ID must not be empty", Toast.LENGTH_SHORT).show();
        }


    }

    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookingId) {

        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;

        txt_machine.setText(bookingInformation.getMachineName());
        txt_time.setText(bookingInformation.getTime());

//        String dateRemain = DateUtils.getRelativeTimeSpanString(
//                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
//                Calendar.getInstance().getTimeInMillis(),0).toString();
//        txt_time_remain.setText(dateRemain);

        card_booking_info.setVisibility(View.VISIBLE);

        dialog.dismiss();

    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }


}