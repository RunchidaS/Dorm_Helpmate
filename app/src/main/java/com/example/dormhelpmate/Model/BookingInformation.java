package com.example.dormhelpmate.Model;

import com.google.firebase.Timestamp;

public class BookingInformation {

    private  String time, machineName, machineId;
    private Long slot;
    private Timestamp timestamp;
    private boolean done;

    public BookingInformation() {
    }

    public BookingInformation(String time, String machineName, String machineId, Long slot) {
        this.time = time;
        this.machineName = machineName;
        this.machineId = machineId;
        this.slot = slot;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public void setDone(boolean b) {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

}
