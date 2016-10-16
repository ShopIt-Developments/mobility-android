package com.mobility.android.ui.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.TextView;

import com.mobility.android.Config;
import com.mobility.android.R;
import com.mobility.android.data.network.model.BusObject;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created on 16.10.2016.
 *
 * @author Martin
 */
public class BusDetailsActivity extends RxAppCompatActivity {

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.bus_details_line) TextView line;
    @BindView(R.id.bus_details_name) TextView name;
    @BindView(R.id.bus_details_location) TextView location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        BusObject mBus = intent.getParcelableExtra(Config.EXTRA_BUS);

        if (mBus == null) {
            Timber.e("Missing intent extra %s", Config.EXTRA_BUS);
            finish();
            return;
        }

        line.setText(mBus.name);
        location.setText(mBus.description);
        name.setText(getString(R.string.solaris_urbino_12));
    }
}
