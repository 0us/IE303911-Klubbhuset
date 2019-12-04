package no.ntnu.klubbhuset.data.model;

import android.graphics.Bitmap;

import java.io.Serializable;

import lombok.Data;

@Data
public class Image implements Serializable {
    private String url;
    private transient Bitmap image;
}
