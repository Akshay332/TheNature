package com.nature.thenature;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ViewImageActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private ImageView mSelectedImg, mIconBack, mDownload;
    private FirebaseAuth mAuth;
    private String uid;
    private String image_key;
    private String imageUrl;
    private ToggleButton toggle_like_unlike;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private AdView mAdView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);


        initVariable();
        //Initialize adds
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5657577738944896/8543692821");
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Rewarded ads
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        mRewardedVideoAd.loadAd("ca-app-pub-5657577738944896/2897334655",new AdRequest.Builder().build());


        //Get Firebase with instance
        getFirebaseInstances();

        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
         uid = user.getUid();

        //backButton
        backIconaction();

        //getting intent data from adapter
        Intent intent = getIntent();
        image_key = intent.getStringExtra("image_key");
        imageUrl = intent.getStringExtra("imageUrl");

        //end


        //Gide to load an image
        Glide.with(ViewImageActivity.this)
                .load(imageUrl)
                .into(mSelectedImg);
        //end




        //Download Image
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();
                }
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);



            }
        });



    }
    // Function to check and request permission.
    private void checkPermission(String writeExternalStorage, int storagePermissionCode) {
        if (ContextCompat.checkSelfPermission(ViewImageActivity.this, writeExternalStorage)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(ViewImageActivity.this,
                    new String[] { writeExternalStorage },
                    storagePermissionCode);
        }
       else {
            DownloadFile(imageUrl);
        }
    }
    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ViewImageActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();

                DownloadFile(imageUrl);
            }
            else {
                Toast.makeText(ViewImageActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void getFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void DownloadFile(String imageUrl) {

        // Using timestamp to save image
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String str = timestamp.toString();
        String image_suffix = str.trim();
        String image_extn = ".jpg";
        String imagename = image_suffix + image_extn;

        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(imageUrl);
        Log.e("URI:", String.valueOf(uri));
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("The Nature", imagename);
        downloadManager.enqueue(request);

    }

    private void backIconaction() {

        mIconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ViewImageActivity.this, DashBoardActivity.class));
                finish();
            }
        });
    }

    private void initVariable() {
        mDownload = findViewById(R.id.iv_download);
        mSelectedImg = findViewById(R.id.selectedImage);
        mIconBack = findViewById(R.id.iv_toolbar_start);
        mIconBack.setImageResource(R.drawable.ic_left_arrow);
        mIconBack.setVisibility(View.VISIBLE);
        toggle_like_unlike = findViewById(R.id.toggle_like);

    }

    //ToggleButton for favourite select image
    public void onToggleButonClick(final View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {

            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }

            //to saving the users  data in firebase database
            final DatabaseReference like_ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(uid).child(image_key);
            like_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue()== null) {
                        Log.e("Message: ","Data not found");
                        Map newPost = new HashMap();
                        newPost.put("image_key", image_key);
                        newPost.put("cuid", uid);
                        like_ref.setValue(newPost);

                    } else {

                        Log.e("Message: ","Data  found");
                        Map RemovePost = new HashMap();
                        RemovePost.put("image_key",image_key);
                        RemovePost.put("cuid",uid);
                        like_ref.removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Toast.makeText(this, "Add to favourite", Toast.LENGTH_SHORT).show();

        } else {
                //delete user data from firebase
            final DatabaseReference delete_ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(uid).child(image_key);
            delete_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map rev_likes = new HashMap();
                    rev_likes.put("image_key",image_key);
                    rev_likes.put("cuid",uid);
                    delete_ref.removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Toast.makeText(this, "Removed from favourite", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //to check that data is empty or not
        final DatabaseReference like_ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(uid).child(image_key);
        like_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue()== null) {
                    toggle_like_unlike.setChecked(false);

                } else {
                    toggle_like_unlike.setChecked(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

}

