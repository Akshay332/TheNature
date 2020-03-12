package com.nature.thenature.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nature.thenature.Model.wallpapers;
import com.nature.thenature.R;
import com.nature.thenature.ViewImageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class StaggeredRecylerAdapter extends FirebaseRecyclerAdapter<wallpapers, StaggeredRecylerAdapter.ImageViewHolder> {

    Context mContext;


    public StaggeredRecylerAdapter(@NonNull FirebaseRecyclerOptions<wallpapers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull wallpapers model) {

        final String image_url = model.getImageUrl();
        final String image_key = model.getImage_key();



        holder.getImage(image_url);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(mContext, ViewImageActivity.class);
                intent.putExtra("image_key",image_key);
                intent.putExtra("imageUrl",image_url);
                mContext.startActivity(intent);
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
