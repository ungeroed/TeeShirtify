package ungeroed.com.teeshirtify;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.HashMap;


/**
 * A simple fragment to display the contents of the shopping basket.
 */
public class CheckoutFragment extends Fragment {

    //this is the callback listener
    private static BasketChangeListener mListener;

    //this is the current sopping cart
    private HashMap<Integer, Integer> basket;

    //this is the name of the broadcast event for order post requests.
    static String ORDER_EVENTS = "order-events";
    //current calculated total price
    private Integer totalPrice = 0;


    //------------------------------ instantiation methods -----------------------

    // Required empty public constructor
    public CheckoutFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment
     */
    public static CheckoutFragment newInstance(Bundle basket) {
        CheckoutFragment fragment = new CheckoutFragment();
        fragment.setArguments(basket);
        return fragment;
    }

    //------------------------------ instantiation methods -----------------------

    //------------------------------ lifecycle methods ---------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            basket = (HashMap<Integer,Integer>)getArguments().getSerializable("basket");
        }

    }

    /**
     * standard layout inflation with some programatically added content.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        LinearLayout linearLayout =  (LinearLayout) view.findViewById(R.id.checkout_content);
        for(Integer key : basket.keySet()){
            linearLayout.addView(createSingleLine(basket.get(key), key));
        }

        //Programatically create a divider to separate price line
        LinearLayout.LayoutParams divider_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        divider_params.height = 1;
        View divider = new View(getContext());
        divider.setLayoutParams(divider_params);
        divider.setBackgroundColor(Color.BLACK);
        linearLayout.addView(divider);

        //line containing calculated price
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 2;
        TextView price_label = new TextView(getContext());
        price_label.setText("Total price:");
        price_label.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
        price_label.setLayoutParams(params);
        line.addView(price_label);
        TextView price = new TextView(getContext());
        price.setText(String.format("%d Kr.",totalPrice));
        price.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
        line.addView(price);

        //lines are added to the content view
        linearLayout.addView(line);
        //add actionlistener to clear basket button
        Button clear_btn = (Button) view.findViewById(R.id.clear_basket);
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basket.clear();
                mListener.onBasketChange(false);
            }
        });

        //add actionlistener to place order button
        Button place_order_btn = (Button) view.findViewById(R.id.place_order);
        place_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mListener.onBasketChange(true);

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BasketChangeListener) {
            mListener = (BasketChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //------------------------------ Lifecycle methods end -----------------------

    //------------------------------ UI methods ----------------------------------

    /**
     * This method creates a single line to display the purchased product, the amount
     * and totalt price for that specific product. It is used in conjunction with looping through
     * all ids of the shopping cart
     * @param quantity
     * @param id
     * @return
     */
    private View createSingleLine(Integer quantity, int id){
        //fetch the correct product
        Shirt shirt = ApiHandler.getInstance().getSingleShirtWithID(id);
        //calculate total price
        totalPrice += (quantity * shirt.price);
        //create layout
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 2;
        //create textviews for product details
        TextView titleView = new TextView(getContext());
        titleView.setText(String.format("%d x %s",quantity,shirt.name));
        titleView.setLayoutParams(params);
        titleView.setTextSize(20F);
        line.addView(titleView);
        TextView titleView2 = new TextView(getContext());
        Integer price = quantity * shirt.price;
        titleView2.setText(String.format("%d kr.",price));
        titleView2.setTextSize(20F);
        line.addView(titleView2);

        return line;
    }

    //------------------------------ UI methods end -----------------------

    /**
     * Callback interface. The method uses a boolean parameter to determine which button was
     * pressed in the ui
     */
    public interface BasketChangeListener {
        void onBasketChange(Boolean place_order);
    }
}
