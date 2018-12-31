package szekelyistvan.com.colorpalette.ui;

/*
 * Copyright (C) 2018 Szekely Istvan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.dialogs.DeleteDialog;
import szekelyistvan.com.colorpalette.dialogs.ExitAppDialog;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.loaders.PaletteLoader;
import szekelyistvan.com.colorpalette.service.PaletteIntentService;
import szekelyistvan.com.colorpalette.service.PaletteResultReceiver;
import szekelyistvan.com.colorpalette.utils.PaletteAdapter;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static szekelyistvan.com.colorpalette.network.CheckInternet.isNetworkConnection;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.service.PaletteIntentService.STATUS_ERROR;
import static szekelyistvan.com.colorpalette.service.PaletteIntentService.STATUS_FINISHED;
import static szekelyistvan.com.colorpalette.service.PaletteIntentService.STATUS_STARTED;
import static szekelyistvan.com.colorpalette.ui.DetailActivity.clearPosition;
import static szekelyistvan.com.colorpalette.utils.DatabaseUtils.cursorToArrayList;
import static szekelyistvan.com.colorpalette.utils.LoaderUtil.makeBundle;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.SERVICE_DOWNLOAD_FINISHED;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.readBoolean;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.readString;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.writeBoolean;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.writeString;

/**
 * Displays a RecyclerView with three different adapter data.
 */

