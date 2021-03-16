package com.example.dormhelpmate.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Machine implements Parcelable {
    private String name,machineId;

    public Machine() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    protected Machine(Parcel in) {
        name = in.readString();
        machineId = in.readString();
    }

    public static final Creator<Machine> CREATOR = new Creator<Machine>() {
        @Override
        public Machine createFromParcel(Parcel in) {
            return new Machine(in);
        }

        @Override
        public Machine[] newArray(int size) {
            return new Machine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(machineId);
    }
}
