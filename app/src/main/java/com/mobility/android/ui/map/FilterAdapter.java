package com.mobility.android.ui.map;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mobility.android.R;
import com.mobility.android.data.model.Filter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private final List<Filter> mItems;
    private final Context mContext;

    public FilterAdapter(Context context, List<Filter> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Filter item = mItems.get(position);

        holder.checkBox.setButtonTintList(ColorStateList.valueOf(
                ContextCompat.getColor(mContext, R.color.accent)));

        holder.checkBox.setChecked(item.isChecked());
        holder.mLine.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_filter_checkbox) CheckBox checkBox;
        @BindView(R.id.list_item_filter_text) TextView mLine;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}