package com.nature.thenature.Model;

public class Likes {
    public String cuid,image_key;

    public Likes() {

    }

    public Likes(String cuid, String image_key) {
        this.cuid = cuid;
        this.image_key = image_key;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getImage_key() {
        return image_key;
    }

    public void setImage_key(String image_key) {
        this.image_key = image_key;
    }
}
