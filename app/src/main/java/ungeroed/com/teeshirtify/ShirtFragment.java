package ungeroed.com.teeshirtify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * A fragment representing a list of shirts.
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShirtFragment extends Fragment {

    //interface that implements callback method
    private OnListFragmentInteractionListener mListener;

    //the custom listviewadapter
    @Inject MyShirtRecyclerViewAdapter myAdapter;

    @Inject ApiHandler handler;
    //Fragment variables are used to hold current filter selections as opposed to savedInstancestate
    //this is because lifecyclemethods are not called when fragments are replaced
    private static int current_size = 0;
    private static int current_color = 0;

    //--------------------------- lifecycle methods ---------------------------------



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //register reciver for api shirts get request, so we can refresh view when data is fetched
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
    }

    /**
     * Standard createview method. It is altered a bit to create and inject the filter-bar.
     * this is done programatically to show a different way of doing that instead of using xml.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //filters view is created programmatically
        View filters = createFilterBar();
        //instead of using the designated container, we inflate the list in a
        //linear layout with the filters
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.addView(filters);
        View view = inflater.inflate(R.layout.fragment_shirt_list, lin, false);
        lin.addView(view);
        // Set the adapter and layoutmanager of the recyclerview.
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            //myAdapter = new MyShirtRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(myAdapter);
        }
        return lin;
    }

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);
        //attache the callback listener
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
        //detach callback listener and local broadcast receiver
        mListener = null;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);

    }

    //--------------------------- lifecycle methods end ---------------------------------


    //--------------------------- UI methods --------------------------------------------

    /**
     * This method creates the view with the filter selectin spinners.
     * @return the filters view.
     * It also sets actionlisteners on the spinners to react to changes
     */
    private View createFilterBar(){
        //top layout
        LinearLayout filters = new LinearLayout(getContext());
        filters.setBackgroundColor(Color.LTGRAY);
        filters.setMinimumHeight(36);

        //size label
        TextView t = new TextView(getContext());
        t.setPadding(46,9,9,9);
        t.setText("Size: ");
        t.setTextSize(14F);
        filters.addView(t);

        //creates the spinner with size values
        Spinner size_spinner = new Spinner(getContext());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.Sizes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        size_spinner.setAdapter(adapter);
        //restore current remembered selection, while the app is running
        size_spinner.setSelection(current_size);

        //set actionlistener to react to item selections from list
        size_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                current_size = i;
                myAdapter.setFilters((String) adapterView.getItemAtPosition(i),null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        filters.addView(size_spinner);

        //create color label
        TextView col = new TextView(getContext());
        col.setPadding(16,9,9,9);
        col.setText("Color: ");
        col.setTextSize(14F);
        filters.addView(col);

        //create color spinner
        Spinner color_spinner = new Spinner(getContext());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> color_adapter = ArrayAdapter.createFromResource(getContext(), R.array.colors, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        color_spinner.setAdapter(color_adapter);
        color_spinner.setSelection(current_color);
        //set actionListener on spinner to react to changes
        color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                current_color = i;
                myAdapter.setFilters(null, (String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        color_spinner.setGravity(Gravity.END);
        filters.addView(color_spinner);
        return filters;
    }

    // -------------------------- UI methods end -----------------------------------------

    // -------------------------- callback methods ---------------------------------------


    /**
     * This interface allows selecting an item to push details view
     * in main activity.
     */
    public interface OnListFragmentInteractionListener {
        //pops the details fragment
        void onListFragmentInteraction(Shirt item);
    }

    // -------------------------- callback methods end--------------------------------------

    // ----------------------- Local Intent receiver methods --------------------

    /**
     * This method receives updates on fetching main shirt data.
     * If the get request was successful, we notify the adapter that
     * new data is available. If its not successful, we retry the request.
     * Note: future versions should only retry once or twice and inform user
     * instead of just using log statement. It could also entail checking network
     * status eg. waiting for internet connecting etc.
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Integer message = intent.getIntExtra("message", 0);
            if(message == 200)
                myAdapter.notifyDataSetChanged();
            else {
                Log.d("retrying", "fetching products again");
                handler.fetchInitial(getContext());
            }
        }
    };

    // ----------------------- Local Intent receiver methods end -----------------
}
