package com.cermati.imams.githubsearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> userArrayList;
    private ItemClickListener mClickListener;

    public UserRecyclerViewAdapter(Context context, ArrayList<User> userArrayList, ItemClickListener mClickListener) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.mClickListener = mClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.tvUsername.setText(user.getUsername());
        Picasso.with(context)
                .load(user.getAvatarUrl())
                .centerCrop()
                .fit()
                .into(holder.ivAvatar);
        holder.layout.setOnClickListener(item -> {
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra("user", user);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (userArrayList == null) {
            return 0;
        } else {
            return userArrayList.size();
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, ImageView avatar);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.layout)
        RelativeLayout layout;
        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_username)
        TextView tvUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClickListener(getAdapterPosition(), ivAvatar);
            }
        }
    }
}
