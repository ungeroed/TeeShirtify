package ungeroed.com.teeshirtify;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by Morten on 29/08/2017.
 */

public class Shirt implements Serializable {
    int id;
    int price;
    String picture;
    String colour;
    String size;
    String name;
    int quantity;
    //public Bitmap image;

    public void print(){
        Log.e("here",picture);
    }
}
