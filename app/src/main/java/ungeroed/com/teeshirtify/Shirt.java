package ungeroed.com.teeshirtify;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;

/**
 * Simple class that holds a single t-shirt data
 * the properties matches the webservice data
 */

public class Shirt implements Serializable {
    int id;
    int price;
    String picture;
    String colour;
    String size;
    String name;
    int quantity;
}
