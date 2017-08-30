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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

    private static BasketChangeListener mListener;

    private HashMap<Integer, Integer> basket;

    static String ORDER_EVENTS = "order-events";
    private Integer totalPrice = 0;



    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(Bundle basket) {
        CheckoutFragment fragment = new CheckoutFragment();
        fragment.setArguments(basket);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            basket = (HashMap<Integer,Integer>)getArguments().getSerializable("basket");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        LinearLayout linearLayout =  (LinearLayout) view.findViewById(R.id.checkout_content);
        for(Integer key : basket.keySet()){
            linearLayout.addView(createSingleLine(basket.get(key), key));
        }

        //Programstically create a divider to separate price line
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

    private View createSingleLine(Integer quantity, int id){
        Shirt shirt = ApiHandler.getInstance().getSingleShirtWithID(id);
        totalPrice += (quantity * shirt.price);
        LinearLayout line = new LinearLayout(getContext());
        line.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 2;

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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface BasketChangeListener {
        void onBasketChange(Boolean place_order);
    }
}
