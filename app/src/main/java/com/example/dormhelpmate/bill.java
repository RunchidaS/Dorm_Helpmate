package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class bill extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView billImage;
    private TextView month,status;

    private int REQ_CODE = 100;
    private int PERMISSION_REQ_CODE = 200;
    private Uri imgUri;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private StorageReference storageReference;

    private  String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        requestFunctions();

        month = findViewById(R.id.month);
        status = findViewById(R.id.status);
        billImage = findViewById(R.id.billImg);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("bill_pics");
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference billRef = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("bills")
                .document(userID);


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

        //set data
        billRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                month.setText(value.getString("month"));
                status.setText(value.getString("status"));

                String link = value.getString("bill");
                Picasso.get().load(link).into(billImage);
            }
        });

    }

    private void requestFunctions(){
        if(ActivityCompat.checkSelfPermission(this, permission[0])!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(bill.this, permission, PERMISSION_REQ_CODE );
        }
    }

    //add slip button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_slip, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSlip:
                startActivity(new Intent(getApplicationContext(),slip.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}