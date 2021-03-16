package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class report extends AppCompatActivity {
    Button repairBtn, appealBtn ;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        repairBtn = findViewById(R.id.repairButton);
        appealBtn = findViewById(R.id.appealButton);

        //เปิดหน้าแจ้งซ่อม
        repairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),repair.class);
                startActivity(intent);
            }
        });

        //เปิดหน้าร้องเรียน
        appealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(),appeal.class);
                startActivity(intent2);
            }
        });

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
}