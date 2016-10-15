package com.mobility.android.ui.myvehicles;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobility.android.R;
import com.mobility.android.data.network.model.VehicleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 15.10.2016.
 *
 * @author Martin
 */

public class MyVehiclesAdapter extends RecyclerView.Adapter<MyVehiclesAdapter.MyVehicleViewHolder> {

    private List<VehicleObject> items;

    public MyVehiclesAdapter() {
        items = new ArrayList<>();
    }

    public void setItems(@NonNull List<VehicleObject> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public MyVehicleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);

        return new MyVehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyVehicleViewHolder holder, int position) {
        VehicleObject item = items.get(position);

        holder.name.setText(item.name);
        holder.description.setText(item.description);
        holder.price.setText(String.format(Locale.getDefault(), "%.2f %s", item.pricePerHour, item.currency));
        holder.licencePlate.setText(item.licencePlate);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyVehicleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_car_manufacturer_model) TextView name;
        @BindView(R.id.item_car_description) TextView description;
        @BindView(R.id.item_car_price) TextView price;
        @BindView(R.id.item_car_licence_plate) TextView licencePlate;

        MyVehicleViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
