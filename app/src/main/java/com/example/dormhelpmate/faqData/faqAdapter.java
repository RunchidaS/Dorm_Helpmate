package com.example.dormhelpmate.faqData;

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

public class faqAdapter extends FirestoreRecyclerAdapter<faqData, faqAdapter.faqHolder> {

    public faqAdapter(@NonNull FirestoreRecyclerOptions<faqData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull faqHolder holder, int position, @NonNull faqData model) {
        holder.question.setText(model.getQuestion());
        holder.answer.setText(model.getAnswer());
    }

    @NonNull
    @Override
    public faqHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_list_layout,parent,false);
        return new faqHolder(v);
    }

    class faqHolder extends RecyclerView.ViewHolder {

        TextView question,answer;

        public faqHolder(View itemView){
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }
    }
}