public class MainActivity extends AppCompatActivity implements PaletteResultReceiver.Receiver,
        DeleteDialog.DeleteDialogListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String BASE_URL ="http://www.colourlovers.com/api/palettes/";
    public static final String PALETTE_INDEX = "palette_index";
    public static final String PALETTE_ARRAY = "palette_array";
    public static final String TAG = "ColorPalette";
    public static final String RECEIVER = "receiver";
    public static final String DEFAULT_SHARED_PREFERENCES = "PalettePreferences";
    public static final String APP_HAS_RUN_BEFORE = "app_has_run_before";
    public static final String TOP_STATE = "top_state";
    public static final String NEW_STATE = "new_state";
    public static final String FAVORITE_STATE = "favorite_state";
    public static final String TOP_BUTTON_CLICKED = "top_button_clicked";
    public static final String NEW_BUTTON_CLICKED = "new_button_clicked";
    public static final String FAVORITE_BUTTON_CLICKED = "favorite_button_clicked";
    public static final String LAST_BUTTON_CLICKED = "last_button_clicked";
    public static final String ADAPTER_DATA = "adapter_data";
    public static final String EXIT_APP_DIALOG = "exit_app_dialog";
    public static final String DELETE_DIALOG = "delete_dialog";
    public static final String SERVICE_STATUS = "service_status";
    public static final String AD_TEST_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String ANOTHER_FORMAT_AD_TEST_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String SERVICE_STARTED = "started";
    public static final String SERVICE_FINISHED = "finished";
    public static final String SERVICE_ERROR = "error";
    public static final String SERVICE_NEVER_RUN = "never_run";
    public static final int MAIN_LOADER_ID = 11;

    List<Palette> palettes;
    @BindView(R.id.palette_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.downloadProgress)
    ProgressBar progressBar;
    @BindView(R.id.main_activity_toolbar)
    Toolbar mainActivityToolbar;
    @BindView(R.id.main_layout)
    CoordinatorLayout mainLayout;
    private PaletteAdapter paletteAdapter;
    private boolean isListDeleteInitialized;

    @Retention(SOURCE)
    @StringDef({ TOP, NEW})
    public @interface InternetClient {}
    public static final String TOP = "top";
    public static final String NEW = "new";
    private boolean isTopButtonClicked;
    private boolean isNewButtonClicked;
    private boolean isFavoriteButtonClicked;
    private String lastButtonClicked;
    private InterstitialAd interstitialAd;
    private int backPressCounter;
    private Parcelable topState;
    private Parcelable newState;
    private Parcelable favoriteState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mainActivityToolbar);
        Fabric.with(this, new Crashlytics());
        clearPosition(this);

        if (isNetworkConnection(this)){
            initializeMobileAd();
        }

        checkProgressBarStatus();

        setupRecyclerView();

        setupBottomNavigation();

        loadLists(savedInstanceState);
    }

    /**
     * Loads the data for the app, from the Internet or from the ContentProvider.
     * @param bundle the argument is the savedInstanceChange, restores data after configuration change
     */
    private void loadLists(Bundle bundle){
        if (bundle == null) {
            if (readBoolean(this, APP_HAS_RUN_BEFORE, false) &&
                    readBoolean(this, SERVICE_DOWNLOAD_FINISHED, false)) {
                bottomNavigationView.setSelectedItemId(R.id.palette_top);
            } else {
                startService();
            }
        } else {
            if (!readBoolean(this, SERVICE_DOWNLOAD_FINISHED, false)){
                startService();
            } else {
                restoreListsState(bundle);
                paletteAdapter.changePaletteData(palettes);
            }
        }
    }

    /**
     * Sets up the BottomNavigationView.
     */
    private void setupBottomNavigation(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.palette_top:
                            saveListState();
                            getSupportLoaderManager().restartLoader(MAIN_LOADER_ID, makeBundle(CONTENT_URI_TOP, null, null), MainActivity.this);
                            isTopButtonClicked = true;
                            isNewButtonClicked = false;
                            isFavoriteButtonClicked = false;
                            break;
                        case R.id.palette_new:
                            saveListState();
                            getSupportLoaderManager().restartLoader(MAIN_LOADER_ID, makeBundle(CONTENT_URI_NEW, null, null), MainActivity.this);
                            isTopButtonClicked = false;
                            isNewButtonClicked = true;
                            isFavoriteButtonClicked = false;
                            break;
                        case R.id.palette_favorite:
                            saveListState();
                            getSupportLoaderManager().restartLoader(MAIN_LOADER_ID, makeBundle(CONTENT_URI_FAVORITE, null, null), MainActivity.this);
                            if (isTopButtonClicked) {
                                lastButtonClicked = TOP;
                            }
                            if (isNewButtonClicked) {
                                lastButtonClicked = NEW;
                            }
                            isTopButtonClicked = false;
                            isNewButtonClicked = false;
                            isFavoriteButtonClicked = true;
                            break;
                    }
                return true;
            }
        });
    }

    /**
     * Saves the state of the RecyclerView before configuration change.
     */
    private void saveListState(){
        if (isTopButtonClicked){
            topState = recyclerView.getLayoutManager().onSaveInstanceState();
        }
        if (isNewButtonClicked){
            newState = recyclerView.getLayoutManager().onSaveInstanceState();
        }
        if (isFavoriteButtonClicked){
            favoriteState = recyclerView.getLayoutManager().onSaveInstanceState();
        }
    }

    /**
     * Restores the state of RecyclerView after configuration change.
     */
    private void restoreListState(){
        if (isTopButtonClicked){
            if (topState != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(topState);
            } else {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        }
        if (isNewButtonClicked){
            if (newState != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(newState);
            } else {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        }
        if (isFavoriteButtonClicked){
            if (favoriteState != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(favoriteState);
            } else {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        }
    }

    /**
     * Saves some property values before configuration change.
     * @param state the bundle where the values are saved
     */
    private void saveListsState (Bundle state){
        state.putParcelable(TOP_STATE, topState);
        state.putParcelable(NEW_STATE, newState);
        state.putParcelable(FAVORITE_STATE, favoriteState);
        state.putBoolean(TOP_BUTTON_CLICKED, isTopButtonClicked);
        state.putBoolean(NEW_BUTTON_CLICKED, isNewButtonClicked);
        state.putBoolean(FAVORITE_BUTTON_CLICKED, isFavoriteButtonClicked);
        state.putString(LAST_BUTTON_CLICKED, lastButtonClicked);
        state.putParcelableArrayList(ADAPTER_DATA, (ArrayList<? extends Parcelable>) palettes);
    }

    /**
     * Restores some property values after configuration change.
     * @param state the bundle that holds the values
     */
    private void restoreListsState (Bundle state){
        topState = state.getParcelable(TOP_STATE);
        newState = state.getParcelable(NEW_STATE);
        favoriteState = state.getParcelable(FAVORITE_STATE);
        isTopButtonClicked = state.getBoolean(TOP_BUTTON_CLICKED);
        isNewButtonClicked = state.getBoolean(NEW_BUTTON_CLICKED);
        isFavoriteButtonClicked = state.getBoolean(FAVORITE_BUTTON_CLICKED);
        lastButtonClicked = state.getString(LAST_BUTTON_CLICKED);
        palettes = state.getParcelableArrayList(ADAPTER_DATA);
    }

    /**
     * At the first run of the app handles the ProgressBar when the service is running.
     */
    private void checkProgressBarStatus(){
        if (readString(this).equals(SERVICE_STARTED)){
            progressBar.setVisibility(View.VISIBLE);
        } else if (readString(this).equals(SERVICE_FINISHED)){
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Initializes the interstitial mobile ad.
     */
    private void initializeMobileAd(){
        MobileAds.initialize(this, AD_TEST_ID);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(ANOTHER_FORMAT_AD_TEST_ID);

        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    /**
     * Loads the mobile ad and starts the other activity.
     * @param position the position of the selected RecyclerView item
     */
    private void loadAd(final int position){
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
                startDetailActivity(position);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad: " + i);
                startDetailActivity(position);
            }
        });
            interstitialAd.show();
    }

    /**
     * Starts DetailActivity.
     * @param position the position of the selected RecyclerView item
     */
    private void startDetailActivity(int position){
        Bundle args = new Bundle();
        args.putInt(PALETTE_INDEX, position);
        args.putParcelableArrayList(PALETTE_ARRAY, (ArrayList<? extends Parcelable>) palettes);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtras(args);
        startActivity(intent);
        // Based on: https://stackoverflow.com/a/12092980
        overridePendingTransition(R.anim.slide_in, R.anim.no_animation);

    }

    /**
     * Sets up a RecyclerView to display the palettes.
     */
    private void setupRecyclerView(){
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        // Based on: https://antonioleiva.com/recyclerview-listener/
        paletteAdapter = new PaletteAdapter(new ArrayList<Palette>(), new PaletteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                if (!isNetworkConnection(MainActivity.this)){
                    startDetailActivity(position);
                    return;
                }

                if (isNetworkConnection(MainActivity.this) && interstitialAd != null && !interstitialAd.isLoaded()){
                    startDetailActivity(position);
                    return;
                }

                if (isNetworkConnection(MainActivity.this) && interstitialAd == null){
                    startDetailActivity(position);
                    initializeMobileAd();
                    return;
                }

                if (isNetworkConnection(MainActivity.this) && interstitialAd != null && interstitialAd.isLoaded()) {
                    loadAd(position);
                }
            }
        });
        recyclerView.setAdapter(paletteAdapter);
    }

    /**
     * Starts an IntentService to download data from the Internet.
     */
    private void startService(){
        String serviceStatus = readString(this);
        if (serviceStatus.equals(SERVICE_NEVER_RUN) || serviceStatus.equals(SERVICE_ERROR)) {
            PaletteResultReceiver resultReceiver = new PaletteResultReceiver(new Handler());
            resultReceiver.setReceiver(this);

            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, PaletteIntentService.class);
            intent.putExtra(RECEIVER, resultReceiver);
            startService(intent);
        }
    }

    /**
     * Saves the state of the RecyclerView.
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveListState();
    }

    /**
     * Restores the state of the Recyclerview, after configuration change or
     * when coming back from DetailActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        restoreListState();
    }

    /**
     * After a back press reloads the favorite list.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFavoriteButtonClicked) {
            bottomNavigationView.setSelectedItemId(R.id.palette_favorite);
        }
    }

    /**
     * Creates a custom options menu.
     * @param menu  the custom menu
     * @return true for the menu to be displayed, false it will not be shown
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  Handles custom menu's item selection.
     * @param item the menu item that was selected
     * @return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete_list:
                isListDeleteInitialized = true;
                getSupportLoaderManager().restartLoader(MAIN_LOADER_ID, makeBundle(CONTENT_URI_FAVORITE,null,null), this);
                return true;
            case R.id.action_exit:
                DialogFragment exitAppDialog = new ExitAppDialog();
                exitAppDialog.setCancelable(false);
                exitAppDialog.show(getSupportFragmentManager(), EXIT_APP_DIALOG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Queries the Content Provider of the app.
     * @param id the id of the Loader
     * @param args the Loader arguments
     * @return returns a new PaletteLoader
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new PaletteLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (isListDeleteInitialized && data.getCount()> 0){
            DialogFragment deleteDialog = new DeleteDialog();
            deleteDialog.setCancelable(false);
            deleteDialog.show(getSupportFragmentManager(), DELETE_DIALOG);
            isListDeleteInitialized = false;
            return;
        }

        if(isListDeleteInitialized){
            isListDeleteInitialized = false;
        }

        if (data.getCount() != 0){
            palettes = cursorToArrayList(data);
            paletteAdapter.changePaletteData(palettes);
            restoreListState();
        } else {
            Snackbar.make(mainLayout, R.string.no_favorite, Snackbar.LENGTH_SHORT).show();
            if (lastButtonClicked != null && lastButtonClicked.equals(TOP)) {
                bottomNavigationView.setSelectedItemId(R.id.palette_top);
            }
            if (lastButtonClicked != null && lastButtonClicked.equals(NEW)) {
                bottomNavigationView.setSelectedItemId(R.id.palette_new);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    /**
     * Receives the status of service.
     * @param resultCode the codes according to the service state
     */
    @Override
    public void onReceiveResult(int resultCode) {
        switch (resultCode) {
            case STATUS_STARTED:
                writeString(this, SERVICE_STARTED);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case STATUS_FINISHED:
                writeString(this, SERVICE_FINISHED);
                progressBar.setVisibility(View.GONE);
                bottomNavigationView.setSelectedItemId(R.id.palette_top);
                break;
            case STATUS_ERROR:
                writeString(this, SERVICE_ERROR);
                progressBar.setVisibility(View.GONE);
                writeBoolean(this, APP_HAS_RUN_BEFORE, false);
                Snackbar.make(mainLayout, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);
                break;
        }
    }

    /**
     * Alerts the user before exit from the app.
     */
    @Override
    public void onBackPressed() {
        if (backPressCounter > 0) {
            super.onBackPressed();
        } else {
            backPressCounter++;
            Snackbar.make(mainLayout, R.string.exit_message, Snackbar.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressCounter = 0;
                }
            }, 5000);
        }
    }

    /**
     * Saves the app's state before configuration change.
     * @param outState the bundle that holds the data
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveListsState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Loads the last list after favorite list delete, if the favorite list was visible.
     */
    @Override
    public void onFavoriteListDelete() {
        if (isFavoriteButtonClicked) {
            bottomNavigationView.setSelectedItemId(R.id.palette_favorite);
        }
    }
}
