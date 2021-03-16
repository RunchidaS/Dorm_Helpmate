package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dormhelpmate.PostData.PostAdapter;
import com.example.dormhelpmate.PostData.PostData;
import com.example.dormhelpmate.faqData.faqAdapter;
import com.example.dormhelpmate.faqData.faqData;
import com.example.dormhelpmate.storage.MySharedPreferences;
import com.example.dormhelpmate.storage.constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class faq extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference faqRef;

    private faqAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        faqRef = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("FAQ");

        setUpRecyclerView();


        //Set bottom bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(),user.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.dorm:
                        startActivity(new Intent(getApplicationContext(),dorm.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<faqData> options = new FirestoreRecyclerOptions.Builder<faqData>()
                .setQuery(faqRef, faqData.class).build();

        adapter = new faqAdapter(options);

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