package ungeroed.com.teeshirtify;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;


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
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
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
        //use the apihandler singleton to fetch the data in a separate thread
        ApiHandler.getInstance().fetchInitial(getApplicationContext());
        basket = new HashMap<Integer, Integer>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naviagtion);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.content) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                Log.e("here","not null");
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ShirtFragment firstFragment = ShirtFragment.newInstance(1);

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.content, firstFragment).commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    }

    //create the list fragment and maybe scroll to previous location
    private void showListFragment(){
        ShirtFragment listFragment = ShirtFragment.newInstance(1);
        getFragmentManager().beginTransaction().replace(R.id.content, listFragment).commit();

    }

    //create the list fragment and maybe scroll to previous location
    private void showCheckoutFragment(){
        Bundle checkout_bundle = new Bundle();
        checkout_bundle.putSerializable("basket", basket);
        CheckoutFragment checkoutFragment = CheckoutFragment.newInstance(checkout_bundle);
        getFragmentManager().beginTransaction().replace(R.id.content, checkoutFragment).commit();
    }

    @Override
    public void onBasketChange(HashMap<Integer, Integer> basket) {
        this.basket = basket;
    }
}
