package ungeroed.com.teeshirtify;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple fragment to display shirt details
 * Activities that contain this fragment must implement the
 * OnFragmentInteractionListener interface
 * to handle purchase events.
 */
public class ShirtDetailsFragment extends Fragment {

    //The currently selected shirt
    private Shirt shirt;

    //the current listener on callback puchase events
    private OnFragmentInteractionListener mListener;

    //required empty public constructor
    public ShirtDetailsFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ShirtDetailsFragment.
     */
    public static ShirtDetailsFragment newInstance(Shirt shirt) {
        ShirtDetailsFragment fragment = new ShirtDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("shirt", shirt);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Standard lifecycle oncreate which restores the selected shirt
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shirt = (Shirt) getArguments().getSerializable("shirt");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shirt_details, container, false);
        ImageView mImageView = (ImageView) view.findViewById(R.id.largeImage);
        ApiHandler.getInstance().fetchImageFromUrl(mImageView,shirt);
        TextView nameView = (TextView) view.findViewById(R.id.product_name);
        nameView.setText(shirt.name);
        TextView colorView = (TextView) view.findViewById(R.id.product_color);
        colorView.setText(shirt.colour);
        TextView sizeView = (TextView) view.findViewById(R.id.product_size);
        sizeView.setText("\""+shirt.size+"\"");
        TextView quantityView = (TextView) view.findViewById(R.id.product_quantity);
        quantityView.setText(shirt.quantity+"");
        TextView priceView = (TextView) view.findViewById(R.id.product_price);
        priceView.setText("Kr. "+ shirt.price);
        FrameLayout buyBtn = (FrameLayout) view.findViewById(R.id.buyBtn);
        //when the BUY button is pressed we notify the callback listener
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface that an item has been selected.
                    mListener.onFragmentInteraction(shirt);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * This interface is used to let the main activity know that the user pressed
     * the purchase button
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Shirt shirt);
    }
}
