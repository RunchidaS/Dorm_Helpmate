package com.example.dormhelpmate.notificationData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormhelpmate.PostData.PostAdapter;
import com.example.dormhelpmate.R;
import com.example.dormhelpmate.notificationData.notiData;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class notiAdapter extends FirestoreRecyclerAdapter<notiData, notiAdapter.notiHolder> {

    public notiAdapter(@NonNull FirestoreRecyclerOptions<notiData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull notiHolder holder, int position, @NonNull notiData model) {
        holder.title.setText(model.getTitle());
        holder.body.setText(model.getBody());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
    }

    @NonNull
    @Override
    public notiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_layout,parent,false);
        return new notiHolder(v);
    }

    class notiHolder extends RecyclerView.ViewHolder {

        TextView title, body, date, time;

        public notiHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}