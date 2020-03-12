package com.nature.thenature;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nature.thenature.Adapter.StaggeredRecyclerAdapterLikesImages;
import com.nature.thenature.Model.Likes;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNavigationView;
    private FirebaseAuth mAuth;
    private FloatingActionButton mFloatingActionButtonUploaad, mFloatingActionButtonPicImage;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private Uri filepathImg;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private RecyclerView staggeredRv;
    private StaggeredRecyclerAdapterLikesImages adapter;
    private StaggeredGridLayoutManager manager;
    private TextView mfavTag, mTitle;
    private String uid;
    private AdView mAdView;
    private Spinner mspinner;
    private List<String> list;
    private ArrayAdapter<String> dataAdapter;
    private String spinner_items;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mspinner = findViewById(R.id.spinner);
        list = new ArrayList<>();
        list.add(0,"Choose Category");
        list.add("Animals");
        list.add("Food");
        list.add("Movies");
        list.add("Nature");
        list.add("Celebrities");
        list.add("Others");

        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspinner.setAdapter(dataAdapter);
        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("Choose Category")){
                    //do nothing
                }else {
                    spinner_items = parent.getItemAtPosition(position).toString();
                    Log.e("Spinner_Items",spinner_items);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //init recyclerview and set layout manager
        staggeredRv = findViewById(R.id.recyclerView);
        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);
        //end



        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        //initailize and assignc variable
        initvariablesofBottomNavigation();

        //initailize variables here
        initVariables();

        //get Title of dashboard
        setToolbarTitle();


        //Get Firebase with instance
        getFirebaseInstances();

        //get current user
        final FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();

        mStorage = FirebaseStorage.getInstance();

        mStorageRef = mStorage.getReference();
        mFloatingActionButtonUploaad.setVisibility(View.GONE);
        mspinner.setVisibility(View.GONE);

        //choose image
        mFloatingActionButtonPicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();



            }
        });

        //upload Image
        mFloatingActionButtonUploaad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (mspinner.getSelectedItem().equals("Choose Category")){
                  Snackbar.make(getWindow().getDecorView().getRootView(), "Please select one category from the list.", Snackbar.LENGTH_LONG).show();
              }else {
                  UploadImages();
              }



            }
        });


        //set Query when data is changed it will notify
        FirebaseRecyclerOptions<Likes> option = new FirebaseRecyclerOptions.Builder<Likes>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Likes").child(uid), Likes.class)
                .build();//end


        //set adapter in recyle view
        adapter = new StaggeredRecyclerAdapterLikesImages(option);
        staggeredRv.setAdapter(adapter);
        //end


    }

    private void setToolbarTitle() {
        mTitle.setText(getResources().getText(R.string.favourites));
        mTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        mTitle.setTextSize(18);
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    private void UploadImages() {
        // Using timestamp to save image
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String str = timestamp.toString();
        String image_suffix = str.trim();
        String image_extn = ".jpg";

        String imagename = image_suffix + image_extn;

      if (filepathImg != null) {
            mContentLoadingProgressBar.setVisibility(View.VISIBLE);


            final StorageReference reference = mStorageRef.child("Wallpaper").child(imagename);
            reference.putFile(filepathImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DatabaseReference imageDatabase = FirebaseDatabase.getInstance().getReference().child("wallpapers");
                            DatabaseReference new_ref = imageDatabase.push();
                            String key = new_ref.getKey();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("imageUrl", String.valueOf(uri));
                            hashMap.put("image_key", key);
                            hashMap.put("SearchWallpaper",spinner_items);
                            new_ref.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                    mContentLoadingProgressBar.setVisibility(View.GONE);
                                    mFloatingActionButtonUploaad.setVisibility(View.GONE);
                                }
                            });
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            Toast.makeText(this, "Please add image before uploading!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepathImg = data.getData();
            mFloatingActionButtonUploaad.setVisibility(View.VISIBLE);
            mspinner.setVisibility(View.VISIBLE);
            mspinner.performClick();


        }
    }


    private void initVariables() {

        mFloatingActionButtonUploaad = findViewById(R.id.main_activity_floatingbtn);
        mFloatingActionButtonPicImage = findViewById(R.id.main_activity_floatingbtnAdd);
        mContentLoadingProgressBar = findViewById(R.id.loading);
        mfavTag = findViewById(R.id.txtv_Nofav);
        mTitle = findViewById(R.id.txtv_title_toolbar);
    }

    private void getFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initvariablesofBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        // set home selected
        mBottomNavigationView.setSelectedItemId(R.id.home);
        //perform itemselectedlistener
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this, ProfileAcitivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.about:
                        startActivity(new Intent(MainActivity.this, AboutAcivity.class));
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
        final DatabaseReference like_ref = FirebaseDatabase.getInstance().getReference().child("Likes").child(uid);
        like_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {

                    mfavTag.setVisibility(View.VISIBLE);

                } else {

                    mfavTag.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();


    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
