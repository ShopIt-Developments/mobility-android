package com.mobility.android.ui.vehicle;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mobility.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVehicleActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.add_car_name_layout) TextInputLayout nameLayout;
    @BindView(R.id.add_car_name) EditText name;

    @BindView(R.id.radio_group_car_bicycle) RadioGroup radioGroup;
    @BindView(R.id.radio_button_car) RadioButton radioCar;
    @BindView(R.id.radio_button_bicycle) RadioButton radioBicycle;

    @BindView(R.id.add_car_availability_layout) TextInputLayout availabilityLayout;
    @BindView(R.id.add_car_availability) EditText availability;

    @BindView(R.id.add_car_licence_plate_layout) TextInputLayout licencePlateLayout;
    @BindView(R.id.add_car_licence_plate) EditText licencePlate;

    @BindView(R.id.add_car_description_layout) TextInputLayout descriptionLayout;
    @BindView(R.id.add_car_description) EditText description;

    @BindView(R.id.add_car_price_layout) TextInputLayout priceLayout;
    @BindView(R.id.add_car_price) EditText price;

    @BindView(R.id.add_vehicle_accept) CardView accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
