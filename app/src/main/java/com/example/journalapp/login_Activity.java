package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.journalapp.util.Journal_Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class login_Activity extends AppCompatActivity {
    private Button login_button,createaccount_button,forgot_button;
    private AutoCompleteTextView enter_email;
    private EditText  enter_password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        forgot_button = findViewById(R.id.forgot_button);
        login_button = findViewById(R.id.login_button);
        createaccount_button = findViewById(R.id.create_account);
        enter_email = findViewById(R.id.enter_email);
        enter_password = findViewById(R.id.enter_password);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar =  findViewById(R.id.progress_bar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        createaccount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login_Activity.this,CreateaccountActivity.class));
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = enter_email.getText().toString();
                String password = enter_password.getText().toString();
                signinaccountwithemailandpassword(email,password);
            }
        });
        forgot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText reset_password = new EditText(view.getContext());
                final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(view.getContext());
                alert_dialog.setTitle("Reset password");
                alert_dialog.setMessage("Enter your email.");
                alert_dialog.setView(reset_password);
                alert_dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = reset_password.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login_Activity.this, "Reset password link has been sent to your email", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login_Activity.this,"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                alert_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert_dialog.show();
            }
        });
    }

    private void signinaccountwithemailandpassword(String email, String password) {
        if(!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser currentuser = firebaseAuth.getCurrentUser();
                        assert currentuser != null;
                        String currentuserid = currentuser.getUid();
                        if (currentuser.isEmailVerified()) {
                            collectionReference.whereEqualTo("userid", currentuserid)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException e) {
                                            assert queryDocumentSnapshots != null;
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {
//                                            Log.d("login", "onEvent: " + snapshots.getString("username") + " from login");
                                                    Journal_Api journal_api = Journal_Api.getInstance();
                                                    journal_api.setUserId(snapshots.getString("userid"));
                                                    journal_api.setUsername(snapshots.getString("username"));
                                                    startActivity(new Intent(login_Activity.this, PostJournal.class));
                                                }
                                            }
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(login_Activity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(login_Activity.this, "Please create account first . ", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(login_Activity.this,"Please create account first . " , Toast.LENGTH_SHORT).show();
                }
            });;
        }else{
            Toast.makeText(login_Activity.this,"please fill all the fields",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
        }

}
