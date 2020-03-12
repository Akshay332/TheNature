package com.nature.thenature;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nature.thenature.Adapter.StaggeredRecylerAdapter;
import com.nature.thenature.Model.wallpapers;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DashBoardActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNavigationView;
    private FirebaseAuth mAuth;
    private TextView mTitle;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseAuth.AuthStateListener mAthListener;
    private RecyclerView staggeredRv;
    private StaggeredRecylerAdapter adapter;
    private StaggeredGridLayoutManager manager;
    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        //init recylerview and set layout manager
        staggeredRv = findViewById(R.id.recyclerView);
        manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        //end

        //set Query when data is changed it will notify
        FirebaseRecyclerOptions<wallpapers> options = new FirebaseRecyclerOptions.Builder<wallpapers>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("wallpapers"), wallpapers.class)
                .build();//end


        //set adapter in recyle view
        adapter = new StaggeredRecylerAdapter(options);
        staggeredRv.setAdapter(adapter);
        //end

        //Initialize adds
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //initialize the variable here
        InitVariables();
        //get Title of dashboard
        setToolbarTitle();

        //initailize and assignc variable of bottom navigation
        initvariablesBottomnavigation();

        // //Get Firebase with instance
        getFirebaseInstances();
        mAthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null){
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(DashBoardActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        //on click listenre on floating action button
        floatingactionbutonClicklisner();
    }


    private void floatingactionbutonClicklisner() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this,SearchViewActivity.class));
                finish();
            }
        });
    }

    private void setToolbarTitle() {
        mTitle.setText(R.string.explore);
        mTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        mTitle.setTextSize(18);
    }

    private void InitVariables() {
        mTitle = findViewById(R.id.txtv_title_toolbar);
        mFloatingActionButton = findViewById(R.id.floatingactionbutonSerach);
    }

    private void getFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initvariablesBottomnavigation() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        // set home selected
        mBottomNavigationView.setSelectedItemId(R.id.dashboard);
        //disable shift mode

        //perform itemselectedlistener
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(DashBoardActivity.this, ProfileAcitivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.about:
                        startActivity(new Intent(DashBoardActivity.this, AboutAcivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        mAuth.addAuthStateListener(mAthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(mAthListener!=null){
            mAuth.removeAuthStateListener(mAthListener);
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
