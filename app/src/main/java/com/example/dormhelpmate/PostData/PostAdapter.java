package com.example.dormhelpmate.PostData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormhelpmate.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PostAdapter extends FirestoreRecyclerAdapter<PostData, PostAdapter.PostHolder> {


    private OnItemClickListener listener;

    public PostAdapter(@NonNull FirestoreRecyclerOptions<PostData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull PostData model) {
        holder.name.setText(model.getName());
        holder.date.setText(model.getDate());
        holder.time.setText(model.getTime());
        holder.description.setText(model.getDescription());

        Picasso.get().load(model.getProfile_pic()).into(holder.profile);
        Picasso.get().load(model.getPost_pic()).into(holder.postPicture);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_layout,parent,false);
        return new PostHolder(v);
    }

    class PostHolder extends RecyclerView.ViewHolder {

        TextView name,date,time,description;
        ImageView postPicture,profile;
        ImageButton comment;

        public PostHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
            profile = itemView.findViewById(R.id.profilePic);
            postPicture = itemView.findViewById(R.id.postPic);
            comment = itemView.findViewById(R.id.comment_btn);

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
