package com.example.dormhelpmate.Interface;

import com.example.dormhelpmate.Model.Machine;

import java.util.List;

public interface IMachineLoadListener {

    void onMachineLoadSuccess(List<Machine> numberNameList);
    void onMachineLoadFailed(String message);

}
