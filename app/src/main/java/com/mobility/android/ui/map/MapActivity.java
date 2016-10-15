package com.mobility.android.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.Endpoint;
import com.mobility.android.data.network.NetUtils;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.VehiclesApi;
import com.mobility.android.data.network.model.BusObject;
import com.mobility.android.data.network.model.MapObject;
import com.mobility.android.data.network.response.MapResponse;
import com.mobility.android.ui.BaseActivity;
import com.mobility.android.ui.widget.BottomSheet;
import com.mobility.android.ui.widget.NestedSwipeRefreshLayout;
import com.mobility.android.util.DeviceUtils;
import com.mobility.android.util.MapUtils;
import com.mobility.android.util.Utils;
import com.trello.rxlifecycle.internal.Preconditions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MapActivity extends BaseActivity implements OnMapReadyCallback,
        View.OnClickListener, GoogleMap.OnMarkerClickListener, Observer<MapResponse>,
        GoogleMap.OnInfoWindowCloseListener {

    private Map<Integer, Marker> mMarkers = new HashMap<>();
    private ArrayList<MapObject> mBusData;

    @BindView(R.id.refresh) NestedSwipeRefreshLayout mRefresh;

    private GoogleMap mGoogleMap;

    private Snackbar mErrorSnackbar;
    private Snackbar mInternetSnackbar;

    private boolean mIsRefreshing;

    private int mGplayStatus;

    private final Handler HANDLER = new Handler();

    private final Runnable REFRESH_RUNNABLE = this::parseData;
    private final Runnable SHEET_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            selectedItem = null;
        }
    };

    @BindView(R.id.bottom_sheet) FrameLayout bottomSheet;
    @BindView(R.id.bottom_sheet_peek_title) TextView bottomSheetTitle;
    @BindView(R.id.bottom_sheet_peek_sub) TextView bottomSheetSub;
    @BindView(R.id.bottom_sheet_peek_delay) TextView bottomSheetDelayPeek;

    private BottomSheetBehavior<FrameLayout> behavior;

    private boolean isBottomSheetOpen;

    private MapObject selectedItem;

    private final TileProvider tileProvider = new UrlTileProvider(512, 512) {

        @Override
        public URL getTileUrl(int x, int y, int zoom) {
            if (!(selectedItem instanceof BusObject)) {
                return null;
            }

            if (!checkTileExists(zoom)) {
                return null;
            }

            String urlFormatted;

            BusObject lineObject = (BusObject) selectedItem;

            String url = Endpoint.API_DATA + Endpoint.MAP_TILES;
            urlFormatted = String.format(Locale.ROOT, url, x, y, zoom,
                    lineObject.lineId, lineObject.variant);

            Timber.d(urlFormatted);

            try {
                return new URL(urlFormatted);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
        }

        private boolean checkTileExists(int zoom) {
            int minZoom = 11;
            int maxZoom = 16;

            //noinspection SimplifiableIfStatement
            if (zoom < minZoom || zoom > maxZoom) {
                return false;
            }

            return selectedItem != null;
        }
    };

    private TileOverlay tileOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        int state = BottomSheetBehavior.STATE_HIDDEN;
        mBusData = new ArrayList<>();

        setupBottomSheet(state);

        mRefresh.setColorSchemeResources(Config.REFRESH_COLORS);

        mGplayStatus = MapsInitializer.initialize(getApplicationContext());
        if (mGplayStatus == 0) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        } else {
            Timber.e("Couldn't initialize google map: error=%s", mGplayStatus);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGoogleMap != null) {
            mGoogleMap.clear();

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(false);
            }
        }

        mMarkers.clear();
        HANDLER.removeCallbacks(REFRESH_RUNNABLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        HANDLER.removeCallbacks(REFRESH_RUNNABLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (mGoogleMap != null) {
                    parseData();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isBottomSheetOpen) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public int getNavItem() {
        return NAVDRAWER_ITEM_MAP;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO: 15/10/16 Add filter
        /*if (requestCode == REQUEST_FILTER && resultCode == Activity.RESULT_OK) {
            mFilterEnabled = data.getBooleanExtra(FilterActivity.EXTRA_FILTER_ENABLED, true);
            mFilter = data.getIntegerArrayListExtra(FilterActivity.EXTRA_FILTER_LIST);

            updateFilterMarkers();
        }*/
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        map.moveCamera(MapUtils.getDefaultPosition(this));

        if (DeviceUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //noinspection MissingPermission
            map.setMyLocationEnabled(true);
        }

        map.setMapType(MapUtils.getType(this));
        map.setMapStyle(MapUtils.getStyle(this));

        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowCloseListener(this);

        parseData();

        if (MapUtils.areOverlaysEnabled(this)) {
            tileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(tileProvider));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Projection projection = mGoogleMap.getProjection();
        Point markerPoint = projection.toScreenLocation(marker.getPosition());

        markerPoint.offset(0, 0);
        LatLng newLatLng = projection.fromScreenLocation(markerPoint);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng), 350, null);

        marker.showInfoWindow();

        HANDLER.removeCallbacks(SHEET_RUNNABLE);

        updateBottomSheet((MapObject) marker.getTag());

        if (behavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        return true;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        HANDLER.postDelayed(SHEET_RUNNABLE, 200);

        selectedItem = null;

        if (tileOverlay != null) {
            tileOverlay.clearTileCache();
        }
    }

    @Override
    public void onNext(MapResponse realtimeResponse) {
        Preconditions.checkNotNull(mGoogleMap, "googleMap == null");
        Preconditions.checkNotNull(mBusData, "mBusData == null");
        Preconditions.checkNotNull(mRefresh, "mRefresh == null");
        Preconditions.checkNotNull(mMarkers, "mMarkers == null");

        mBusData.clear();

        if (mGplayStatus != 0) {
            mIsRefreshing = false;
            return;
        }

        mBusData.clear();
        mBusData.addAll(realtimeResponse.buses);

        hideSnackbar();

        Map<Integer, Marker> updatedMarkers = new HashMap<>();

        for (MapObject mapObject : realtimeResponse.buses) {
            BusObject bus = (BusObject) mapObject;

            Marker marker = mMarkers.get(bus.trip);

            if (marker == null) {
                marker = mGoogleMap.addMarker(new MarkerOptions()
                        .title(bus.name)
                        .position(new LatLng(bus.lat, bus.lng))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        /*.visible(!mFilterEnabled || mFilter.contains(bus.lineId))*/);

                marker.setTag(bus);
            } else {
                animateMarker(marker, new LatLng(bus.lat, bus.lng));

                marker.setTitle(bus.name);
                marker.setTag(bus);
            }

            updatedMarkers.put(bus.trip, marker);
            mMarkers.remove(bus.trip);
        }

        for (MapObject mapObject : realtimeResponse.buses) {
            BusObject bus = (BusObject) mapObject;

            Marker marker = mMarkers.get(bus.trip);

            if (marker == null) {
                marker = mGoogleMap.addMarker(new MarkerOptions()
                                .title(bus.name)
                                .position(new LatLng(bus.lat, bus.lng))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        /*.visible(!mFilterEnabled || mFilter.contains(bus.lineId))*/);

                marker.setTag(bus);
            } else {
                animateMarker(marker, new LatLng(bus.lat, bus.lng));

                marker.setTitle(bus.name);
                marker.setTag(bus);
            }

            updatedMarkers.put(bus.trip, marker);
            mMarkers.remove(bus.trip);
        }

        for (Marker marker : mMarkers.values()) {
            marker.remove();
        }

        mMarkers = updatedMarkers;

        for (Marker marker : mMarkers.values()) {
            if (marker != null && marker.isInfoWindowShown()) {
                updateBottomSheet((MapObject) marker.getTag());
            }
        }

        mIsRefreshing = false;

        mRefresh.setRefreshing(false);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Utils.logException(e);

        mIsRefreshing = false;
        showErrorSnackbar(R.string.error_general);

        mRefresh.setRefreshing(false);
    }


    private void parseData() {
        if (mIsRefreshing) {
            Timber.w("Already loading data");
            return;
        }

        if (!NetUtils.isOnline(this)) {
            showInternetSnackbar();
        } else {
            mIsRefreshing = true;
            mRefresh.setRefreshing(true);

            VehiclesApi api = RestClient.ADAPTER.create(VehiclesApi.class);
            api.getAvailableVehicles()
                    .compose(bindToActivity())
                    .subscribeOn(Schedulers.io())
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    private void animateMarker(Marker marker, LatLng endPosition) {
        Projection projection = mGoogleMap.getProjection();
        LatLng startPosition = projection.fromScreenLocation(projection.toScreenLocation(marker.getPosition()));

        Handler handler = new Handler();
        long start = SystemClock.uptimeMillis();
        long duration = 200;

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (endPosition == null || startPosition == null) {
                    return;
                }

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = (float) elapsed / duration;
                double lng = t * endPosition.longitude + (1 - t) * startPosition.longitude;
                double lat = t * endPosition.latitude + (1 - t) * startPosition.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    private void showErrorSnackbar(@StringRes int message) {
        if (mErrorSnackbar != null && mErrorSnackbar.isShown()) return;

        mErrorSnackbar = Snackbar.make(getMainContent(), message, Snackbar.LENGTH_INDEFINITE);

        View snackbarView = mErrorSnackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));

        runOnUiThread(() -> {
            if (mInternetSnackbar != null) {
                mInternetSnackbar.dismiss();
            }
            mErrorSnackbar.show();
        });
    }

    private void showInternetSnackbar() {
        if (mInternetSnackbar != null && mInternetSnackbar.isShown()) return;

        mInternetSnackbar = Snackbar.make(getMainContent(), R.string.error_wifi, Snackbar.LENGTH_INDEFINITE);

        View snackbarView = mInternetSnackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));

        runOnUiThread(() -> {
            if (mErrorSnackbar != null) {
                mErrorSnackbar.dismiss();
            }
            mInternetSnackbar.show();
        });
    }

    private void hideSnackbar() {
        if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }

        if (mInternetSnackbar != null) {
            mInternetSnackbar.dismiss();
        }
    }


    private void setupBottomSheet(@BottomSheetBehavior.State int state) {
        behavior = BottomSheet.from(bottomSheet, state);

        bottomSheet.setOnClickListener(v -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                isBottomSheetOpen = newState == BottomSheetBehavior.STATE_EXPANDED;
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        behavior.setPeekHeight((int) getResources().getDimension(R.dimen.peek_height));
    }

    private void updateBottomSheet(MapObject bus) {
        selectedItem = bus;
        bottomSheetTitle.setText("Line " + bus.name);
        bottomSheetSub.setText(bus.description);
    }
}
