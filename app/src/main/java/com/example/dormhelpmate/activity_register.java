package com.example.dormhelpmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dormhelpmate.storage.MySharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class activity_register extends AppCompatActivity {
    private EditText username, email, password, phone, floor, room, code;
    private Button registerBtn;
    private TextView loginBtn;
    private String n,e,p,ph,c,f,r;
    public static final String TAG = "TAG";

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private MySharedPreferences sp;

    private CollectionReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText)findViewById(R.id.rName);
        email = findViewById(R.id.rEmail);
        password = findViewById(R.id.rPassword);
        phone = findViewById(R.id.rPhone);
        code = findViewById(R.id.code);
        floor = findViewById(R.id.floor);
        room = findViewById(R.id.room);

        registerBtn = findViewById(R.id.button3);
        loginBtn = findViewById(R.id.textView12);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userRef = FirebaseFirestore.getInstance().collection("หอพักทั้งหมด");


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataAndLogin();

            }

        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }

    private void checkDataAndLogin(){

        e = email.getText().toString().trim();
        p = password.getText().toString().trim();
        n = username.getText().toString().trim();
        ph = phone.getText().toString().trim();
        c = code.getText().toString().trim();
        f = floor.getText().toString().trim();
        r = room.getText().toString().trim();


        //เงื่อนไข
        if(TextUtils.isEmpty(n)){
            username.setError("Username is Required"); return;
        }
        if(TextUtils.isEmpty(e)){
            email.setError("Email is Required"); return;
        }
        if(TextUtils.isEmpty(p)){
            password.setError("Password is Required"); return;
        }
        if(p.length() < 8){
            password.setError("Password Must Be >= 8 Characters"); return;
        }
        if(TextUtils.isEmpty(ph)){
            phone.setError("Phone is Required"); return;
        }
        if(ph.length() != 10){
            phone.setError("Phone Number Must Be 10 digits"); return;
        }
        if(TextUtils.isEmpty(c)){
            code.setError("Dorm-ID is Required"); return;
        }
        if(TextUtils.isEmpty(f)){
            floor.setError("Floor is Required"); return;
        }
        if(TextUtils.isEmpty(r)){
            room.setError("Room is Required"); return;
        }

        else{

            //register the user in firebase
            fAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                    }else {
                        saveUserData();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }
            });
        }
    }

    private void saveUserData(){
        Map<String,String> map = new HashMap<>();
        map.put("dorm_id",c);
        map.put("user_id",fAuth.getUid());
        map.put("username",n);
        map.put("email",e);
        map.put("phone",ph);
        map.put("floor",f);
        map.put("room",r);
        userRef.document(c).collection("users").document(fAuth.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}