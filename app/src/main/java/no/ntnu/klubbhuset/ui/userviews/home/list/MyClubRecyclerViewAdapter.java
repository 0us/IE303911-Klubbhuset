package no.ntnu.klubbhuset.ui.userviews.home.list;

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
import no.ntnu.klubbhuset.ui.userviews.club.ClubsViewModel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Club} and makes a call to the
 * specified {@link ClubFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyClubRecyclerViewAdapter extends RecyclerView.Adapter<MyClubRecyclerViewAdapter.ViewHolder> {

    private final List<Club> mValues;
    private final ClubFragment.OnListFragmentInteractionListener mListener;
    private ClubsViewModel model;

    public MyClubRecyclerViewAdapter(List<Club> items,
                                     ClubFragment.OnListFragmentInteractionListener listener) {
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
        holder.mMembercountView.setText("0"); // TODO

        holder.view.setOnClickListener(v -> {
            model.setSelectedClub(holder.mItem);
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
