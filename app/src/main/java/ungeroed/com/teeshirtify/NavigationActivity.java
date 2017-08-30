package ungeroed.com.teeshirtify;


import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.HashMap;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static ungeroed.com.teeshirtify.CheckoutFragment.ORDER_EVENTS;


public class NavigationActivity extends AppCompatActivity implements ShirtFragment.OnListFragmentInteractionListener, ShirtDetailsFragment.OnFragmentInteractionListener , CheckoutFragment.BasketChangeListener{

    private TextView mTextMessage;

    private HashMap<Integer, Integer> basket;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        basket = new HashMap<Integer, Integer>();
        super.onCreate(savedInstanceState);
        //use the apihandler singleton to fetch the data in a separate thread
        ApiHandler.getInstance().fetchInitial(getApplicationContext());

        setContentView(R.layout.activity_naviagtion);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.content) != null) {

            // if restored from a previous state, just return
            if (savedInstanceState != null) {
                Log.e("here","not null");
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ShirtFragment firstFragment = new ShirtFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.content, firstFragment).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        View decorView = getWindow().getDecorView();
        //      Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        LocalBroadcastManager.getInstance(NavigationActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter(ORDER_EVENTS));

    }

    @Override
    protected void onResume(){
        super.onResume();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("basket", basket);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListFragmentInteraction(Shirt item) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        ShirtDetailsFragment detailsFragment = ShirtDetailsFragment.newInstance(item);
        fragmentTransaction.replace(R.id.content, detailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Shirt shirt) {
        Context context = getApplicationContext();
        CharSequence text = shirt.name + " added to Basket";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        updateBasket(shirt);
    }

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
    Badge badge;
    private void updateBadge(Boolean clear){
        if(!clear){
            if(badge == null) {
                //update badge in navigation item
                BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                BottomNavigationMenuView bottomNavigationMenuView =
                        (BottomNavigationMenuView) navigation.getChildAt(0);
                View v = bottomNavigationMenuView.getChildAt(1); // number of menu from left
                badge = new QBadgeView(this).bindTarget(v).setBadgeGravity(Gravity.CENTER | Gravity.END).setGravityOffset(29F, true).setBadgeNumber(getBasketSize());
            } else {
                badge.setBadgeNumber(getBasketSize());
            }
        } else {
            badge.hide(true);
        }
    }
    //create the list fragment and maybe scroll to previous location
    private void showListFragment(){
        ShirtFragment listFragment = new ShirtFragment();
        getFragmentManager().beginTransaction().replace(R.id.content, listFragment).commit();

    }

    //create the list fragment and maybe scroll to previous location
    private void showCheckoutFragment(){
        Bundle checkout_bundle = new Bundle();
        checkout_bundle.putSerializable("basket", basket);
        CheckoutFragment checkoutFragment = CheckoutFragment.newInstance(checkout_bundle);
        getFragmentManager().beginTransaction().replace(R.id.content, checkoutFragment).commit();
    }

    private int getBasketSize(){
        int count = 0;
        for(Integer value : basket.values())
            count += value;
        return count;
    }

    @Override
    public void onBasketChange(Boolean place_order) {
        if(!place_order){
            basket.clear();
            updateBadge(true);
            Context context = getApplicationContext();
            CharSequence text = "Shopping basket cleared";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            showListFragment();
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Placing order please wait";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            ApiHandler.getInstance().placeOrder(getApplicationContext(), basket);
        }


    }

    private AlertDialog shown;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Integer status = intent.getIntExtra("response",400);
            if(status != 200){
                CharSequence text = "Could not send order, please try again";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }
            if(shown != null){
                shown.show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(NavigationActivity.this);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View v = inflater.inflate(R.layout.order_dialog, null, false);
                TextView textview = (TextView) v.findViewById(R.id.order_textview);
                textview.setText("Sucessfully placed order!");
                ImageView imageview = (ImageView) v.findViewById(R.id.order_successful);
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
            }
            onBasketChange(false);
            showListFragment();
            BottomNavigationView bottomNavigationView;
            bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        }
    };

}
