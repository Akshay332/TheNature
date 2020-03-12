package com.nature.thenature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {
    private Button mResetPaswordBtn;
    private EditText mEditTextEmail;
    private Toolbar toolbar;
    private ImageView mIconback ,mIvnature;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAthListener;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
      //initialize variables
        initvariables();
        //getimage from glide
            getGlideImage();
        // //Get Firebase with instance
        getFirebaseInstances();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // this listener will be called when there is change in firebase user session
        mAthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        mResetPaswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextEmail.getText().toString().trim();
                if(email.isEmpty()){
                    mEditTextEmail.setError("Enter your registered email id");
                }else if(!isValidEmailId(email)){
                    mEditTextEmail.setError("Invalid email address!");
                }else {
                    mContentLoadingProgressBar.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mAuth.signOut();
                                Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                            }
                            mContentLoadingProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        mIconback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void getGlideImage() {
        Glide.with(this)
                .load(R.drawable.nature_image)
                .placeholder(R.drawable.ic_launcher_web)
                .into(mIvnature);
    }

    private boolean isValidEmailId(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void getFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initvariables() {
        mIvnature = findViewById(R.id.iv_nature);
        mResetPaswordBtn = findViewById(R.id.Btn_resetpasword);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mEditTextEmail = findViewById(R.id.activity_resetpasword_EdittextEmail);
        toolbar = findViewById(R.id.toolbar);
        mIconback = findViewById(R.id.iv_toolbar_start);
        mIconback.setImageResource(R.drawable.ic_left_arrow);
        mIconback.setVisibility(View.VISIBLE);



    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
