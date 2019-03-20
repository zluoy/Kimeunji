package com.eunji.mobile_st_unitas;


public class ImageItem {
    private String img;
    private int height;
    private int width;

    public int getHeight() { return height; }
    public int getWidth() { return width; }

    public ImageItem(String img, int height, int width){
        this.img = img;
        this.height = height;
        this.width = width;
    }

    public String getImage(){
        return img;
    }
}
