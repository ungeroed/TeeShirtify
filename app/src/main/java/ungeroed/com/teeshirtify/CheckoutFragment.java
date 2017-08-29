package ungeroed.com.teeshirtify;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment {

    private BasketChangeListener mListener;

    private HashMap<Integer, Integer> basket;

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
        FrameLayout frameLayout =  (FrameLayout) view.findViewById(R.id.checkout_content);
        frameLayout.addView(createSingleLine());
        return view;
    }

    private View createSingleLine(){
        Log.d("here","run");
        LinearLayout line = new LinearLayout(getContext());

        line.setOrientation(LinearLayout.HORIZONTAL);
        TextView titleView = new TextView(getContext());
        titleView.append("sadfdsafsadfsd");
        titleView.setTextSize(20F);
        //line.addView(titleView);
        TextView titleView2 = new TextView(getContext());
        titleView2.setText("alabama");
        line.addView(titleView2);
        return titleView;
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
        // TODO: Update argument type and name
        void onBasketChange(HashMap<Integer, Integer> basket);
    }
}
