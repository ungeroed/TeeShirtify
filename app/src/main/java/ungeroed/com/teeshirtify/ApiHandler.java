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
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;


/**
 * This class handles all interaction with the mock shirts API.
 * It also functions as the model for the MVC - for simplicity.
 * Note: future versions should compartmentalize those aspects in separate entities.
 * The interaction with the api is implemented i a few different ways to show different approaches.
 */
public class ApiHandler {

    //Fetched products
    ArrayList<Shirt> products;


    AsyncShirtFetcher shirt_fetcherProvider = new AsyncShirtFetcher();

    //----------------------- Constructor methods ----------------------------

    //private constructor to force singleton instantiation
    public ApiHandler(){
        //DaggerAppComponent.create().inject(this);
        if (products == null){products  = new ArrayList<Shirt>(); }

    }

    //----------------------- Constructor methods end ------------------------

    //----------------------- Model methods ----------------------------------

    /**
     * This method returns the filtered products. It could look a lot nicer with java 8 stream
     * interface.. Cmon google just enable that for 1.8 in android.
     * @param filters
     * @return
     */
    private ArrayList<Shirt> getFilteredProducts(String[] filters){
        ArrayList<Shirt> filtered = new ArrayList<Shirt>();
        for(Shirt s : products){
            if ((s.size.toLowerCase().equals(filters[0].toLowerCase()) || filters[0].equals("All")) &&
                    (s.colour.toLowerCase().equals(filters[1].toLowerCase()) || filters[1].equals("All")))
                filtered.add(s);
        }
        return filtered;
    }

    /**
     * This shirt returns the correct t-shirt for the position calculated after
     * the filters have benn applied
     * @param index
     * @param filters
     * @return
     */
    public Shirt getSingleShirt(int index, String[] filters){
        return getFilteredProducts(filters).get(index);
    }

    /**
     * This method returns the number of products available after the filters have been applied
     * @param filters
     * @return
     */
    public int getProductCount(String[] filters){
        return getFilteredProducts(filters).size();
    }

    /**
     * Gets a single t-shirt that has the supplied id.
     * this is rather slow on large amounts of products
     * Note: if needed revert to a map based implementation instead of a list
     */

    public Shirt getSingleShirtWithID(int id){
        for(Shirt shirt : products){
            if(shirt.id == id)
                return shirt;
        }
        return null;
    }
    //----------------------- Model methods end ------------------------------


    //----------------------- Product methods --------------------------------

    /**
     * Initiates the fetching of all initial products using an async task
     * @param context
     */
    public void fetchInitial(Context context){
        shirt_fetcherProvider.execute(context);
    }


    /**
     * AsyncTask designed to fetch shirt details from the api
     * and notify the UI when the fetching is done.
     * it broadcasts the statuscode so that the receiver can respond EG. by
     * retrying if unsuccessful
     */
    private class AsyncShirtFetcher extends AsyncTask<Context, Void, Context> {


        @Override
        protected Context doInBackground(Context[] params) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("http://mock-shirt-backend.getsandbox.com/shirts");
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    //if the responsecode is 200 we parse the result
                    parseResult(urlConnection.getInputStream());
                }else{
                    //else we notify the receiver that the request was unsuccessful
                    Intent intent = new Intent("custom-event-name");
                    // Im just sending error 500, but we could send the correct
                    //error code to react accordingly for different types of errors
                    intent.putExtra("message", 500);
                    LocalBroadcastManager.getInstance(params[0]).sendBroadcast(intent);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * this method automatically runs on UI thread and notfies the receiver after the
         * parsing has commenced.
         * Note: since we're using local broadcast as pub/sub pattern, it is actually not
         * needed (and maybe a bit taxing) that this runs on UI thread.
         * @param context
         */
        @Override
        protected void onPostExecute(Context context) {
            super.onPostExecute(context);
            Intent intent = new Intent("custom-event-name");
            // You can also include some extra data.
            intent.putExtra("message", 200);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        /**
         * This method Converting InputStream to list of shirts.
         * it uses Gson for an automatic Unmarshalling of the json response.
         * @param in
         */
        private void parseResult(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                //NOTE:probably use stringbuilder instead
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
            //we use Gson for unmarshalling based on specific class type
            Gson gson = new Gson();
            //since we employ reflection here, it is not recommended if blazing speed is required.
            ArrayList<Shirt> fetched_products = gson.fromJson(response.toString(), new TypeToken<ArrayList<Shirt>>(){}.getType());
            sanitizeProducts(fetched_products);

        }
    }



    /**
     * This method removes the corrupt data entries fetched form the server
     * @param list
     * @return
     */
    public ArrayList<Shirt> sanitizeProducts(ArrayList<Shirt> list){
        ArrayList<Shirt> sanitized = new ArrayList<>();
        for (Shirt shirt : list){
            if(shirt.quantity == 0 || shirt.name.equals("string"))
                continue; //NOTE: could check for duplicate items here as well.
            sanitized.add(shirt);
        }
        products = sanitized;
        return sanitized;
    }

    //----------------------- Product methods end ----------------------------

    //----------------------- Image methods  ---------------------------------

    /**
     * This method initiates the fetching of images for a shirt.
     * It fetches each img asynchronously and updates the supplied ImageView.
     * this is invoked per view instantiation (as there is no reason to fetch all
     * images if they are never shown).
     * @param view
     * @param shirt
     */
    public void fetchImageFromUrl(ImageView view, Shirt shirt){
        new DownloadImageTask(view).execute(shirt);
    }

    /**
     * This asyncTask fetches a single image and updates the supplied imageview
     * after a successful image retrieval.
     * Note: this is borrowed from Android developer site
     */
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
            //Note: we could store each image here with each shirt, but it would
            //make shirts unserializaable. Instead we rely on optimization in
            // caching of fetched urls.
            return mIcon11;
        }
        //update imageview on sucessful retrieval
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    //----------------------- Image methods  end -----------------------------


    //----------------------- Order methods ----------------------------------

    /**
     * This method creates the json object for the order posts it to the mock api.
     * It uses threading to execute a runnable task in the background.
     * The result is communicated to the UI thread via localBroadcast intents.
     * @param con
     * @param basket
     */
    public void placeOrder(final Context con, final HashMap<Integer,Integer> basket){
            //if there is no shirts in basket we return
            if(basket.isEmpty()){return;}
            //atomic integer is not strictly necessary here, but is a nice theadsafe object
            final AtomicInteger total = new AtomicInteger(0);
            //calculate total price
            for (Integer key : basket.keySet()){
                total.addAndGet((getSingleShirtWithID(key).price)*basket.get(key));
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //basic http post request
                        URL url = new URL("http://mock-shirt-backend.getsandbox.com/order");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        conn.setRequestProperty("Accept","application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        //create json object
                        JSONObject order = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        order.put("total", total.intValue());
                        //add all products to json array
                        for(Integer key : basket.keySet()){
                            JSONObject shirt = new JSONObject();
                            shirt.put("id", key);
                            shirt.put("quantity", basket.get(key));
                            jsonArray.put(shirt);
                        }
                        JSONObject basket = new JSONObject();
                        basket.put("shirts", jsonArray);
                        order.put("basket", basket);

                        //create outputstrream and send data
                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
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
                        //in case of exceptions we broadcast that the request was unsuccessful
                        Intent intent = new Intent(CheckoutFragment.ORDER_EVENTS);
                        //use correct code if several different reactions are needed
                        intent.putExtra("response", 404);
                        LocalBroadcastManager.getInstance(con).sendBroadcast(intent);
                    }
                }
            });

            thread.start();

    }

    //----------------------- Order methods end ------------------------------






}

