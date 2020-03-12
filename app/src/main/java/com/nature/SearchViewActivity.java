package com.nature.thenature;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nature.thenature.Adapter.StaggeredRecylerAdapter;
import com.nature.thenature.Model.wallpapers;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SearchViewActivity extends AppCompatActivity {

    private ImageView mIconBack,mIvSearchBtn;
    private RecyclerView rvStaggered;
    private StaggeredGridLayoutManager manager;
    private EditText mEditTextSearchField;
    private DatabaseReference mWallpaperDatabaseRef;
    private StaggeredRecylerAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);
        mWallpaperDatabaseRef = FirebaseDatabase.getInstance().getReference().child("wallpapers");
        //init recylerview and set layout manager
        rvStaggered = findViewById(R.id.recyclerView);
        manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rvStaggered.setLayoutManager(manager);
        //end


        //initialize variables here
        initVariable();

        //backButton
        backIconaction();

        //Search button
        mIvSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mEditTextSearchField.getText().toString();
                Log.e("search Text",searchText);
                if (searchText.isEmpty()){
                    mEditTextSearchField.setError("Enter wallpaper's category!");
                }else {
                    firebaseSearchWallpaper(searchText);
                }

            }
        });

    }

    private void firebaseSearchWallpaper(String searchText) {

//        Query query  = mWallpaperDatabaseRef.startAt(searchText).endAt(searchText+"\ufBff");
//        Log.e("Query", String.valueOf(query));
        //set Query when data is changed it will notify
        FirebaseRecyclerOptions<wallpapers> options = new FirebaseRecyclerOptions.Builder<wallpapers>()
                .setQuery(mWallpaperDatabaseRef.orderByChild("SearchWallpaper").startAt(searchText).endAt(searchText+"\ufBff"), wallpapers.class)
                .build();//end


        //set adapter in recycle view
        adapter = new StaggeredRecylerAdapter(options);
        rvStaggered.setAdapter(adapter);
        //end
        adapter.startListening();
    }




    private void backIconaction() {
        mIconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SearchViewActivity.this, DashBoardActivity.class));
                finish();
            }
        });
    }

    private void initVariable() {
        mIvSearchBtn = findViewById(R.id.iv_search);
        mEditTextSearchField = findViewById(R.id.edit_text_Search);
        mIconBack = findViewById(R.id.iv_toolbar_start);
        mIconBack.setImageResource(R.drawable.ic_left_arrow);
        mIconBack.setVisibility(View.VISIBLE);


    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }
}
