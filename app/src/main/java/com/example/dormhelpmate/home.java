package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormhelpmate.storage.MySharedPreferences;
import com.example.dormhelpmate.storage.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home extends AppCompatActivity {
    private TextView logout,username,dorm,room;
    private CardView c1,c2,c3,c4,c5 ;
    private ImageView profile;
    private FirebaseAuth fAuth ;
    private FirebaseFirestore fstore;
    private String userID;
    private BottomNavigationView bottomNavigationView;
    private long backPressedTime;

    private int REQ_CODE = 100;
    private int PERMISSION_REQ_CODE = 200;
    private  String permission[] = {Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR,
                                    Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestFunctions();

        //notification
        FirebaseMessaging.getInstance().subscribeToTopic("updates");

        //get token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
//                            Log.w(TAG , "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        saveToken(token);

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        logout = findViewById(R.id.logoutBtn);
        profile = findViewById(R.id.profileImg);
        username = findViewById(R.id.nameText);
        dorm = findViewById(R.id.dormText);
        room = findViewById(R.id.roomText);
        c1 = findViewById(R.id.lostfoundCard);
        c2 = findViewById(R.id.wmCard);
        c3 = findViewById(R.id.billCard);
        c4 = findViewById(R.id.reportCard);
        c5 = findViewById(R.id.faqCard);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
                room.setText(value.getString("room"));
                String link = value.getString("profile_pic");
                Picasso.get().load(link).into(profile);
            }
        });



        DocumentReference dormReference = fstore.collection("หอพักทั้งหมด")
                .document("123456");

        dormReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                dorm.setText(value.getString("ชื่อหอพัก"));
            }
        });


        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        return true;

                    case R.id.user:
                        Intent intent = new Intent(home.this, user.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.dorm:
                        Intent intent2 = new Intent(home.this, dorm.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });



        //เปิดหน้า lost&found
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),lostfound.class);
                startActivity(intent1);
            }
        });

        //เปิดหน้าจองคิวเครื่องซักผ้า
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(),Washing_Machine.class);
                startActivity(intent2);
            }
        });

        //เปิดหน้าบิลค่าเช่า
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(getApplicationContext(),bill.class);
                startActivity(intent3);
            }
        });

        //เปิดหน้าแจ้งซ่อมร้องเรียน
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(getApplicationContext(),report.class);
                startActivity(intent4);
            }
        });

        //เปิดหน้า faq
        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent(getApplicationContext(),faq.class);
                startActivity(intent5);
            }
        });

        //logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });


    }

    private void requestFunctions(){
        if(ActivityCompat.checkSelfPermission(this, permission[0])!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(home.this, permission, PERMISSION_REQ_CODE );
        }
    }

    private void showConfirmDialog() {
        //show dialog confirm
        AlertDialog confirmDialog = new AlertDialog.Builder(home.this).create();
        confirmDialog.setCancelable(false);
        confirmDialog.setMessage("คุณต้องการออกจากระบบใช่หรือไม่?");

        confirmDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "ยกเลิก",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        confirmDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ตกลง",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fAuth.signOut();
                        updateUI();
                    }
                });

        confirmDialog.show();
    }

    private void saveToken(String token) {

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);

        DocumentReference documentReference = fstore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID);

        documentReference.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

//                        Toast.makeText(home.this, "Token Saved", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    //log out
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser == null) {
            updateUI();
        }
    }

    private void updateUI() {

        Toast.makeText(home.this, "Logged out Successfully!", Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(home.this, MainActivity.class);
        startActivity(accountIntent);
        finish();
    }

    //notification
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                startActivity(new Intent(getApplicationContext(),notification.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
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