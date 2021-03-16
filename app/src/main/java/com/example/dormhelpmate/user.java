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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dormhelpmate.storage.MySharedPreferences;
import com.example.dormhelpmate.storage.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class user extends AppCompatActivity
        implements UsernameDialog.UsernameDialogListener,
                    PhoneDialog.PhoneDialogListener,
                    PasswordDialog.PasswordDialogListener{

    private BottomNavigationView bottomNavigationView;

    private ImageView profile;
    private TextView username,email,phone,dorm,floor,room;
    private ImageButton profileImgSave,usernameEdit,phoneEdit,passwordEdit;

    private int REQ_CODE = 100;
    private int PERMISSION_REQ_CODE = 200;
    private Uri imgUri;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private DocumentReference userRef;
    private String userID;
    private StorageReference storageReference;

    private long backPressedTime;

    private  String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        requestFunctions();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pics");
        userID = fAuth.getCurrentUser().getUid();

        userRef = FirebaseFirestore.getInstance().collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profile = findViewById(R.id.profileImg);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        dorm = findViewById(R.id.dorm);
        floor = findViewById(R.id.floor);
        room = findViewById(R.id.room);
        profileImgSave = findViewById(R.id.profileImgSaveBtn);
        usernameEdit = findViewById(R.id.usernameEditBtn);
        phoneEdit = findViewById(R.id.phoneEditBtn);
        passwordEdit = findViewById(R.id.passwordEditBtn);


        //set data
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
                email.setText(value.getString("email"));
                phone.setText(value.getString("phone"));
                floor.setText(value.getString("floor"));
                room.setText(value.getString("room"));
                String link = value.getString("profile_pic");
                Picasso.get().load(link).into(profile);
            }
        });

        DocumentReference dormReference = fStore.collection("หอพักทั้งหมด")
                .document("123456");

        dormReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                dorm.setText(value.getString("ชื่อหอพัก"));
            }
        });

        //edit data
        usernameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogEditUsername();
            }
        });

        phoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogEditPhone();
            }
        });
        passwordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogEditPassword();
            }
        });

        //เปลี่ยนรูป
        profileImgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDataAndSave();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_CODE);
            }
        });

        //Set user Selected
        bottomNavigationView.setSelectedItemId(R.id.user);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),home.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.user:
                        return true;

                    case R.id.dorm:
                        Intent intent2 = new Intent(user.this, dorm.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }

    private void requestFunctions(){
        if(ActivityCompat.checkSelfPermission(this, permission[0])!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(user.this, permission, PERMISSION_REQ_CODE );
        }
    }

    private void checkDataAndSave() {
        if(imgUri==null){
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQ_CODE && resultCode==RESULT_OK && data!=null) {
            imgUri = data.getData();
            profile.setImageURI(imgUri);

            final StorageReference Imagename = storageReference.child("img"+imgUri.getLastPathSegment());
            Imagename.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Map<String,Object> map = new HashMap<>();
                            map.put("profile_pic",String.valueOf(uri));
                            userRef.update(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Profile Picture is saved",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    });
                }
            });
        }
    }



    //change username
    public void openDialogEditUsername() {
        UsernameDialog usernameDialog = new UsernameDialog();
        usernameDialog.show(getSupportFragmentManager(), "username dialog");
    }
    @Override
    public void applyUsernameText(String username) {
        this.username.setText(username);

        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        userRef.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Save Change",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //change phone
    public void openDialogEditPhone() {
        PhoneDialog phoneDialog = new PhoneDialog();
        phoneDialog.show(getSupportFragmentManager(), "phone dialog");
    }
    @Override
    public void applyPhoneText(String phone) {
        this.phone.setText(phone);

        Map<String,Object> map = new HashMap<>();
        map.put("phone",phone);
        userRef.update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Save Change",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //change password
    public void openDialogEditPassword() {
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.show(getSupportFragmentManager(), "password dialog");
    }
    @Override
    public void applyPasswordText(EditText cPasswordEt, EditText nPasswordEt) {
        String currentPassword = cPasswordEt.getText().toString().trim();
        String newPassword = nPasswordEt.getText().toString().trim();

        if(TextUtils.isEmpty(currentPassword)){
            Toast.makeText(getApplicationContext(), "Enter your current password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(newPassword.length()<8){
            Toast.makeText(getApplicationContext(), "Password length must atleast 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        //get current user
        FirebaseUser user = fAuth.getCurrentUser();

        //before changing password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),currentPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successfully authenticated, begin update
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //password updated
                                        Toast.makeText(getApplicationContext(),"Password Updated",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //authentication failed, show reason
                        Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //exit app
    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }


}