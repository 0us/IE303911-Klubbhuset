package no.ntnu.klubbhuset.adapter;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Club;
import no.ntnu.klubbhuset.viewmodels.ClubsViewModel;
import no.ntnu.klubbhuset.ui.userviews.home.ClubsListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Club} and makes a call to the
 * specified {@link ClubsListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ClubsRecyclerViewAdapter extends RecyclerView.Adapter<ClubsRecyclerViewAdapter.ViewHolder> {

    private final List<Club> mValues;
    private final ClubsListFragment.OnListFragmentInteractionListener mListener;
    private ClubsViewModel model;

    public ClubsRecyclerViewAdapter(List<Club> items,
                                    ClubsListFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_club, parent, false);
        model = ViewModelProviders.of(
                (FragmentActivity) parent.getContext()).get(ClubsViewModel.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
//        holder.mMembercountView.setText("0"); TODO Count Dracula
        if (holder.mItem.getOrgImages() == null || holder.mItem.getOrgImages().length == 0) {
            // set placeholder
            holder.mLogo.setImageResource(R.drawable.ic_broken_image_black_24dp);
        } else {
            // TODO: currently only fetches the image at index 0, as there is no classifier for the
            // TODO: images, e.g if the image is Logo or Banner
            holder.mLogo.setImageBitmap(holder.mItem.getOrgImages()[0].getImage());
        }

        holder.view.setOnClickListener(v -> {
            mListener.onListFragmentInteraction(holder.mItem);
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
//            mMembercountView = v.findViewById(R.id.club_member_count); TODO Count Dracula
            mLogo = v.findViewById(R.id.club_logo);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
