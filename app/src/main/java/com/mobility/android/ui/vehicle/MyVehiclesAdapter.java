package com.mobility.android.ui.vehicle;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.model.VehicleObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyVehiclesAdapter extends RecyclerView.Adapter<MyVehiclesAdapter.MyVehicleViewHolder> {

    private List<VehicleObject> mItems;
    private Context mContext;

    MyVehiclesAdapter(Context context) {
        mContext = context;

        mItems = new ArrayList<>();
    }

    void setItems(@NonNull List<VehicleObject> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public MyVehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);

        return new MyVehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyVehicleViewHolder holder, int position) {
        VehicleObject item = mItems.get(position);

        holder.name.setText(item.name);
        holder.description.setText(item.description);
        holder.price.setText((int) item.pricePerHour + "€/h");
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyVehicleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_vehicle_card) CardView cardView;
        @BindView(R.id.item_vehicle_manufacturer_model) TextView name;
        @BindView(R.id.item_vehicle_description) TextView description;
        @BindView(R.id.item_vehicle_price) TextView price;

        MyVehicleViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            VehicleObject object = mItems.get(position);

            Intent intent = new Intent(mContext, VehicleDetailsActivity.class);
            intent.putExtra(Config.EXTRA_VEHICLE, object);
            mContext.startActivity(intent);
        }
    }
}
