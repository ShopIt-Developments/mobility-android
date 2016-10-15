package com.mobility.android.ui.vehicle;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.mobility.android.R;
import com.mobility.android.data.network.model.VehicleObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AddVehicleActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final int CAMERA_REQUEST = 101;
    private static final int GALLERY_REQUEST = 102;

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

    private VehicleObject mVehicle;

    @BindView(R.id.add_vehicle_add_image) FrameLayout addImage;
    @BindView(R.id.backdrop) ImageView imageBackground;

    private File imageFile;

    private VehicleObject object = new VehicleObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vehicle);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((v) -> finish());

        //Fixing measure bug so the views don't expand in size when a error is displayed
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


        radioGroup.setOnCheckedChangeListener(this);

        accept.setOnClickListener(this);
        addImage.setOnClickListener(this);
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
            case R.id.add_vehicle_add_image:
                showPictureDialog();
                break;
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


    // ================================= PROFILE PICTURE ===========================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Timber.w("Got camera result intent");

            if (requestCode == CAMERA_REQUEST) {
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
        Glide.with(this)
                .load(file)
                .animate(android.R.anim.fade_in)
                .into(imageBackground);

        object.image = getBase64Image(file);
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

    private String getBase64Image(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
