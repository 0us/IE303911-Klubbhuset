package no.ntnu.klubbhuset.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.ui.userviews.mymemberships.MyMemberhipsFragment;
import no.ntnu.klubbhuset.ui.userviews.mymemberships.MyMembershipsListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Club} and makes a call to the
 * specified {@link MyMembershipsListFragment.OnListFragmentInteractionListener}.
 */
public class ClubMembershipRecyclerViewAdapter extends RecyclerView.Adapter<ClubMembershipRecyclerViewAdapter.ViewHolder> {

    private final List<Club> mValues;
    private final MyMembershipsListFragment.OnListFragmentInteractionListener mListener;

    public ClubMembershipRecyclerViewAdapter(List<Club> items, MyMembershipsListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_club, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mMembercountView.setText("0"); // TODO
        if (holder.mItem.getOrgImages() == null || holder.mItem.getOrgImages().length == 0) {
            // set placeholder
            holder.mLogo.setImageResource(R.drawable.ic_broken_image_black_24dp);
        } else {
            holder.mLogo.setImageBitmap(holder.mItem.getOrgImages()[0].getImage());
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView mNameView;
        public TextView mMembercountView;
        public ImageView mLogo;
        public Club mItem;
        public ViewHolder(View v) {
            super(v);
            view = v;
            mNameView = v.findViewById(R.id.club_name);
            mMembercountView = v.findViewById(R.id.club_member_count);
            mLogo = v.findViewById(R.id.club_logo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
