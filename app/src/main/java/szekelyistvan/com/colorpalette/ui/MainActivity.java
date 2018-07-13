package szekelyistvan.com.colorpalette.ui;

/*Copyright 2018 Szekely Istvan

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
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
import szekelyistvan.com.colorpalette.provider.PaletteLoader;
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
import static szekelyistvan.com.colorpalette.provider.DatabaseUtils.cursorToArrayList;
import static szekelyistvan.com.colorpalette.provider.LoaderUtil.makeBundle;

public class MainActivity extends AppCompatActivity implements PaletteResultReceiver.Receiver,
        DeleteDialog.DeleteDialogListener, LoaderManager.LoaderCallbacks<Cursor>{

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
    public static final String EXIT_APP_DIALOG = "exit_app_dialog";
    public static final String DELETE_DIALOG = "delete_dialog";
    public static final String AD_TEST_ID = "ca-app-pub-3940256099942544~3347511713";
    public static final String ANOTHER_FORMAT_AD_TEST_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final int LOADER_ID = 22;

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
    private LinearLayoutManager linearLayoutManager;
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
        // Butterknife is distributed under Apache License, Version 2.0
        ButterKnife.bind(this);
        setSupportActionBar(mainActivityToolbar);
        Fabric.with(this, new Crashlytics());

        if (isNetworkConnection(this)){
            initializeMobileAd();
        }

        setupRecyclerView();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.palette_top:
                        saveListState();
                        getSupportLoaderManager().restartLoader(LOADER_ID, makeBundle(CONTENT_URI_TOP,null,null), MainActivity.this);
                        isTopButtonClicked = true; isNewButtonClicked = false; isFavoriteButtonClicked = false;
                        break;
                    case R.id.palette_new:
                        saveListState();
                        getSupportLoaderManager().restartLoader(LOADER_ID, makeBundle(CONTENT_URI_NEW,null,null), MainActivity.this);
                        isTopButtonClicked = false; isNewButtonClicked = true; isFavoriteButtonClicked = false;
                        break;
                    case R.id.palette_favorite:
                        saveListState();
                        getSupportLoaderManager().restartLoader(LOADER_ID, makeBundle(CONTENT_URI_FAVORITE,null,null), MainActivity.this);
                        if (isTopButtonClicked){
                            lastButtonClicked = TOP;
                        }
                        if (isNewButtonClicked){
                            lastButtonClicked = NEW;
                        }
                        isTopButtonClicked = false; isNewButtonClicked = false; isFavoriteButtonClicked = true;
                        break;
                }
                return true;
            }
        });
        if (appHasRunBefore(this)){
            bottomNavigationView.setSelectedItemId(R.id.palette_top);
        } else {
            startService();
            appRunBefore(this);
        }
    }
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
    private void restoreListState(){
        if (isTopButtonClicked && topState != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(topState);
        }
        if (isNewButtonClicked && newState != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(newState);
        }
        if (isFavoriteButtonClicked && favoriteState != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(favoriteState);
        }
    }

    private void saveListsState (Bundle state){
        state.putParcelable(TOP_STATE, topState);
        state.putParcelable(NEW_STATE, newState);
        state.putParcelable(FAVORITE_STATE, favoriteState);
    }

    private void restoreListsState (Bundle state){
        topState = state.getParcelable(TOP_STATE);
        newState = state.getParcelable(NEW_STATE);
        favoriteState = state.getParcelable(FAVORITE_STATE);
    }

    private void initializeMobileAd(){
        MobileAds.initialize(this, AD_TEST_ID);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(ANOTHER_FORMAT_AD_TEST_ID);

        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

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

        linearLayoutManager = new LinearLayoutManager(this);
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

    private boolean isAnyButtonCLicked(){
        return !isTopButtonClicked && !isNewButtonClicked && !isFavoriteButtonClicked;
    }

    private void startService(){
        PaletteResultReceiver resultReceiver = new PaletteResultReceiver(new Handler());
        resultReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, PaletteIntentService.class);
        intent.putExtra(RECEIVER, resultReceiver);
        startService(intent);
    }

    public static boolean appHasRunBefore(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(APP_HAS_RUN_BEFORE, false);
    }

    public static void appRunBefore(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(APP_HAS_RUN_BEFORE, true);
        editor.apply();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFavoriteButtonClicked) {
            bottomNavigationView.setSelectedItemId(R.id.palette_favorite);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_delete_list:
                isListDeleteInitialized = true;
                getSupportLoaderManager().restartLoader(LOADER_ID, makeBundle(CONTENT_URI_FAVORITE,null,null), this);
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

    @Override
    public void onReceiveResult(int resultCode) {
        switch (resultCode) {
            case STATUS_STARTED:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case STATUS_FINISHED:
                progressBar.setVisibility(View.GONE);
                bottomNavigationView.setSelectedItemId(R.id.palette_top);
                break;
            case STATUS_ERROR:
                progressBar.setVisibility(View.GONE);
                Snackbar.make(mainLayout, R.string.error_message, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveListsState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        restoreListsState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onFavoriteListDelete() {
        if (isFavoriteButtonClicked) {
            bottomNavigationView.setSelectedItemId(R.id.palette_favorite);
        }
    }
}
