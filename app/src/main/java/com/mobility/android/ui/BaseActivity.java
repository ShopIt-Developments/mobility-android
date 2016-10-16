package com.mobility.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobility.android.R;
import com.mobility.android.data.network.RestClient;
import com.mobility.android.data.network.api.UserApi;
import com.mobility.android.data.network.model.User;
import com.mobility.android.ui.leaderboard.LeaderboardActivity;
import com.mobility.android.ui.login.LoginActivity;
import com.mobility.android.ui.profile.ProfileActivity;
import com.mobility.android.ui.vehicle.MyVehiclesActivity;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A base activity that handles common functionality in the app. This includes the
 * navigation drawer, handling of app upgrades, Action Bar tweaks, amongst others.
 * <p>
 * All activities which are present in the navigation drawer have to subclass this class to
 * show the navigation drawer with all drawer items.
 *
 * @author Alex Lardschneider
 */
public abstract class BaseActivity extends RxAppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener {

    /**
     * Navigation drawer
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Handler to help with fading the main content
     */
    private final Handler mHandler = new Handler();

    /**
     * Use this action if you want to fade the activity content, i.e. after you click
     * on a navigation drawer item.
     */
    private static final String ACTION_FADE_CONTENT = "com.davale.sasabus.ACTION_FADE_CONTENT";

    /**
     * The menu item ids for the navigation drawer.
     */
    protected static final int NAVDRAWER_ITEM_INVALID = -1;

    protected static final int NAVDRAWER_ITEM_MAP = R.id.nav_orders;
    protected static final int NAVDRAWER_ITEM_MY_VEHICLES = R.id.nav_my_vehicles;
    protected static final int NAVDRAWER_ITEM_PROFILE = R.id.nav_profile;
    protected static final int NAVDRAWER_ITEM_LEADERBOARD = R.id.nav_leaderboard;

    protected static final int NAVDRAWER_ITEM_SIGN_OUT = R.id.nav_signout;

    /**
     * Delay to launch nav drawer item, to allow close animation to play
     */
    private static final int NAVDRAWER_LAUNCH_DELAY = 300;

    /**
     * Fade in and fade out durations for the main content when switching between
     * different Activities of the app through the navigation drawer
     */
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 300;

    /**
     * Toolbar
     */
    private Toolbar mToolbar;

    /**
     * The main content, consisting of a {@link android.support.design.widget.CoordinatorLayout}
     * to allow {@link android.support.design.widget.Snackbar} animations to play.
     */
    private View mMainContent;

    protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        mAuth.addAuthStateListener(this);

        overridePendingTransition(0, 0);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        setUpToolbar();
        setUpNavDrawer();

        mMainContent = findViewById(R.id.main_content);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = getIntent();

        /*
         * Fade the main content if we transition from a navigation drawer activity
         */
        if (savedInstanceState == null && ACTION_FADE_CONTENT.equals(intent.getAction())) {
            if (mMainContent != null) {
                mMainContent.setAlpha(0);
                mMainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
            } else {
                Timber.e("No view with ID main_content to fade in.");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getNavItem() == NAVDRAWER_ITEM_INVALID) {
            return false;
        }

        int menuResource = R.menu.main;

        getMenuInflater().inflate(menuResource, menu);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mDrawerLayout.closeDrawers();

        onNavDrawerItemClicked(menuItem.getItemId());

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            navigationView.getMenu().findItem(getNavItem()).setChecked(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Timber.e("User logged out, redirecting to login activity");

            mAuth.removeAuthStateListener(this);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

            finish();
        }
    }


    /**
     * Override this method in Activities which have a entry in the navigation
     * drawer. The implemented method should return the menu id corresponding to its drawer
     * entry.
     *
     * @return the menu id of the current activity, or {@link #NAVDRAWER_ITEM_INVALID}
     * if the activity is not present in the navigation drawer
     */
    protected abstract int getNavItem();

