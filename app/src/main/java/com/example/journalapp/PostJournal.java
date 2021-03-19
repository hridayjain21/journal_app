package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journalapp.model.Journal;
import com.example.journalapp.util.Journal_Api;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

public class PostJournal extends AppCompatActivity implements View.OnClickListener{
    private static final int GALLERY_CODE = 1;
    private Uri imageuri;
    private Button save_button;
    private TextView username,date;
    private EditText title,Thought;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("journal");
    private ImageView upload_pic,pic;
    private String currrentuserid,currentusername;
    private ImageButton goto_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        save_button = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progressBar_postjournal);
        goto_list = findViewById(R.id.goto_list);
        username = findViewById(R.id.username);
//        date = findViewById(R.id.date);
        title = findViewById(R.id.Title);
        Thought = findViewById(R.id.Your_thoughts);
        firebaseAuth = FirebaseAuth.getInstance();
        upload_pic = findViewById(R.id.load_image);
        pic = findViewById(R.id.image_platform);
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar.setVisibility(View.INVISIBLE);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        goto_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostJournal.this,Journal_list.class));
            }
        });


        upload_pic.setOnClickListener(this);
        save_button.setOnClickListener(this);

        if(Journal_Api.getInstance() != null){
            currentusername = Journal_Api.getInstance().getUsername();
            currrentuserid = Journal_Api.getInstance().getUserId();
            username.setText(currentusername);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentuser = firebaseAuth.getCurrentUser();
                if(currentuser != null){

                }else {

                }
            }
        };

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.load_image:
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_CODE);
                break;
            case R.id.save_button:
                save_journal();
                break;
        }
    }

    private void save_journal() {
        final String Title = title.getText().toString().trim();
        final String  thought = Thought.getText().toString().trim();

        if(!TextUtils.isEmpty(Title)
                && !TextUtils.isEmpty(thought)
                && imageuri != null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference filepath = storageReference
                    .child("journal_image")
                    .child("my_Image "+ Timestamp.now().getSeconds());

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageurl = uri.toString();
//                            Log.d("image", "onSuccess: "+imageurl);
                            Journal journal = new Journal();
                            journal.setTitle(Title);
                            journal.setThought(thought);
                            journal.setImageurl(imageurl);
                            journal.setUserid(currrentuserid);
                            journal.setUsername(currentusername);
                            journal.setTimeAdded(new Timestamp(new Date()));

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostJournal.this,Journal_list.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("failure", "onFailure: "+e.getMessage());
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            Toast.makeText(PostJournal.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageuri = data.getData();
                pic.setImageURI(imageuri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentuser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
