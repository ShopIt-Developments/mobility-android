package com.mobility.android.ui.vehicle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.VehicleApi;
import com.mobility.android.data.network.model.VehicleObject;
import com.mobility.android.util.UIUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AddVehicleActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    public static final int ACTION_PICK_LOCATION = 100;

    private static final int CAMERA_REQUEST = 101;
    private static final int GALLERY_REQUEST = 102;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.add_vehicle_name) EditText name;
    @BindView(R.id.add_vehicle_name_layout) TextInputLayout nameLayout;

    @BindView(R.id.radio_group_car_bicycle) RadioGroup radioGroup;
    @BindView(R.id.radio_button_car) RadioButton radioCar;
    @BindView(R.id.radio_button_bicycle) RadioButton radioBicycle;

    @BindView(R.id.add_vehicle_availability) EditText availability;
    @BindView(R.id.add_vehicle_availability_layout) TextInputLayout availabilityLayout;

    @BindView(R.id.add_vehicle_licence_plate) EditText licencePlate;
    @BindView(R.id.add_vehicle_licence_plate_layout) TextInputLayout licencePlateLayout;

    @BindView(R.id.add_vehicle_description) EditText description;
    @BindView(R.id.add_vehicle_description_layout) TextInputLayout descriptionLayout;

    @BindView(R.id.add_vehicle_price) EditText price;
    @BindView(R.id.add_vehicle_price_layout) TextInputLayout priceLayout;

    @BindView(R.id.add_vehicle_button_add) CardView accept;

    @BindView(R.id.add_vehicle_location) TextView location;

    @BindView(R.id.add_vehicle_add_image) FrameLayout addImage;
    @BindView(R.id.backdrop) ImageView imageBackground;

    private File imageFile;

    private VehicleObject vehicle = new VehicleObject();

    private Place place;

    private boolean isCar = true;

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
        addImage.setOnClickListener(this);
        location.setOnClickListener(this);

        nameLayout.setError("error");
        nameLayout.setError(null);

        availabilityLayout.setError("error");
        availabilityLayout.setError(null);

        licencePlateLayout.setError("error");
        licencePlateLayout.setError(null);

        descriptionLayout.setError("error");
        descriptionLayout.setError(null);

        priceLayout.setError("error");
        priceLayout.setError(null);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_button_car:
                if (licencePlateLayout.getVisibility() == View.GONE) {
                    licencePlateLayout.setVisibility(View.VISIBLE);
                }

                isCar = true;
                break;
            case R.id.radio_button_bicycle:
                if (licencePlateLayout.getVisibility() == View.VISIBLE) {
                    licencePlateLayout.setVisibility(View.GONE);
                }
                licencePlate.setText("");
                isCar = false;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_vehicle_add_image:
                showPictureDialog();
                break;
            case R.id.add_vehicle_location:
                showPlacesPopup();
                break;
            case R.id.add_vehicle_button_add:
                if (isInputValid()) {
                    uploadVehicle();
                }

                break;
        }
    }

    private boolean isInputValid() {
        boolean error = false;

        if (name.getText().toString().isEmpty()) {
            nameLayout.setError("You need to add a name");
            error = true;
        } else {
            nameLayout.setError(null);
        }

        if (availability.getText().toString().isEmpty()) {
            availabilityLayout.setError("You need to add an availability date");
            error = true;
        } else {
            availabilityLayout.setError(null);
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.radio_button_car
                && licencePlate.getText().toString().isEmpty()) {
            licencePlateLayout.setError("You need to add a license plate");
            error = true;
        } else {
            licencePlateLayout.setError(null);
        }

        if (description.getText().toString().isEmpty()) {
            descriptionLayout.setError("You need to add a description");
            error = true;
        } else {
            descriptionLayout.setError(null);
        }

        if (price.getText().toString().isEmpty()) {
            priceLayout.setError("You need to enter a price");
            error = true;
        } else {
            int enteredPrice = Integer.parseInt(price.getText().toString());
            if (enteredPrice < 1) {
                priceLayout.setError("The car should cost at least 1€");
                error = true;
            } else if (enteredPrice > 100) {
                priceLayout.setError("The car should cost at most 100€");
                error = true;
            } else {
                priceLayout.setError(null);
            }
        }

        if (TextUtils.isEmpty(vehicle.image)) {
            UIUtils.okDialog(this, "Missing image", "You need to provide an image of your vehicle.");
            error = true;
        }

        if (place == null) {
            UIUtils.okDialog(this, "Missing location",
                    "You need to provide a location where the car is located.");
            error = true;
        }

        return !error;
    }

    private void uploadVehicle() {
        ProgressDialog dialog = new ProgressDialog(this, R.style.DialogStyle);
        dialog.setMessage("Adding vehicle...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        vehicle.lat = (float) place.getLatLng().latitude;
        vehicle.lng = (float) place.getLatLng().longitude;
        vehicle.address = place.getAddress().toString();
        vehicle.country = "IT";

        vehicle.name = name.getText().toString();
        vehicle.availability = availability.getText().toString();
        vehicle.licencePlate = isCar ? licencePlate.getText().toString() : null;
        vehicle.description = description.getText().toString();
        vehicle.pricePerHour = Float.parseFloat(price.getText().toString());
        vehicle.currency = "EUR";

        vehicle.type = isCar ? "car" : "bike";

        Timber.e("Uploading vehicle: %s", vehicle);

        VehicleApi api = RestClient.ADAPTER.create(VehicleApi.class);
        api.addVehicle(vehicle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VehicleObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        UIUtils.okDialog(AddVehicleActivity.this, "Error", "Couldn't add vehicle.");
                    }

                    @Override
                    public void onNext(VehicleObject object) {
                        Timber.w("Added vehicle");
                        dialog.dismiss();

                        Intent intent = new Intent(AddVehicleActivity.this, VehicleDetailsActivity.class);
                        intent.putExtra(Config.EXTRA_VEHICLE, object);
                        startActivity(intent);
                        finish();
                    }
                });
    }


    // ================================== PLACE PICKER =============================================

    private void showPlacesPopup() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder()
                    .setLatLngBounds(new LatLngBounds(
                            new LatLng(46.3711, 11.0510),
                            new LatLng(46.7257, 11.4240)));

            startActivityForResult(builder.build(this), ACTION_PICK_LOCATION);
        } catch (Exception e) {
            GoogleApiAvailability api = GoogleApiAvailability.getInstance();
            int googleStatus = api.isGooglePlayServicesAvailable(this);

            if (googleStatus != ConnectionResult.SUCCESS) {
                api.showErrorDialogFragment(this, googleStatus, 100);
            }

            e.printStackTrace();
        }
    }


    // ================================= PROFILE PICTURE ===========================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_PICK_LOCATION) {
                place = PlacePicker.getPlace(this, data);

                location.setText(place.getName());
                location.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            } else if (requestCode == CAMERA_REQUEST) {
                Timber.w("Got picture from camera: %s", imageFile.getAbsolutePath());
                uploadPicture(imageFile);
            } else if (requestCode == GALLERY_REQUEST) {
                Timber.w("Got picture from gallery: %s", data.getData().toString());
                uploadPicture(getFileFromUri(data.getData()));
            }
        }
    }

    private void showPictureDialog() {
        CharSequence[] options = {
                "Take picture",
                "Pick from gallery"
        };

        new AlertDialog.Builder(this, R.style.DialogStyle)
                .setItems(options, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            takePicture();
                            break;
                        case 1:
                            pickFromGallery();
                            break;
                    }
                })
                .create()
                .show();
    }

    private void takePicture() {
        imageFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void uploadPicture(File file) {
        ProgressDialog dialog = new ProgressDialog(this, R.style.DialogStyle);
        dialog.setMessage("Loading image...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        Glide.with(AddVehicleActivity.this)
                .load(file)
                .animate(android.R.anim.fade_in)
                .into(imageBackground);

        base64(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();

                        UIUtils.okDialog(AddVehicleActivity.this, "Error", "Couldn't load image.");
                    }

                    @Override
                    public void onNext(String s) {
                        Timber.w("Loaded image base64, size: %s KB", s.length() / 1024);
                        vehicle.image = s;

                        dialog.dismiss();
                    }
                });
    }

    private void pickFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    private File getFileFromUri(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return new File(result);
    }

    private Observable<String> base64(File file) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    FileInputStream in = new FileInputStream(file);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    Bitmap bitmap = BitmapFactory.decodeStream(in);

                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 960, 720, false);
                    bitmap.recycle();

                    scaled.compress(Bitmap.CompressFormat.JPEG, 60, out);

                    subscriber.onNext(Base64.encodeToString(out.toByteArray(), Base64.DEFAULT));
                    subscriber.onCompleted();
                } catch (FileNotFoundException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
