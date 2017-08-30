package ungeroed.com.teeshirtify;
/**
 * This is the main activity. Its main function is to coordinate and show different fragments
 * as the user navigates the application. It holds and listens to callback methods from all fragments
 * and handles persistance between app restarts.
 */

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static ungeroed.com.teeshirtify.CheckoutFragment.ORDER_EVENTS;


public class NavigationActivity extends AppCompatActivity implements ShirtFragment.OnListFragmentInteractionListener, ShirtDetailsFragment.OnFragmentInteractionListener , CheckoutFragment.BasketChangeListener{

    //Stores key = id and value = quantity
    private HashMap<Integer, Integer> basket = new HashMap<Integer, Integer>();

    //displays an items count on the shopping basket
    private Badge badge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //use the Apihandler singleton to fetch the data in a separate thread
        ApiHandler.getInstance().fetchInitial(getApplicationContext());

        setContentView(R.layout.activity_naviagtion);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.content) != null) {

            // if restored from a previous state, just return
            //this is to avoid duplicated fragments
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ShirtFragment firstFragment = new ShirtFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.content, firstFragment).commit();
        }
        //set navigationlistener
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //this hides the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //set listener to local broadcast events - pub/sub pattern for network events
        LocalBroadcastManager.getInstance(NavigationActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter(ORDER_EVENTS));

        //restore shopping basket from previous state
        HashMap<Integer, Integer> restoredBasket = restoreBasket();
        if(restoredBasket != null) {
            basket = restoredBasket;
            updateBadge(false);
        }

    }


    //-------------------------- Persistence methods  --------------------------------

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveBasket();
    }

    /**
     * Saves the current basket to persistance.
     * Im using google Gson to persist the hashmap as a string in sharedprefs, which
     * is a bit of a hack to avoid setting up SQlite to store such few values
     */
    private void saveBasket() {
        //convert to string using gson
        Gson gson = new Gson();
        String mapString = gson.toJson(basket);

        //save in shared prefs
        SharedPreferences prefs = getSharedPreferences("basket_prefs", MODE_PRIVATE);
        prefs.edit().putString("basket", mapString).apply();
    }

    /**
     * Restores the shopping basket from shared prefs.
     * @return
     */
    private HashMap<Integer, Integer> restoreBasket() {
        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreferences("basket_prefs", MODE_PRIVATE);
        //null is the default value if fetching fails
        String storedHashMapString = prefs.getString("basket", null);
        if(storedHashMapString == null)
            return null;
        //unmarshalling to simple hashmap type
        return gson.fromJson(storedHashMapString, new TypeToken<HashMap<Integer, Integer>>(){}.getType());

    }


    //-------------------------- Persistence methods end --------------------------------

    //-------------------------- Fragment callback methods  --------------------------------

    /**
     * Callback method for whenever users click on list items.
     * @param item the item that was clicked.
     * the method pushes the details fragment
     */
    @Override
    public void onListFragmentInteraction(Shirt item) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        ShirtDetailsFragment detailsFragment = ShirtDetailsFragment.newInstance(item);
        fragmentTransaction.replace(R.id.content, detailsFragment);
        //we use addToBackStack to enable natural android back button
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * The callback method for the puchase button in the details view.
     * @param shirt
     * the method updates the shopping basket & badge and shows
     * a toast to the user.
     */
    @Override
    public void onFragmentInteraction(Shirt shirt) {
        Context context = getApplicationContext();
        CharSequence text = shirt.name + " added to Basket";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        updateBasket(shirt);
    }

    /**
     * The callback method for the checkout fragment
     * @param place_order the parameter is used to distinguish
     * whether the user clicked 'clear basket' or 'place order'
     */
    @Override
    public void onBasketChange(Boolean place_order) {
        //If clear basket was pressed, we clear the cart and
        //show the user a toast.
        if(!place_order){
            basket.clear();
            updateBadge(true);
            Context context = getApplicationContext();
            CharSequence text = "Shopping basket cleared";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            //show list fragment after clearing shopping cart
            showListFragment();
        } else {
            //here we show a toast and place the order using ApiHandler
            Context context = getApplicationContext();
            CharSequence text = "Placing order please wait";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            ApiHandler.getInstance().placeOrder(getApplicationContext(), basket);
        }


    }

    //-------------------------- Fragment callback methods end --------------------------------

    //-------------------------- UI Update methods --------------------------------

    /**
     * This method updates the shopping basket.
     * @param shirt the shirt that is added to the basket.
     * if the shirt id is already in the map, we increment the value
     */
    private void updateBasket(Shirt shirt){
        if (basket.containsKey(shirt.id)){
            Integer count = basket.get(shirt.id);
            count++;
            basket.put(shirt.id, count);
        }else {
            basket.put(shirt.id, 1);
        }
        updateBadge(false);
    }

    /**
     * This method updates the shopping cart badge to reflect the number of
     * items in the cart
     * @param clear if true, the shopping cart has been cleard and we reset
     * and removes the current badge
     */
    private void updateBadge(Boolean clear){
        if(!clear){
            //if no badge exists, create it
            if(badge == null) {
                //update badge in navigation item
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                BottomNavigationMenuView bottomNavigationMenuView =
                        (BottomNavigationMenuView) navigation.getChildAt(0);
                View v = bottomNavigationMenuView.getChildAt(1); // number of menu from left
                badge = new QBadgeView(this).bindTarget(v).setBadgeGravity(Gravity.CENTER | Gravity.END).setGravityOffset(29F, true).setBadgeNumber(getBasketSize());
            } else {
                //else just set the correct number
                badge.setBadgeNumber(getBasketSize());
            }
        } else {
            //if clear, we hide the badge
            badge.hide(true);
        }
    }

    /**
     * utility method to determine current total amount of elements in the basket
     * @return
     */
    private int getBasketSize(){
        int count = 0;
        for(Integer value : basket.values())
            count += value;
        return count;
    }

    //-------------------------- UI Update methods END --------------------------------

    //-------------------------- navigation --------------------------------

    /**
     * standard listener for bottom navigationview.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showListFragment();
                    return true;
                case R.id.navigation_notifications:
                    showCheckoutFragment();
                    return true;
            }
            return false;
        }

    };

    /**
     * creates and pushes the list fragment
     * Note: future version should scroll to previous location
     */
    private void showListFragment(){
        ShirtFragment listFragment = new ShirtFragment();
        getFragmentManager().beginTransaction().replace(R.id.content, listFragment).commit();

    }

    /**
     * Creates and pushes the checkout fragment.
     * the shopping cart is serialized and included as a bundle
     */
    private void showCheckoutFragment(){
        Bundle checkout_bundle = new Bundle();
        checkout_bundle.putSerializable("basket", basket);
        CheckoutFragment checkoutFragment = CheckoutFragment.newInstance(checkout_bundle);
        getFragmentManager().beginTransaction().replace(R.id.content, checkoutFragment).commit();
    }

    //-------------------------- navigation end -----------------------------

    //-------------------------- Local Intent receiver methods --------------

    /**
     * This method catches events fired by the ApiHandler with http statuscodes from
     * the post call to place the order.
     * if successfull we show a dialog with acknowledgement and clear the shopping cart, if not
     * we show a toast to the user and she is free to try to place order again. We don't currently
     * autoresend unsuccessful orders, as that could easilty be a slippery slope with race conditions.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //If the post request was not successfull, we show a
            //toast to the user
            Integer status = intent.getIntExtra("response",400);
            if(status != 200){
                CharSequence text = "Could not send order, please try again";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }
            //build the custom dialogue
            AlertDialog.Builder dialog = new AlertDialog.Builder(NavigationActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.order_dialog, null, false);
            TextView textview = (TextView) v.findViewById(R.id.order_textview);
            textview.setText("Sucessfully placed order!");
            ImageView imageview = (ImageView) v.findViewById(R.id.order_successful);
            //Glide is used to enable animated gifs in imageviews
            Glide.with(NavigationActivity.this)
                    .load(R.drawable.checkmark)
                    .into(imageview);
            dialog.setView(v);
            dialog.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();

            //when successfull we clear the basket
            onBasketChange(false);
            //and shows listfragment
            showListFragment();
            //we make sure the correct bottomn avigation item is currently highlighted
            BottomNavigationView bottomNavigationView;
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        }
    };

    //-------------------------- Local Intent receiver methods end --------------

}
