package com.nature.thenature;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChangeEmailAddressActivity extends AppCompatActivity {
    private ImageView mIconback, mIvnature;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAthListener;
    private TextInputLayout mTextInputLayout;
    private EditText mEdittextEmail;
    private Button mChangeEmailBtn;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private DatabaseReference current_user_db_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email_address);
        //initialize variables here

        initvaribales();
        //getimage from glide
        getGlideImage();
        //Get Firebase with instance
        getFirebaseInstances();
        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        // this listener will be called when there is change in firebase user session
        mAthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ChangeEmailAddressActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        mChangeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = mEdittextEmail.getText().toString().trim();
                if (newEmail.isEmpty()) {
                    mEdittextEmail.setError("Enter email address!");
                } else if (!isValidEmailAddress(newEmail)) {
                    mEdittextEmail.setError("Invalid email address!");
                } else {
                    mContentLoadingProgressBar.setVisibility(View.VISIBLE);
                        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangeEmailAddressActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    //to saving the users    data in firebase database
                                    current_user_db_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

                                    String newEmail = mEdittextEmail.getText().toString();
                                    Map newPostEmail  = new HashMap();
                                    newPostEmail.put("Email",newEmail);
                                    current_user_db_reference.updateChildren(newPostEmail);
                                    mContentLoadingProgressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(ChangeEmailAddressActivity.this, "Failed to update email!", Toast.LENGTH_SHORT).show();
                                    mContentLoadingProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });

                }
            }
        });

        //back button icon
        mIconback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangeEmailAddressActivity.this, ProfileAcitivity.class));
                finish();
            }
        });

    }

    private boolean isValidEmailAddress(String newEmail) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(newEmail).matches();
    }

    private void getGlideImage() {
        Glide.with(this)
                .load(R.drawable.nature_image)
                .placeholder(R.drawable.ic_launcher_background)
                .into(mIvnature);
    }

    private void initvaribales() {
        mTextInputLayout = findViewById(R.id.activity_change_email_textInputLayout);
        mEdittextEmail = findViewById(R.id.activity_change_Email_Edittext);
        mChangeEmailBtn = findViewById(R.id.btn_change_email);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mIvnature = findViewById(R.id.iv_nature);
        mIconback = findViewById(R.id.iv_toolbar_start);
        mIconback.setImageResource(R.drawable.ic_left_arrow);
        mIconback.setVisibility(View.VISIBLE);


    }

    private void getFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mContentLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAthListener!=null){
            mAuth.removeAuthStateListener(mAthListener);
        }

    }
}
