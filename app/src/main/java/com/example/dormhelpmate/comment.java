package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dormhelpmate.CommentData.CommentAdapter;
import com.example.dormhelpmate.CommentData.CommentData;
import com.example.dormhelpmate.storage.constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class comment extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton postCommentBtn;
    private EditText CommentInputText;
    private BottomNavigationView bottomNavigationView;
    private String saveCurrentDate;
    private String saveCurrentTime;

    private FirebaseAuth fAuth ;
    private FirebaseFirestore fstore;
    private String userID,postID;

    private CollectionReference commentRef;

    private CommentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        CommentInputText = (EditText) findViewById(R.id.comment);
        postCommentBtn = (ImageButton) findViewById(R.id.post_comment_btn);

        postID = getIntent().getExtras().get("postID").toString();

        commentRef = fstore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("posts")
                .document(postID)
                .collection("comments");

        setUpRecyclerView();

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommentInputText.getText().toString().equals("")) {
                    Toast.makeText(comment.this,"You can't send empty comment",Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
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

    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<CommentData> options = new FirestoreRecyclerOptions.Builder<CommentData>()
                .setQuery(commentRef, CommentData.class).build();

        adapter = new CommentAdapter(options);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    private  void addComment() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
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
                String username = value.getString("username");
                String link = value.getString("profile_pic");

                Map<String, Object> map = new HashMap<>();
                map.put(constants.NAME, username);
                map.put(constants.PROFILE_PIC, link);
                map.put("comment", CommentInputText.getText().toString());
                map.put("user_id", userID);
                map.put("date", saveCurrentDate);
                map.put("time", saveCurrentTime);

                commentRef.document(String.valueOf(System.currentTimeMillis() / 1000)).set(map);

                CommentInputText.setText("");
            }
        });
    }
}