    /**
     * Set up the Toolbar
     */
    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    /**
     * Set up the navigation drawer and the click listeners for the drawer.
     * Also hides the scrollbar from the navigation drawer and sets up the action bar
     * drawer toggle animation.
     */
    private void setUpNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (mDrawerLayout == null) {
            Timber.e("Couldn't find navigation drawer");
            return;
        }

        if (mToolbar == null) {
            Timber.e("Couldn't find toolbar");
            return;
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);

            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }

            Menu menu = navigationView.getMenu();

            menu.findItem(getNavItem()).setChecked(true);
            navigationView.setNavigationItemSelectedListener(this);

            CircleImageView userProfilePicture = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.partial_nav_header_profile_pic);
            TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.partial_nav_header_name);
            TextView points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.partial_nav_header_points);

            FirebaseUser user = mAuth.getCurrentUser();
            name.setText(user.getDisplayName());

            Glide.with(this).load(user.getPhotoUrl()).into(userProfilePicture);

            userProfilePicture.setOnClickListener((v) -> startActivity(new Intent(BaseActivity.this, ProfileActivity.class)));

            RestClient.ADAPTER.create(UserApi.class)
                    .getUser()
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<User>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(User userModel) {
                            points.setText(userModel.points + " points");
                        }
                    });
        }
    }

    /**
     * Handles the navigation from the navigation drawer.
     *
     * @param item the menu item id where the click was performed.
     */
    private void goToNavDrawerItem(int item) {
        switch (item) {
            case NAVDRAWER_ITEM_MAP:
                createBackStack(new Intent(this, MainActivity.class));
                break;
            case NAVDRAWER_ITEM_MY_VEHICLES:
                createBackStack(new Intent(this, MyVehiclesActivity.class));
                break;
            case NAVDRAWER_ITEM_PROFILE:
                createBackStack(new Intent(this, ProfileActivity.class));
                break;
            case NAVDRAWER_ITEM_LEADERBOARD:
                createBackStack(new Intent(this, LeaderboardActivity.class));
                break;
            case NAVDRAWER_ITEM_SIGN_OUT:
                mAuth.signOut();
                break;
            /*case NAVDRAWER_ITEM_SETTINGS:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_ABOUT:
                startActivity(new Intent(this, AboutActivity.class));
                break;*/
            default:
                throw new IllegalStateException("Unknown nav drawer item id " + item);
        }
    }

    /**
     * Executed when a navigation item is clicked.
     *
     * @param itemId the clicked on item id.
     */
    private void onNavDrawerItemClicked(int itemId) {
        if (itemId == getNavItem()) {
            closeNavDrawer();
            return;
        }

        // launch the target Activity after a short delay, to allow the close animation to play
        mHandler.postDelayed(() -> goToNavDrawerItem(itemId), NAVDRAWER_LAUNCH_DELAY);

        // fade out the main content
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        closeNavDrawer();
    }

    /**
     * Checks if the navigation drawer is currently open.
     *
     * @return {@code true} if open, {@code false} otherwise.
     */
    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * Close the navigation drawer
     */
    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     *
     * @param intent the intent of the activity
     */
    protected void createBackStack(Intent intent) {
        intent.setAction(ACTION_FADE_CONTENT);

        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * Returns the mToolbar currently in use
     *
     * @return {@link #mToolbar}
     */
    @Nullable
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Returns the main content layout in use,
     * mostly {@link android.support.design.widget.CoordinatorLayout}
     *
     * @return {@link #mMainContent}
     */
    @Nullable
    public View getMainContent() {
        return mMainContent;
    }

    /**
     * Returns the main content layout in use,
     * mostly {@link android.support.design.widget.CoordinatorLayout}
     *
     * @return {@link #mMainContent}
     */
    @Nullable
    public DrawerLayout getNavigationDrawer() {
        return mDrawerLayout;
    }

    public final <T> LifecycleTransformer<T> bindToActivity() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }
}
