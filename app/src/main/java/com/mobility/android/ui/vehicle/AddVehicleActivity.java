package com.mobility.android.ui.vehicle;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mobility.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVehicleActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

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
        toolbar.setNavigationOnClickListener((v) -> finish());

        radioGroup.setOnCheckedChangeListener(this);

        accept.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_button_car:
                if (licencePlateLayout.getVisibility() == View.GONE) {
                    licencePlateLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radio_button_bicycle:
                if (licencePlateLayout.getVisibility() == View.VISIBLE) {
                    licencePlateLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_vehicle_accept:
                if (!validateInput()) {
                    return;
                }

                //TODO add car
                break;
        }
    }

    private boolean validateInput() {
        boolean inputIsValid = true;
        if (name.getText().toString().isEmpty()) {
            inputIsValid = false;
            nameLayout.setError(getString(R.string.error_field_empty));
        } else {
            nameLayout.setError(null);
        }

        if (availability.getText().toString().isEmpty()) {
            inputIsValid = false;
            availabilityLayout.setError(getString(R.string.error_field_empty));
        } else {
            availabilityLayout.setError(null);
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.radio_button_car
                && licencePlate.getText().toString().isEmpty()) {
            licencePlateLayout.setError(getString(R.string.error_field_empty));
        } else {
            licencePlateLayout.setError(null);
        }

        if (description.getText().toString().isEmpty()) {
            inputIsValid = false;
            descriptionLayout.setError(getString(R.string.error_field_empty));
        } else {
            descriptionLayout.setError(null);
        }

        if (price.getText().toString().isEmpty()) {
            inputIsValid = false;
            priceLayout.setError(getString(R.string.error_field_empty));
        } else {
            int enteredPrice = Integer.parseInt(price.getText().toString());
            if (enteredPrice <= 0 || enteredPrice > 150) {
                inputIsValid = false;
                priceLayout.setError(getString(R.string.error_price_invalid));
            } else {
                priceLayout.setError(null);
            }
        }

        return inputIsValid;
    }
}
