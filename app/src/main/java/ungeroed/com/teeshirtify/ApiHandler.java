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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
            intent.putExtra("message", "This is my message!");
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
        products = gson.fromJson(response.toString(), new TypeToken<ArrayList<Shirt>>(){}.getType());
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
            urls[0].image = mIcon11;
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
}

