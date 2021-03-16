package com.example.dormhelpmate.CommentData;

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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class CommentAdapter extends FirestoreRecyclerAdapter<CommentData, CommentAdapter.CommentHolder> {

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<CommentData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentHolder holder, int position, @NonNull CommentData model) {
        holder.name.setText(model.getName());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.comment.setText(model.getComment());
        Picasso.get().load(model.getProfile_pic()).into(holder.profile);
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_layout,parent,false);
        return new CommentHolder(v);
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        TextView name,date,time,comment;
        ImageView profile;

        public CommentHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.comment);
            profile = itemView.findViewById(R.id.profilePic);

        }
    }

}
