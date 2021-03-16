package com.example.dormhelpmate.Interface;

import com.example.dormhelpmate.Model.BookingInformation;

public interface IBookingInfoLoadListener {

    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String documentId);
    void onBookingInfoLoadFailed(String message);


}
