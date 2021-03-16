package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class repair extends AppCompatActivity {

    private Button sendButton;
    private EditText repairInputText;
    private BottomNavigationView bottomNavigationView;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private FirebaseAuth fAuth ;
    private FirebaseFirestore fstore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        repairInputText = (EditText) findViewById(R.id.repairText);
        sendButton = (Button) findViewById(R.id.sendBtn);

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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repairInputText.getText().toString().equals("")) {
                    Toast.makeText(repair.this,"You can't send empty text",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(repair.this, "Send Successfully", Toast.LENGTH_SHORT).show();
                    saveData();
                }
            }
        });
    }

    private  void saveData() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        DocumentReference documentReference = fstore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String name = value.getString("username");
                String floor = value.getString("floor");
                String room = value.getString("room");

                DocumentReference postRef = FirebaseFirestore.getInstance()
                        .collection("หอพักทั้งหมด")
                        .document("123456")
                        .collection("repairs")
                        .document(String.valueOf(System.currentTimeMillis() / 1000));

                Map<String, Object> map = new HashMap<>();
                map.put("username", name);
                map.put("floor", floor);
                map.put("room", room);
                map.put("text", repairInputText.getText().toString());
                map.put("date", saveCurrentDate);
                map.put("time", saveCurrentTime);

                postRef.set(map);
                repairInputText.setText("");
            }
        });
    }
}