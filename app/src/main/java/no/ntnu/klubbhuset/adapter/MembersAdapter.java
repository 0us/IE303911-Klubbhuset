package no.ntnu.klubbhuset.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import no.ntnu.klubbhuset.R;
import no.ntnu.klubbhuset.data.model.Member;
import no.ntnu.klubbhuset.data.model.User;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    List<User> members;



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberName;
        public ImageView hasPaid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.memberName = itemView.findViewById(R.id.member_name);
            this.hasPaid = itemView.findViewById(R.id.has_paid);
        }
    }

    public MembersAdapter(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_members, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User member = members.get(position);

        TextView memberNameTextView = holder.memberName;
        ImageView hasPaid = holder.hasPaid;

        memberNameTextView.setText(member.getFirstName() + " " + member.getLastName());
        
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
