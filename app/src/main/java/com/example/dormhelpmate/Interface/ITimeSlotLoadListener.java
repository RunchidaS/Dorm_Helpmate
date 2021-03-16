package com.example.dormhelpmate.Interface;

import com.example.dormhelpmate.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {

    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();



}
