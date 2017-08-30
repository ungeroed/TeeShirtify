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
 * specified {@link OnListFragmentInteractionListener}.
 *
 */
public class MyShirtRecyclerViewAdapter extends RecyclerView.Adapter<MyShirtRecyclerViewAdapter.ViewHolder> {

    ApiHandler handler;
    String[] filters = new String[]{"All", "All"};

    private final OnListFragmentInteractionListener mListener;

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
        Shirt shirt = handler.getSingleShirt(position, filters);
        holder.mItem = shirt;
        holder.title.setText(shirt.name.toUpperCase());
        holder.description.setText(String.format("Color: %s, Size: %s",shirt.colour,shirt.size));
        ApiHandler.getInstance().fetchImageFromUrl(holder.mImageView,shirt);

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


    @Override
    public int getItemCount() {
        return handler.getProductCount(filters);
    }

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

        @Override
        public String toString() {
            return super.toString() + " '";
        }
    }

    public void setFilters(String size, String color){
        if(size != null)
            filters[0] = size;
        if(color != null)
            filters[1] = color;
        this.notifyDataSetChanged();
    }
}
