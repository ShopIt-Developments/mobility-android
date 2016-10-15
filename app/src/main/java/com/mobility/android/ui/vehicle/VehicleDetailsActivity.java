package com.mobility.android.ui.vehicle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.VehicleApi;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.util.UIUtils;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class VehicleDetailsActivity extends AppCompatActivity {

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.vehicle_details_book_car) CardView bookButton;

    @BindView(R.id.vehicle_details_name) TextView name;
    @BindView(R.id.vehicle_details_description) TextView description;
    @BindView(R.id.vehicle_details_price) TextView price;
    @BindView(R.id.vehicle_details_availability) TextView availability;
    @BindView(R.id.vehicle_details_location) TextView location;

    private VehicleObject vehicle;

    private NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ITALY);

    private boolean userIsOwner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle_details);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        vehicle = intent.getParcelableExtra(Config.EXTRA_VEHICLE);

        if (vehicle == null) {
            Timber.e("Missing intent extra %s", Config.EXTRA_VEHICLE);
            finish();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userIsOwner = vehicle.owner.equals(user.getUid());
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        collapsingToolbarLayout.setTitle(vehicle.name);

        if (userIsOwner) {
            bookButton.setOnClickListener(v -> bookCarConfirmation());
        }

        name.setText(vehicle.name);
        description.setText(vehicle.description);
        availability.setText(vehicle.availability);
        location.setText(vehicle.address);

        price.setText(format.format(vehicle.pricePerHour));
    }

    private void bookCarConfirmation() {
        price.setText(format.format(vehicle.pricePerHour));

        String message = "Do you really want to book this car for <b>" +
                format.format(vehicle.pricePerHour) + "</b> per hour?";

        new AlertDialog.Builder(this, R.style.DialogStyle)
                .setTitle("Book this car?")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    bookCar();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void bookCar() {
        ProgressDialog dialog = new ProgressDialog(this, R.style.DialogStyle);
        dialog.setMessage("Booking car...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        VehicleApi api = RestClient.ADAPTER.create(VehicleApi.class);
        api.order(vehicle.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        dialog.dismiss();

                        UIUtils.okDialog(VehicleDetailsActivity.this, "Couldn't book car", "An error happened while trying to book a car");
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Timber.e("Ordered vehicle: %s", vehicle.id);

                        dialog.dismiss();

                        UIUtils.okDialog(VehicleDetailsActivity.this, "Car booked", "Car booking was successful");
                    }
                });
    }
}
