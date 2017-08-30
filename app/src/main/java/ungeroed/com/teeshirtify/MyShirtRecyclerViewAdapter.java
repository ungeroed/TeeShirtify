package ungeroed.com.teeshirtify;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ungeroed.com.teeshirtify.ShirtFragment.OnListFragmentInteractionListener;



/**
 * This is a custom RecyclerView, with relative few changes to the standard implementation.
 * Basically the viewholder has been altered to match the custom xml layouts file and the filters method has been
 * added.
 */
public class MyShirtRecyclerViewAdapter extends RecyclerView.Adapter<MyShirtRecyclerViewAdapter.ViewHolder> {

    //ApiHandler class fetches data from webservice
    ApiHandler handler;

    //these are the currently employed filters
    String[] filters = new String[]{"All", "All"};

    //listener for callback events when users selects an item
    private final OnListFragmentInteractionListener mListener;


    // ------------------------- lifecycle methods -----------------------

    /**
     * standard constructor
     * @param listener callback listener implementing correct interface
     */
    public MyShirtRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        handler = ApiHandler.getInstance();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_shirt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //fetch single shirt from the model
        Shirt shirt = handler.getSingleShirt(position, filters);
        holder.mItem = shirt;
        holder.title.setText(shirt.name.toUpperCase());
        holder.description.setText(String.format("Color: %s, Size: %s",shirt.colour,shirt.size));
        //start fetching the image asynchronously
        ApiHandler.getInstance().fetchImageFromUrl(holder.mImageView,shirt);

        //set actionlistener
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    //Notify the callback
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    // ------------------------- Lifecycle methods end -----------------------

    // ------------------------- Data adaptor methods -----------------------

    /**
     * @return current number of items in datasource, with the filters applied
     */
    @Override
    public int getItemCount() {
        return handler.getProductCount(filters);
    }


    /**
     * this methods updates the currently selected filters
     * @param size
     * @param color
     */
    public void setFilters(String size, String color){
        if(size != null)
            filters[0] = size;
        if(color != null)
            filters[1] = color;
        this.notifyDataSetChanged();
    }

    // ------------------------- Lifecycle methods end -----------------------

    // ------------------------- UI methods ---------------------------------

    /**
     * this contents of this method matches the custom layout xml
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView description;
        public ImageView mImageView;
        public Shirt mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.firstLine);
            description = (TextView) view.findViewById(R.id.secondLine);
            mImageView = (ImageView) view.findViewById(R.id.icon);
        }

    }

    // ------------------------- UI methods end ---------------------------------
}
