package ungeroed.com.teeshirtify;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ungeroed.com.teeshirtify.ShirtFragment.OnListFragmentInteractionListener;
import ungeroed.com.teeshirtify.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyShirtRecyclerViewAdapter extends RecyclerView.Adapter<MyShirtRecyclerViewAdapter.ViewHolder> {

    ApiHandler handler;
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
        Shirt shirt = handler.getSingleShirt(position);
        holder.mItem = shirt;
        holder.mIdView.setText(shirt.name);
        holder.mContentView.setText("kr."+shirt.price);
        ApiHandler.getInstance().fetchImageFromUrl(holder.mImageView,shirt);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return handler.getProductCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ImageView mImageView;
        public Shirt mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.thumbImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
