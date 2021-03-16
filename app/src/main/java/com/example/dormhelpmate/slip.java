package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dormhelpmate.storage.MySharedPreferences;
import com.example.dormhelpmate.storage.constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class slip extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageButton selectSlipImg;
    private Button sentSlipButton;

    private int REQ_CODE = 100;
    private Uri imgUri;
    private MySharedPreferences sp;

    private CollectionReference slipRef;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID = fAuth.getUid();

    private StorageReference slipImgReference;

    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;

    private  String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slip);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        sp = MySharedPreferences.getInstance(this);

        saveUserData();

        slipRef = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด").document("123456").collection("slips");

        slipImgReference = FirebaseStorage.getInstance().getReference().child("slip_pics");

        selectSlipImg = (ImageButton) findViewById(R.id.AddSlipBtn);
        sentSlipButton = (Button) findViewById(R.id.sendBtn);

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

        //เปิดรูป
        selectSlipImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_CODE);
            }
        });

        //send
        sentSlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataAndSave();
            }
        });
    }

    private void checkDataAndSave() {


        if (imgUri == null) {
            Toast.makeText(getApplicationContext(), "Please select photo!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            uploadImage();
        }

    }

    private void uploadImage() {
        //เวลาปัจจุบัน
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + ";" + saveCurrentTime;

        DocumentReference documentReference = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID);

        final StorageReference Imagename = slipImgReference.child("img" + imgUri.getLastPathSegment());

        //เก็บข้อมูล slip ลงไปในฐานข้อมูล
        Imagename.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Map<String, Object> map = new HashMap<>();
                        map.put(constants.NAME, sp.getUserData(constants.NAME));
                        map.put("date", saveCurrentDate);
                        map.put("time", saveCurrentTime);
                        map.put("slip", String.valueOf(uri));

                        //ตำแหน่งที่เก็บรูป
                        slipRef.document(String.valueOf(System.currentTimeMillis() / 1000))
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(slip.this, "Slip is sent successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });
    }

    private void  saveUserData() {

        DocumentReference documentReference = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String username = value.getString(constants.NAME);
                sp.setUserData(constants.NAME,username);
            }
        });
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            selectSlipImg.setImageURI(imgUri);
        }
    }

}