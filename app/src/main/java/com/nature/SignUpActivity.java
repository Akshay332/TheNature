package com.nature.thenature;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView mTextAlreadyaccount, mTextForgotpaswrd;
    private TextInputLayout mTextInputLayoutEmail, mTextInputLayoutPasword, mTextInputLayoutConfirmPasword;
    private EditText mEditTextEmail, mEdiTextPaswrd, mEdittextConfirmpaswrd, mEdittextName;
    private ImageView mImageview, mCancelIv, mUpoadIv;
    private Button mSignupBtn;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private CircleImageView mIvprofie;
    private DatabaseReference current_user_db_reference;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private Uri filepathImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initvariables();
        initGlide();
        // Get Firebase Auth instance
        FirbaseAuthInstance();

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        //camera icon
        mUpoadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //if already have account go to signin activity
        mTextAlreadyaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });


        //if you have forgot your password go to reset password with email
        mTextForgotpaswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
                finish();
            }
        });
        //signUp conditions
        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mEdittextName.getText().toString().trim();
                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEdiTextPaswrd.getText().toString().trim();
                String confirmPasword = mEdittextConfirmpaswrd.getText().toString().trim();


                if (filepathImg == null) {
                    Log.e("URIfilePath", String.valueOf(filepathImg));

                    Toast.makeText(SignUpActivity.this, "Please upload profile!", Toast.LENGTH_SHORT).show();

                } else if (name.isEmpty()) {
                    mEdittextName.setError("Enter user name!");
                } else if (email.isEmpty()) {
                    mEditTextEmail.setError("Enter email address!");
                } else if (!isValidEmaiId(email)) {
                    mEditTextEmail.setError("Invalid email address!");
                } else if (password.isEmpty()) {
                    mTextInputLayoutPasword.setError("Enter password!");
                } else if (password.length() < 6) {
                    mTextInputLayoutPasword.setError("Password too short, enter minimum 6 characters!");
                } else if (confirmPasword.isEmpty()) {
                    mTextInputLayoutConfirmPasword.setError("Enter Confirm password!");
                } else if (!confirmPasword.equals(password)) {
                    mTextInputLayoutConfirmPasword.setError("Password do not match!");
                } else {
                    Query UsernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name").equalTo(name);
                    UsernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                mEdittextName.setError("Please enter different user name!");
                            } else {
                                CreateUserAccount(email, password);
                                mContentLoadingProgressBar.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }
        });

    }

    //choose image from Intent
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepathImg = data.getData();

            Glide.with(this)
                    .load(filepathImg)
                    .placeholder(R.drawable.ic_profile_black_24dp)
                    .into(mIvprofie);

        }
    }


    //create user account with firebase
    private void CreateUserAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mContentLoadingProgressBar.setVisibility(View.GONE);

                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(SignUpActivity.this, DashBoardActivity.class));
                    finish();
                    Toast.makeText(SignUpActivity.this, "Account Created Successfully" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    //to saving the users  data in firebase database
                    String user_id = mAuth.getCurrentUser().getUid();
                    current_user_db_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                    String name = mEdittextName.getText().toString();
                    String Email = mEditTextEmail.getText().toString();
                    String Password = mEdiTextPaswrd.getText().toString();

                    Map newPost = new HashMap();
                    newPost.put("name", name);
                    newPost.put("Email", Email);
                    newPost.put("Password", Password);

                    current_user_db_reference.setValue(newPost);

                    uploadProfile();

                }
            }
        });


    }

    private void uploadProfile() {
        if (filepathImg != null) {

            String user_id = mAuth.getCurrentUser().getUid();
            StorageReference reference = mStorageRef.child("images/").child(user_id);
            reference.putFile(filepathImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SignUpActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void FirbaseAuthInstance() {
        mAuth = FirebaseAuth.getInstance();
    }


    private boolean isValidEmaiId(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void initGlide() {
        Glide.with(this)
                .load(R.drawable.nature_image)
                .placeholder(R.drawable.ic_launcher_web)
                .into(mImageview);
    }

    private void initvariables() {
        mImageview = findViewById(R.id.iv_nature);
        mUpoadIv = findViewById(R.id.iv_camera);
        mIvprofie = findViewById(R.id.iv_circle_profile);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mTextAlreadyaccount = findViewById(R.id.activity_Signup_txtv_alreadyhaveaccount);
        mTextForgotpaswrd = findViewById(R.id.activity_Signup_textv_ForgotPasword);
        mTextInputLayoutEmail = findViewById(R.id.activity_signup_Textinputlyout_Email);
        mTextInputLayoutPasword = findViewById(R.id.activity_signup_Textinputlyout_password);
        mTextInputLayoutConfirmPasword = findViewById(R.id.activity_signup_Textinputlyout_Confirmpassword);
        mEdittextName = findViewById(R.id.activity_Signup_Edittext_Username);
        mEditTextEmail = findViewById(R.id.activity_Signup_Edittext_email);
        mEdiTextPaswrd = findViewById(R.id.activity_Signup_Edittext_password);
        mSignupBtn = findViewById(R.id.activity_Signup_BtnSignup);
        mEdittextConfirmpaswrd = findViewById(R.id.activity_Signup_Edittext_Confirmpassword);
        //set iconStar in toolbar
        mCancelIv = findViewById(R.id.iv_toolbar_start);
        mCancelIv.setImageResource(R.drawable.ic_left_arrow);
        mCancelIv.setVisibility(View.VISIBLE);
        mCancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mContentLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
