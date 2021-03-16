package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormhelpmate.storage.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn, registerBtn ;
    private EditText email, password ;
    private FirebaseAuth fAuth ;

    private  MySharedPreferences sp;

    private  static final String TAG = "FACELOG";

    public static final String CHANNEL_ID = "simplified_coding";
    private static final String CHANNEL_NAME = "Simplified Coding";
    private static final String CHANNEL_DESC = "Simplified Coding Notification";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //        registerBtn = findViewById(R.id.button2);
        loginBtn = findViewById(R.id.button);
        email = findViewById(R.id.lEmail);
        password = findViewById(R.id.lPassword);
        fAuth = FirebaseAuth.getInstance();

        sp = MySharedPreferences.getInstance(this);

        //notification

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


//        //เปิดหน้า register
//        registerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(getApplicationContext(),activity_register.class);
//                startActivity(intent1);
//            }
//        });

        //เปิดหน้า home
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String e = email.getText().toString().trim();
                String p = password.getText().toString().trim();

                if(TextUtils.isEmpty(e)){
                    email.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(p)){
                    password.setError("Password is Required");
                    return;
                }

                if(p.length() < 8) {
                    password.setError("Password Must Be >= 8 Characters");
                    return;
                }

                //Authenticate the user

                fAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
//                            sp.setLogin("2");
                            Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();

                            //startActivity(new Intent(getApplicationContext(),home.class));
                            Intent intent = new Intent(getApplicationContext(), home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                        }else {
                            Toast.makeText(MainActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser != null) {
            updateUI();
        }
    }

    private void updateUI() {
        Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_LONG).show();

        Intent accountIntent = new Intent(MainActivity.this, home.class);
        startActivity(accountIntent);
        finish();
    }


}