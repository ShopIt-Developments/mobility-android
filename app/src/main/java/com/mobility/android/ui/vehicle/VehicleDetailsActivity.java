package com.mobility.android.ui.vehicle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;

import com.mobility.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleDetailsActivity extends AppCompatActivity {

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.vehicle_details_book_car) CardView bookButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle_details);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());
        collapsingToolbarLayout.setTitle("Tesla Model S");

        bookButton.setOnClickListener(v -> bookCar());
    }

    private void bookCar() {
        String message = "Do you really want to book this car for <b>5.00$</b> per hour?";

        new AlertDialog.Builder(this, R.style.DialogStyle)
                .setTitle("Book this car?")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .create()
                .show();
    }
}
