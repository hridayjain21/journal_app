package com.example.journalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.journalapp.util.Journal_Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateaccountActivity extends AppCompatActivity {

        private Button loginButton;
        private Button createAcctButton;
        private FirebaseAuth firebaseAuth;
        private FirebaseAuth.AuthStateListener authStateListener;
        private FirebaseUser currentUser;

        //Firestore connection
        private FirebaseFirestore ab = FirebaseFirestore.getInstance();

        private CollectionReference collectionReference = ab.collection("users");


        private EditText emailEditText;
        private EditText passwordEditText;
        private ProgressBar progressBar;
        private EditText userNameEditText;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_createaccount_activity);

            firebaseAuth = FirebaseAuth.getInstance();

            createAcctButton = findViewById(R.id.create_account);
            progressBar = findViewById(R.id.progress_bar);
            emailEditText = findViewById(R.id.enter_email);
            passwordEditText = findViewById(R.id.enter_password);
            userNameEditText = findViewById(R.id.username);
            Objects.requireNonNull(getSupportActionBar()).setElevation(0);
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    currentUser = firebaseAuth.getCurrentUser();

                    if (currentUser != null) {
                        //user is already loggedin..
                    }else {
                        //no user yet...
                    }

                }
            };

            createAcctButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(emailEditText.getText().toString())
                            && !TextUtils.isEmpty(passwordEditText.getText().toString())
                            && !TextUtils.isEmpty(userNameEditText.getText().toString())) {

                        String email = emailEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        String username = userNameEditText.getText().toString().trim();

                        createUserEmailAccount(email, password, username);

                    }else {
                        Toast.makeText(CreateaccountActivity.this,
                                "Empty Fields Not Allowed",
                                Toast.LENGTH_LONG)
                                .show();
                    }



                }
            });

        }
    private void createUserEmailAccount(String email, String password, final String username) {
            if (!TextUtils.isEmpty(email)
                    && !TextUtils.isEmpty(password)
                    && !TextUtils.isEmpty(username)) {


                  progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("added", "added successfully");
                                    currentUser = firebaseAuth.getCurrentUser();
                                    assert currentUser != null;
                                    currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(CreateaccountActivity.this,"Email Verification has been " +
                                                        "sent to your email.\nplease verify your email.",Toast.LENGTH_LONG).show();

                                                final String currentUserId = currentUser.getUid();

                                                //Create a user Map so we can create a user in the User collection
                                                Map<String, String> userObj = new HashMap<>();
                                                userObj.put("userid", currentUserId);
                                                userObj.put("username", username);

                                                //save to our firestore database
                                                collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(Objects.requireNonNull(task.getResult()).exists()){
                                                                    String name = task.getResult().getString("username");
//                                                        Log.d("username", "onComplete: " + name +"from create account");
                                                                    Journal_Api journal_api = Journal_Api.getInstance();
                                                                    journal_api.setUsername(name);
                                                                    journal_api.setUserId(currentUserId);
                                                                    Intent intent = new Intent(CreateaccountActivity.this,login_Activity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Storing", "onFailure: " + e.getMessage());
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("saving", "onFailure: " + e.getMessage());
                                                    }
                                                });
                                            }else {
                                                Toast.makeText(CreateaccountActivity.this, "This account is already registered.",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                                }}
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CreateaccountActivity.this, "This account is already registered",Toast.LENGTH_LONG).show();
                                                }
                                            });


                                }





            }


        @Override
        protected void onStart() {
            super.onStart();

            currentUser = firebaseAuth.getCurrentUser();
            firebaseAuth.addAuthStateListener(authStateListener);

        }
        }

