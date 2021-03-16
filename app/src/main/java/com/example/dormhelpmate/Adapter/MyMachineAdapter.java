package com.example.dormhelpmate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormhelpmate.Common.Common;
import com.example.dormhelpmate.Interface.IRecyclerItemSelectedListener;
import com.example.dormhelpmate.Model.Machine;
import com.example.dormhelpmate.R;

import java.util.ArrayList;
import java.util.List;

public class MyMachineAdapter extends RecyclerView.Adapter<MyMachineAdapter.MyViewHolder> {

    Context context;
    List<Machine> machineList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyMachineAdapter(Context context, List<Machine> machineList) {
        this.context = context;
        this.machineList = machineList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_machine,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_machine_name.setText(machineList.get(i).getName());

        if(!cardViewList.contains(myViewHolder.card_machine))
            cardViewList.add(myViewHolder.card_machine);

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //set white background for all card not be selected
                for(CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //set selected BG for only selected item
                myViewHolder.card_machine.setCardBackgroundColor(context.getResources()
                .getColor(android.R.color.holo_orange_light));

                //send broadcast to tell booking activity enable button next
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_MACHINE_SELECTED,machineList.get(pos));
                intent.putExtra(Common.KEY_STEP,0);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_machine_name;
        CardView card_machine;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(View itemView) {
            super(itemView);

            card_machine = (CardView)itemView.findViewById(R.id.card_machine);
            txt_machine_name = (TextView)itemView.findViewById(R.id.txt_machine_name);

            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
