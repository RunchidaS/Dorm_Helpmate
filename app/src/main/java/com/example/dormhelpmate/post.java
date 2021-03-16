package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class post<$> extends AppCompatActivity {

    private ImageButton selectPostImg;
    private Button postButton;
    private EditText postDescription;

    private int REQ_CODE = 100;
    private Uri imgUri;

    private CollectionReference postRef;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID = fAuth.getUid();

    private StorageReference postImgReference;

    private String description;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;

    private BottomNavigationView bottomNavigationView;

    private MySharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        sp = MySharedPreferences.getInstance(this);

        saveUserData();

        postRef = FirebaseFirestore.getInstance()
                .collection("หอพักทั้งหมด").document("123456").collection("posts");
        postImgReference = FirebaseStorage.getInstance().getReference().child("post_pics");

        postDescription = (EditText) findViewById(R.id.postLostFound);
        selectPostImg = (ImageButton) findViewById(R.id.img);
        postButton = (Button) findViewById(R.id.sendBtn);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //เปิดรูป
        selectPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_CODE);
            }
        });

        //post
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataAndSave();
            }
        });


        //Set bottom bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), home.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.user:
                        startActivity(new Intent(getApplicationContext(), user.class));
                        overridePendingTransition(0, 0);
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

    private void checkDataAndSave() {

        description = postDescription.getText().toString().trim();

        if (TextUtils.isEmpty(description)) {
            postDescription.setError("Please post something!");
            return;
        }
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
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + ";" + saveCurrentTime;

        final StorageReference Imagename = postImgReference.child("img" + imgUri.getLastPathSegment());

        //เก็บข้อมูลโพสต์ลงไปในฐานข้อมูล
        Imagename.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Map<String, Object> map = new HashMap<>();
                        map.put(constants.NAME, sp.getUserData(constants.NAME));
                        map.put(constants.PROFILE_PIC, sp.getUserData(constants.PROFILE_PIC));
                        map.put("date", saveCurrentDate);
                        map.put("time", saveCurrentTime);
                        map.put(constants.DESCRIPTION, description);
                        map.put(constants.POST_PIC, String.valueOf(uri));

                        //ตำแหน่งที่เก็บรูป
                        postRef.document(String.valueOf(System.currentTimeMillis() / 1000))
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(post.this, "Post Successfully", Toast.LENGTH_SHORT).show();
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
                String profilePic = value.getString(constants.PROFILE_PIC);
                String dorm = value.getString(constants.DORM_ID);

                sp.setUserData(constants.NAME,username);
                sp.setUserData(constants.PROFILE_PIC,profilePic);
                sp.setUserData(constants.DORM_ID,dorm);
            }
        });
    }


    @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQ_CODE && resultCode == RESULT_OK && data != null) {
                imgUri = data.getData();
                selectPostImg.setImageURI(imgUri);
            }
    }
}

