package com.example.dormhelpmate.Common;

import com.example.dormhelpmate.CommentData.CommentData;
import com.example.dormhelpmate.Model.BookingInformation;
import com.example.dormhelpmate.Model.Machine;
import com.example.dormhelpmate.Model.TimeSlot;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_MACHINE = "MACHINE_SAVE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_MACHINE_SELECTED = "MACHINE_SELECTED";
    public static final int TIME_SLOT_TOTAL = 12;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static String IS_LOGIN = "IsLogin";
    public static User currentUser;
    public static Machine currentMachine;
    public static int step = 0; //first step
    public static int currentTimeSlot = -1;
    public static Calendar bookingDate = Calendar.getInstance();

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static BookingInformation currentBooking;
    public static String currentBookingId = "";

    public static String convertTimeSlotToString(int slot) {
        switch (slot)
        {
            case 0:
                return "06:00 - 07:30";
            case 1:
                return "07:30 - 09:00";
            case 2:
                return "09:00 - 10:30";
            case 3:
                return "10:30 - 12:00";
            case 4:
                return "12:00 - 13:30";
            case 5:
                return "13:30 - 15:00";
            case 6:
                return "15:00 - 16:30";
            case 7:
                return "16:30 - 18:00";
            case 8:
                return "18:00 - 19:30";
            case 9:
                return "19:30 - 21:00";
            case 10:
                return "21:00 - 22:30";
            case 11:
                return "22:30 - 00:00";
            default:
                return "Closed";
        }
    }

    public static String convertTimeStampToStringKey(Timestamp timestamp) {

        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);

    }
}
