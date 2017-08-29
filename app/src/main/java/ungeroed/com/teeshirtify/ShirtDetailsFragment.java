package ungeroed.com.teeshirtify;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShirtDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShirtDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShirtDetailsFragment extends Fragment {

    private Shirt shirt;

    private OnFragmentInteractionListener mListener;

    public ShirtDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShirtDetailsFragment.
     */
    public static ShirtDetailsFragment newInstance(Shirt shirt) {
        ShirtDetailsFragment fragment = new ShirtDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("shirt", shirt);
        fragment.setArguments(args);
        return fragment;
    }

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
        if(shirt.image != null)
            mImageView.setImageBitmap(shirt.image);
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
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
