package com.nature.thenature;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutAcivity extends AppCompatActivity {
    private BottomNavigationView mBottomNavigationView;
    private TextView mDeveloper,mTitle,mPrivacyPolicy;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_acivity);
        initVaribales();
        //Initialize adds
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //initailize and assignc variable
        initvariablesOfbottomnavigation();
        //init title
        getToolbarTitle();
        //Developer link
        mDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.instagram.com/___nature__lover/");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                intent.setPackage("com.instagram.android");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/___nature__lover/")));
                }
            }
        });

        mPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutAcivity.this,PrivacyPolicyActivity.class));
                finish();
            }
        });
    }

    private void initVaribales() {
        mDeveloper = findViewById(R.id.txtv_developer);
        mPrivacyPolicy = findViewById(R.id.txtv_Privacy_policy);
    }

    private void getToolbarTitle() {
        mTitle = findViewById(R.id.txtv_title_toolbar);
        mTitle.setText(R.string.info);
        mTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        mTitle.setTextSize(18);
    }

    private void initvariablesOfbottomnavigation() {

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        // set home selected
        mBottomNavigationView.setSelectedItemId(R.id.about);
        //perform itemselectedlistener
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(AboutAcivity.this, DashBoardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.home:
                        startActivity(new Intent(AboutAcivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;



                    case R.id.profile:
                        startActivity(new Intent(AboutAcivity.this, ProfileAcitivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.about:

                        return true;
                }
                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
