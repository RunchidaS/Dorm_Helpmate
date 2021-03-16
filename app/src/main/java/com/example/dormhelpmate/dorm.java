package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormhelpmate.storage.MySharedPreferences;
import com.example.dormhelpmate.storage.constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;

public class dorm extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView dormName,address,email,phone;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fstore;
    private String userID;
    private DocumentReference dormRef;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dorm);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        dormName = findViewById(R.id.dormName);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set data
        dormRef = fstore.collection("หอพักทั้งหมด")
                .document("123456");

        dormRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                dormName.setText(value.getString("ชื่อหอพัก"));
                address.setText(value.getString("ที่อยู่หอพัก"));
                email.setText(value.getString("dorm_email"));
                phone.setText(value.getString("dorm_phone"));
            }
        });

        //Set dorm Selected
        bottomNavigationView.setSelectedItemId(R.id.dorm);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        Intent intent2 = new Intent(dorm.this, user.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.dorm:
                        return true;

                }
                return false;
            }
        });
    }

    //exit app
    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

}

