package com.nature.thenature;

import android.content.Intent;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout mTextInputLaoyoutEmail, mTextInputLaoyoutPasword;
    private ImageView mImageview, mCancelIv;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private TextView mTitle, mForgotpaswrd, mDontAccountsignup;
    private EditText mEditextEmail, meditTextPassword;
    private Button mSigninBtn, mGoogleBtn;
    private GoogleSignInClient mGoogleSignInClient;
    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initvariables();
        initGlide();


        //get firebase instance
        FirbaseAuthInstance();

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = mEditextEmail.getText().toString().trim();
                final String mPassword = meditTextPassword.getText().toString().trim();
                if (mEmail.isEmpty()) {

                    mEditextEmail.setError("Enter email address!");

                } else if (!isValidEmaiId(mEmail)) {
                    mEditextEmail.setError("Invalid email address!");

                } else if (mPassword.isEmpty()) {

                    mTextInputLaoyoutPasword.setError("Enter password!");
                } else {
                    mContentLoadingProgressBar.setVisibility(View.VISIBLE);
                    //authenticate user
                    mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mContentLoadingProgressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                            if (!task.isSuccessful()) {
                                // there was an error
                                if (mPassword.length() < 6) {
                                    mTextInputLaoyoutPasword.setError("Password too short, enter minimum 6 characters!");
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GooglesignIn();
            }
        });

        mDontAccountsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        mForgotpaswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));

            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();
        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, DashBoardActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {
            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                Log.e("error", "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        //getting the auth credential
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, " Sign In", Toast.LENGTH_SHORT).show();
                    // Sign in success
                    Log.e("TAG", "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();


                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("TAG", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, " failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void GooglesignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean isValidEmaiId(String mEmail) {
        return Patterns.EMAIL_ADDRESS.matcher(mEmail).matches();
    }

    private void FirbaseAuthInstance() {
        // //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
    }

    private void initGlide() {
        Glide.with(this)
                .load(R.drawable.nature_image)
                .placeholder(R.drawable.ic_launcher_web)
                .into(mImageview);
    }


    private void initvariables() {
        //set the custome title in toolbar
        mTitle = findViewById(R.id.txtv_center_title_toolbar);
        mTitle.setText(R.string.welcome_back);
        mTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        //end
        mTextInputLaoyoutEmail = findViewById(R.id.activity_login_Edittext_email_Textlayout);
        mTextInputLaoyoutPasword = findViewById(R.id.activity_login_Edittext_pasword_Textlayout);
        mEditextEmail = findViewById(R.id.activity_login_Edittext_email);
        meditTextPassword = findViewById(R.id.activity_login_Edittext_pasword);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mSigninBtn = findViewById(R.id.activity_SignIn_Btn);
        mGoogleBtn = findViewById(R.id.google_btn);
        mForgotpaswrd = findViewById(R.id.activity_txtv_forgotpaswrd);
        mDontAccountsignup = findViewById(R.id.activity_txtv_DonthaveaccountSignup);
        mImageview = findViewById(R.id.iv_nature);
        //set iconStart in toolbar
        mCancelIv = findViewById(R.id.iv_toolbar_start);
        mCancelIv.setImageResource(R.drawable.ic_cancel);
        mCancelIv.setVisibility(View.VISIBLE);
        mCancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });//end

    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}
