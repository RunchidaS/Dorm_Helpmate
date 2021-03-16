package com.example.dormhelpmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dormhelpmate.PostData.PostAdapter;
import com.example.dormhelpmate.PostData.PostData;
import com.example.dormhelpmate.storage.MySharedPreferences;
import com.example.dormhelpmate.storage.constants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class lostfound extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference postRef;

    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfound);

        postRef = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("posts");

        setUpRecyclerView();


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
        FirestoreRecyclerOptions<PostData> options = new FirestoreRecyclerOptions.Builder<PostData>()
                .setQuery(postRef, PostData.class).build();

        adapter = new PostAdapter(options);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.startListening();

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                PostData post = documentSnapshot.toObject(PostData.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                Toast.makeText(lostfound.this,"Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(lostfound.this, comment.class);
                intent.putExtra("postID",id);
                startActivity(intent);

            }
        });
    }


    //add post button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addpost, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPost:
                startActivity(new Intent(getApplicationContext(),post.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}