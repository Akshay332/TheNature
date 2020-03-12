package com.nature.thenature;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAcitivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAthListener;
    private CircleImageView mcircleImageView;
    private TextView mUsername, mChangePassword, mChangeEmailId ,mDeleteAccount,mTitle;
    private Button mSignoutBtn;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference UserReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private static final String USERS = "Users";
    private DatabaseReference current_user_db_reference;
    private GoogleSignInClient mGoogleSignInClient;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acitivity);
        //initailize and assigne variables
        initvariables();
        // //Get Firebase with instance
        getFirebaseInstances();
        //init title
        getToolbarTitle();
        //Initialize adds

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        Log.e("uid",uid);
        //get user name from database
        mDatabase = FirebaseDatabase.getInstance();
        UserReference = mDatabase.getReference(USERS).child(uid);
        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsername.setText(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });//end


        //get profile image from firebase storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference("images/").child(uid);
        mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
               Glide.with(ProfileAcitivity.this)
                       .load(uri)
                       .placeholder(R.drawable.ic_profile_black_24dp)
                       .into(mcircleImageView);

            }
        });



        // this listener will be called when there is change in firebase user session
        mAthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileAcitivity.this, LoginActivity.class));
                    finish();
                }
            }
        };



        //perform itemselectedlistener
        getBottomNavigationItemslelectListener();



        //signOut
        mSignoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentLoadingProgressBar.setVisibility(View.VISIBLE);
                mAuth.signOut();




            }

        });

        //Change password
        mChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileAcitivity.this,ChangePasswordActivity.class));
                finish();
            }
        });
        //change Email id
        mChangeEmailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileAcitivity.this,ChangeEmailAddressActivity.class));
                finish();
            }
        });


        //Delete account
        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentLoadingProgressBar.setVisibility(View.VISIBLE);

                    user.delete()
                            .addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ProfileAcitivity.this,"Your profile is deleted:( Create a account now!",Toast.LENGTH_SHORT).show();

                                        mAuth.signOut();
                                        //to delete the database current user
                                        current_user_db_reference= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                                        current_user_db_reference.removeValue();

                                        mContentLoadingProgressBar.setVisibility(View.GONE);
                                    }else {
                                        Toast.makeText(ProfileAcitivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        mContentLoadingProgressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

            }
        });
    }





    private void getToolbarTitle() {
        mTitle = findViewById(R.id.txtv_title_toolbar);
        mTitle.setText(R.string.profile);
        mTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        mTitle.setTextSize(18);
    }


    private void getFirebaseInstances() {

        mAuth = FirebaseAuth.getInstance();
    }



    private void getBottomNavigationItemslelectListener() {
        // set home selected
        mBottomNavigationView.setSelectedItemId(R.id.profile);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(ProfileAcitivity.this, DashBoardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.home:
                        startActivity(new Intent(ProfileAcitivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.profile:

                        return true;

                    case R.id.about:
                        startActivity(new Intent(ProfileAcitivity.this, AboutAcivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void initvariables() {
        mUsername = findViewById(R.id.txtv_username);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mSignoutBtn = findViewById(R.id.singn_out_btn);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mChangeEmailId = findViewById(R.id.txtv_change_Email);
        mcircleImageView = findViewById(R.id.iv_circle_profile);
        mChangePassword = findViewById(R.id.txtv_change_password);
        mDeleteAccount = findViewById(R.id.activity_profile_Texttv_deleteAccount);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mContentLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account!=null){
            String personName = account.getDisplayName().toString();
            String PersonImage = account.getPhotoUrl().toString();
            Log.e("personName",personName);
            mUsername.setText(personName);
            Glide.with(this)
                    .load(PersonImage)
                    .into(mcircleImageView);
        }

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
        moveTaskToBack(true);
    }
}
