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
import android.widget.AdapterView;
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


    private OnListFragmentInteractionListener mListener;
    MyShirtRecyclerViewAdapter myAdapter;

    private static int current_size = 0;
    private static int current_color = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShirtFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Integer size = getArguments().getInt("size");
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Integer message = intent.getIntExtra("message", 0);
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

        Spinner size_spinner = new Spinner(getContext());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.Sizes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        size_spinner.setAdapter(adapter);
        size_spinner.setSelection(current_size);
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
        //pops the details fragment
        void onListFragmentInteraction(Shirt item);
    }
}
