package com.example.dormhelpmate.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormhelpmate.Adapter.MyMachineAdapter;
import com.example.dormhelpmate.Common.SpacesItemDecoration;
import com.example.dormhelpmate.Interface.IMachineLoadListener;
import com.example.dormhelpmate.Model.Machine;
import com.example.dormhelpmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingStep1Fragment extends Fragment implements IMachineLoadListener {

    //variable
    CollectionReference machineRef;
    IMachineLoadListener iMachineLoadListener;

    @BindView(R.id.recycler_machine)
    RecyclerView recycler_machine;

    Unbinder unbinder;

    AlertDialog dialog;

    static BookingStep1Fragment instant;

    public static BookingStep1Fragment getInstance() {
        if(instant == null)
            instant = new BookingStep1Fragment();
        return instant;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iMachineLoadListener = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView =  inflater.inflate(R.layout.fragment_booking_step_one,container,false);
        unbinder = ButterKnife.bind(this,itemView);

        initView();
        loadAllMachine ();

        return itemView;
    }

    private void initView() {
        recycler_machine.setHasFixedSize(true);
        recycler_machine.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_machine.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllMachine () {
        dialog.show();

        machineRef = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด")
                .document("123456")
                .collection("WashingMachine");

        machineRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<Machine> list = new ArrayList<>();

                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()) {
                        Machine machine = documentSnapshot.toObject(Machine.class);
                        machine.setMachineId(documentSnapshot.getId());
                        list.add(machine);
                    }

                    iMachineLoadListener.onMachineLoadSuccess(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iMachineLoadListener.onMachineLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onMachineLoadSuccess(List<Machine> numberNameList) {
        MyMachineAdapter adapter = new MyMachineAdapter(getActivity(),numberNameList);
        recycler_machine.setAdapter(adapter);
        recycler_machine.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onMachineLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
