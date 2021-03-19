package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.journalapp.model.Journal;
import com.example.journalapp.util.Journal_Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button getstarted_button;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getstarted_button = findViewById(R.id.get_started);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentuser = firebaseAuth.getCurrentUser();
//                if (currentuser != null) {
//                    currentuser = firebaseAuth.getCurrentUser();
//                    String currentuserid = currentuser.getUid();
//                    collectionReference.whereEqualTo("userid", currentuserid)
//                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
//                                                    @Nullable FirebaseFirestoreException e) {
//                                    assert queryDocumentSnapshots != null;
//                                    if (!queryDocumentSnapshots.isEmpty()) {
//                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
//                                            Journal_Api journal_api = Journal_Api.getInstance();
//                                            journal_api.setUsername(snapshot.getString("username"));
//                                            journal_api.setUserId(snapshot.getString("userid"));
//                                            startActivity(new Intent(MainActivity.this, Journal_list.class));
//                                            finish();
//                                        }
//                                    }
//                                }
//                            });
//                }
            }
        };


        getstarted_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, login_Activity.class));
            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        currentuser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
