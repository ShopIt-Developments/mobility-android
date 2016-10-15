package com.mobility.android.ui.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.mobility.android.R;
import com.mobility.android.ui.BaseActivity;

import timber.log.Timber;

public class MapActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        Button button = (Button) findViewById(R.id.logout);
        button.setOnClickListener(v -> {
            Timber.w("Logging out user");
            FirebaseAuth.getInstance().signOut();
        });
    }

    @Override
    protected int getNavItem() {
        return NAVDRAWER_ITEM_MAP;
    }
}
