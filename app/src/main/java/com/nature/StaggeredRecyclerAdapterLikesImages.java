package com.nature.thenature.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nature.thenature.Model.Likes;
import com.nature.thenature.R;
import com.nature.thenature.ViewImageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StaggeredRecyclerAdapterLikesImages extends FirebaseRecyclerAdapter<Likes, StaggeredRecyclerAdapterLikesImages.ImageViewHolder> {

    Context mContext;

    public StaggeredRecyclerAdapterLikesImages(@NonNull FirebaseRecyclerOptions<Likes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ImageViewHolder holder, int position, @NonNull Likes model) {

        final String img_key = model.getImage_key();

        Log.e("GET IMAGE KEY:", img_key);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("wallpapers").child(img_key);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final String img_url = dataSnapshot.child("imageUrl").getValue().toString();
                Log.e("GET IMAGE URL:", img_url);

                holder.getImage(img_url);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ViewImageActivity.class);
                        intent.putExtra("imageUrl", img_url);
                        intent.putExtra("image_key",img_key);
                        mContext.startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallpaper, parent, false);

        mContext = parent.getContext();

        return new ImageViewHolder(view);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView mImage;

        public ImageViewHolder(@NonNull View itemView) {

            super(itemView);
            mImage = itemView.findViewById(R.id.iv_wallpaper);
        }

        public void getImage(String image_url) {

            Glide.with(mContext)
                    .load(image_url)
                    .placeholder(R.color.colorPinkLight)
                    .into(mImage);
        }
    }
}
