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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView mIconBack;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAthListener;
    private EditText mEdittextOldPasword, mEdittextNewPasword, mEditTextConfirmPasword;
    private TextInputLayout mTextInputLayoutOldpasword, mTextInputLayoutNewpasword, mTextInputLayoutConfirmpasword;
    private Button mChangepaswordBtn;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private AuthCredential mAuthCredential;
    private DatabaseReference current_user_db_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //initialize variables here
        initvaribales();
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
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        mChangepaswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String OldPassword = mEdittextOldPasword.getText().toString().trim();
                final String newPasword = mEdittextNewPasword.getText().toString().trim();
                final String confirmNewPasword = mEditTextConfirmPasword.getText().toString().trim();
                if (OldPassword.isEmpty()) {
                    mTextInputLayoutOldpasword.setError("Enter old password!");
                } else if (newPasword.isEmpty()) {
                    mTextInputLayoutNewpasword.setError("Enter new password!");
                } else if (newPasword.length() < 6 && OldPassword.length() < 6) {
                    mTextInputLayoutNewpasword.setError("Password too short, enter minimum 6 characters!");
                } else if (confirmNewPasword.isEmpty()) {
                    mTextInputLayoutConfirmpasword.setError("Enter confirm password!");
                } else if (!confirmNewPasword.equals(newPasword)) {
                    mTextInputLayoutConfirmpasword.setError("Password do not match!");
                } else {
                    mContentLoadingProgressBar.setVisibility(View.VISIBLE);

                    mAuthCredential = EmailAuthProvider.getCredential(user.getEmail(), OldPassword);
                    user.reauthenticate(mAuthCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPasword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            //to saving the users    data in firebase database

                                            current_user_db_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

                                            String newPassword = mEdittextNewPasword.getText().toString();
                                            Map newPost = new HashMap();
                                            newPost.put("Password",newPassword);
                                            current_user_db_reference.updateChildren(newPost);

                                            mContentLoadingProgressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            mContentLoadingProgressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }

        });

        //back button icon
        mIconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePasswordActivity.this, ProfileAcitivity.class));
                finish();
            }
        });
    }

    private void getFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initvaribales() {
        mChangepaswordBtn = findViewById(R.id.btn_change_pasword);
        mTextInputLayoutOldpasword = findViewById(R.id.activity_change_paswrd_old_TextInpulaout);
        mEdittextNewPasword = findViewById(R.id.activity_change_paswrd_New_EditText);
        mEditTextConfirmPasword = findViewById(R.id.activity_change_paswrd_ComfirmNew_EditText);
        mEdittextOldPasword = findViewById(R.id.activity_change_paswrd_old_EditText);
        mTextInputLayoutNewpasword = findViewById(R.id.activity_change_paswrd_New_TextInpulaout);
        mTextInputLayoutConfirmpasword = findViewById(R.id.activity_change_paswrd_ConfirmNew_TextInpulaout);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mIconBack = findViewById(R.id.iv_toolbar_start);
        mIconBack.setImageResource(R.drawable.ic_left_arrow);
        mIconBack.setVisibility(View.VISIBLE);
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
        if (mAthListener != null) {
            mAuth.removeAuthStateListener(mAthListener);
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
