package com.nature.thenature.Model;

public class wallpapers {

    private String imageUrl,image_key;

    public wallpapers() {
    }

    public wallpapers(String imageUrl, String image_key) {
        this.imageUrl = imageUrl;
        this.image_key = image_key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImage_key() {
        return image_key;
    }

    public void setImage_key(String image_key) {
        this.image_key = image_key;
    }
}
