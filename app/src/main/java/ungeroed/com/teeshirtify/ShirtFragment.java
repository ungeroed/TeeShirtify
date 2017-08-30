package ungeroed.com.teeshirtify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.internal.NavigationMenu;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShirtFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    MyShirtRecyclerViewAdapter myAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShirtFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShirtFragment newInstance(int columnCount) {
        ShirtFragment fragment = new ShirtFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //use the apihandler singleton to fetch the data in a separate thread
        ApiHandler.getInstance().fetchInitial(getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Integer message = intent.getIntExtra("message", 0);
            Log.d("receiver", "Got message: " + message);
            if(message == 200)
                myAdapter.notifyDataSetChanged();
            else {
                Log.e("retrying", "fetching products again");
                ApiHandler.getInstance().fetchInitial(getContext());
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View filters = createFilterBar();
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.addView(filters);
        View view = inflater.inflate(R.layout.fragment_shirt_list, lin, false);
        lin.addView(view);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myAdapter = new MyShirtRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(myAdapter);
        }
        return lin;
    }

    private View createFilterBar(){
        LinearLayout filters = new LinearLayout(getContext());
        filters.setBackgroundColor(Color.LTGRAY);
        filters.setMinimumHeight(36);



        TextView t = new TextView(getContext());
        t.setPadding(46,9,9,9);
        t.setText("Size: ");
        t.setTextSize(14F);
        filters.addView(t);

        Spinner spinner = new Spinner(getContext());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.Sizes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        filters.addView(spinner);

        TextView col = new TextView(getContext());
        col.setPadding(16,9,9,9);
        col.setText("Color: ");
        col.setTextSize(14F);
        filters.addView(col);

        Spinner color_spinner = new Spinner(getContext());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> color_adapter = ArrayAdapter.createFromResource(getContext(), R.array.colors, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        color_spinner.setAdapter(adapter);
        color_spinner.setGravity(Gravity.END);
        filters.addView(color_spinner);
        return filters;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("count: ", ""+myAdapter.getItemCount());
    }

    /**
     * selecting an item should show details.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Shirt item);
    }
}
