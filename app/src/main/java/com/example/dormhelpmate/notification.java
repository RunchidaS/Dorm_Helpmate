package com.example.dormhelpmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.dormhelpmate.notificationData.notiAdapter;
import com.example.dormhelpmate.notificationData.notiData;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class notification extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userID;
    private CollectionReference notiRef;

    private notiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        userID = fAuth.getCurrentUser().getUid();

        notiRef = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID)
                .collection("notification");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<notiData> options = new FirestoreRecyclerOptions.Builder<notiData>()
                .setQuery(notiRef, notiData.class).build();

        adapter = new notiAdapter(options);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }
}