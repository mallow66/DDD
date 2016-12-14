package brahim.mallow.com.ddd.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by brahim on 05/12/16.
 */

public class ImageModel  {


    private String caption;
    private Bitmap image;

    public ImageModel(String caption, Bitmap image){
        this.caption = caption;
        this.image = image;
    }

    protected ImageModel(Parcel in) {
        caption = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }



    public String getCaption() {
        return caption;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }


}