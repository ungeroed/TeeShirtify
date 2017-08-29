package ungeroed.com.teeshirtify;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiHandler {


    private static ApiHandler instance;

    ArrayList<Shirt> products;

    private ApiHandler(){
        products = new ArrayList<Shirt>();
    }

    public static ApiHandler getInstance(){
        if(instance == null){
            instance = new ApiHandler();
        }
        return instance;
    }

    public void fetchInitial(Context context){
        new shirt_fetcher().execute(context);
    }

    public void fetchImageFromUrl(ImageView view, Shirt shirt){
        new DownloadImageTask(view).execute(shirt);
    }

    public void placeOrder(final Context con, final HashMap<Integer,Integer> basket){
            if(basket.isEmpty()){return;}
            final AtomicInteger total = new AtomicInteger(0);
            for (Integer key : basket.keySet()){
                total.addAndGet(getSingleShirtWithID(key).price);
            }

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://mock-shirt-backend.getsandbox.com/order");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept","application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        JSONObject order = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        order.put("total", total.intValue());
                        for(Integer key : basket.keySet()){
                            JSONObject shirt = new JSONObject();
                            shirt.put("id", key);
                            shirt.put("quantity", basket.get(key));
                            jsonArray.put(shirt);
                        }
                        JSONObject basket = new JSONObject();
                        basket.put("shirts", jsonArray);
                        order.put("basket", basket);
                        Log.i("JSON", order.toString());
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                        //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                        os.writeBytes(order.toString());

                        os.flush();
                        os.close();

                        //broadcast result to listening receivers
                        Intent intent = new Intent(CheckoutFragment.ORDER_EVENTS);
                        intent.putExtra("response", conn.getResponseCode());
                        LocalBroadcastManager.getInstance(con).sendBroadcast(intent);
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(CheckoutFragment.ORDER_EVENTS);
                        intent.putExtra("response", 404);
                        LocalBroadcastManager.getInstance(con).sendBroadcast(intent);
                    }
                }
            });

            thread.start();

    }


    private class shirt_fetcher extends AsyncTask<Context, Context, Context> {

        @Override
        protected Context doInBackground(Context[] params) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://mock-shirt-backend.getsandbox.com/shirts");
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    parseResult(urlConnection.getInputStream());
                }else{
                    Log.e("not okay: ", responseCode+"");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
            intent.putExtra("message", 200);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    // Converting InputStream to list of shirts
    private void parseResult(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Gson gson = new Gson();
        ArrayList<Shirt> fetched_products = gson.fromJson(response.toString(), new TypeToken<ArrayList<Shirt>>(){}.getType());
        products = sanitizeProducts(fetched_products);

    }

    private ArrayList<Shirt> sanitizeProducts(ArrayList<Shirt> list){
        ArrayList<Shirt> sanitized = new ArrayList<>();
        for (Shirt shirt : list){
            if(shirt.quantity == 0 || shirt.name.equals("string"))
               continue; //NOTE: could check for duplicate items here as well.
            sanitized.add(shirt);
        }
        return sanitized;
    }
    //this is borrowed from Android developer site
    //this is invoked per view instantiation (as there is no reason to fetch all
    //images if they are never shown).
    private class DownloadImageTask extends AsyncTask<Shirt, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(Shirt... urls) {
            String urldisplay = urls[0].picture;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            //store image
            //urls[0].image = mIcon11;
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public Shirt getSingleShirt(int index){
        return products.get(index);
    }

    public int getProductCount(){
        return products.size();
    }

    //this is rather slow on large amounts of products
    //revert to a map if needed
    public Shirt getSingleShirtWithID(int id){
        for(Shirt shirt : products){
            if(shirt.id == id)
                return shirt;
        }
        return null;
    }


}

