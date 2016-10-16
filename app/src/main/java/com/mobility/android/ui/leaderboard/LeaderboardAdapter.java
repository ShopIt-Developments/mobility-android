package com.mobility.android.ui.leaderboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mobility.android.R;
import com.mobility.android.data.network.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 16.10.2016.
 *
 * @author Martin
 */

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<User> items;

    private boolean sortByStars;

    public LeaderboardAdapter() {
        sortByStars = false;
        items = new ArrayList<>();
    }

    public void sort() {
        Collections.sort(items, (o1, o2) -> {
            int ret;
            if (sortByStars) {
                ret = (int) (10 * o2.averageRating - 10 * o1.averageRating);
            } else {
                ret =  o2.points - o1.points;
            }
            return ret;
        });
        sortByStars = !sortByStars;
        notifyDataSetChanged();
    }

    public void setItems(List<User> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
            sort();
        }
        notifyDataSetChanged();
    }

    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {
        User user = items.get(position);

        holder.rank.setText(String.valueOf(position + 1));
        holder.name.setText(user.name);
        holder.points.setText(String.format(Locale.getDefault(), "%d Points", user.points));
        holder.stars.setRating(user.averageRating);
        holder.numStars.setText(String.format(Locale.getDefault(), "(%d)", user.ratingsCount));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_user_rank) TextView rank;
        @BindView(R.id.item_user_name) TextView name;
        @BindView(R.id.item_user_points) TextView points;
        @BindView(R.id.item_user_stars) RatingBar stars;
        @BindView(R.id.item_user_num_rating) TextView numStars;

        LeaderboardViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
