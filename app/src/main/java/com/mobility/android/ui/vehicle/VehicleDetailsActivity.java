package com.mobility.android.ui.vehicle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.PaymentApi;
import com.mobility.android.data.network.api.VehicleApi;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.ui.payment.client.ClientPagerActivity;
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

    @BindView(R.id.vehicle_details_button_book) CardView bookButton;
    @BindView(R.id.vehicle_details_button_pay) CardView payButton;

    @BindView(R.id.vehicle_details_name) TextView name;
    @BindView(R.id.vehicle_details_description) TextView description;
    @BindView(R.id.vehicle_details_price) TextView price;
    @BindView(R.id.vehicle_details_availability) TextView availability;
    @BindView(R.id.vehicle_details_location) TextView location;
    @BindView(R.id.vehicle_details_licence_plate) TextView licencePlate;

    @BindView(R.id.activity_vehicle_details_licence) RelativeLayout licenceLayout;

    @BindView(R.id.activity_vehicle_details_divider) View divider;

    @BindView(R.id.backdrop) ImageView backdrop;
    @BindView(R.id.vehicle_details_name_icon) ImageView iconCarBike;

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

        toolbar.setNavigationOnClickListener(v -> finish());
        collapsingToolbarLayout.setTitle(vehicle.name);

        boolean userHasBorrowed;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userIsOwner = vehicle.owner.equals(user.getUid());
            userHasBorrowed = vehicle.borrower.equals(user.getUid());
        } else {
            Timber.e("User doesn't exist");
            finish();
            return;
        }

        bookButton.setOnClickListener(v -> bookCarConfirmation());
        payButton.setOnClickListener(v -> payCar());

        if (userIsOwner || userHasBorrowed) {
            bookButton.setVisibility(View.GONE);
        } else {
            bookButton.setVisibility(View.VISIBLE);
        }

        if (userHasBorrowed && !userIsOwner) {
            payButton.setVisibility(View.VISIBLE);
        } else {
            payButton.setVisibility(View.GONE);
        }

        if (vehicle.type.equalsIgnoreCase("bike")) {
            iconCarBike.setImageResource(R.drawable.ic_directions_bike_white_24dp);
            licenceLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        name.setText(vehicle.name);
        description.setText(vehicle.description);
        availability.setText(vehicle.availability);
        location.setText(vehicle.address);
        licencePlate.setText(vehicle.licencePlate);

        price.setText(format.format(vehicle.pricePerHour) + "/h");

        new Thread(() -> {
            byte[] image = Base64.decode(vehicle.image, Base64.DEFAULT);

            runOnUiThread(() -> Glide.with(this)
                    .load(image)
                    .into(backdrop));
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!userIsOwner) {
            return false;
        }

        getMenuInflater().inflate(R.menu.menu_vehicle_details, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_delete);

        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.color_control), PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteCarConfirmation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // ===================================== DELETE CAR ============================================

    private void deleteCarConfirmation() {
        new AlertDialog.Builder(this, R.style.DialogStyle)
                .setTitle("Delete this vehicle?")
                .setMessage("Deleting this vehicle means that it will no longer be bookable by other users. Do you really want to delete it?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteCar();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteCar() {
        ProgressDialog dialog = new ProgressDialog(this, R.style.DialogStyle);
        dialog.setMessage("Deleting vehicle...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        VehicleApi api = RestClient.ADAPTER.create(VehicleApi.class);
        api.deleteCar(vehicle.id)
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

                        UIUtils.okDialog(VehicleDetailsActivity.this, "Error", "Couldn't delete this vehicle");
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Timber.e("Deleted vehicle: %s", vehicle.id);
                        dialog.dismiss();
                        finish();
                    }
                });
    }


    // ====================================== BOOK CAR =============================================

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
        dialog.setMessage("Booking vehicle...");
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

                        bookButton.setVisibility(View.GONE);
                        payButton.setVisibility(View.VISIBLE);
                    }
                });
    }


    // ======================================= PAY CAR =============================================

    private void payCar() {
        ProgressDialog dialog = new ProgressDialog(this, R.style.DialogStyle);
        dialog.setMessage("Starting payment...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        PaymentApi paymentApi = RestClient.ADAPTER.create(PaymentApi.class);
        paymentApi.initiate(vehicle.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.cancel();

                        UIUtils.okDialog(VehicleDetailsActivity.this, "Error", "Couldn't start payment.");
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        dialog.cancel();

                        Intent intent = new Intent(VehicleDetailsActivity.this,
                                ClientPagerActivity.class);
                        intent.putExtra(Config.EXTRA_VEHICLE, vehicle);
                        startActivity(intent);
                    }
                });
    }
}
