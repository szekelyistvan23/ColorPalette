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
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.network.NewInternetClient;
import szekelyistvan.com.colorpalette.network.TopInternetClient;
import szekelyistvan.com.colorpalette.service.PaletteIntentService;
import szekelyistvan.com.colorpalette.service.PaletteResultReceiver;
import szekelyistvan.com.colorpalette.util.PaletteAdapter;
import szekelyistvan.com.colorpalette.util.PaletteAsyncQueryHandler;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static szekelyistvan.com.colorpalette.network.CheckInternet.isNetworkConnection;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.service.PaletteIntentService.STATUS_ERROR;
import static szekelyistvan.com.colorpalette.service.PaletteIntentService.STATUS_FINISHED;
import static szekelyistvan.com.colorpalette.service.PaletteIntentService.STATUS_STARTED;
import static szekelyistvan.com.colorpalette.util.DatabaseUtils.cursorToArrayList;
import static szekelyistvan.com.colorpalette.util.DatabaseUtils.paletteToContentValues;

public class MainActivity extends AppCompatActivity implements
        PaletteAsyncQueryHandler.AsyncQueryListener, PaletteResultReceiver.Receiver{

    public static final String BASE_URL ="http://www.colourlovers.com/api/palettes/";
    public static final String PALETTE_INDEX = "palette_index";
    public static final String PALETTE_ARRAY = "palette_array";
    public static final String TAG = "ColorPalette";
    public static final String DEFAULT_SHARED_PREFERENCES = "PalettePreferences";
    public static final String APP_HAS_RUN_BEFORE = "app_has_run_before";
    List<Palette> palettes;
    @BindView(R.id.palette_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.downloadProgress)
    ProgressBar progressBar;
    PaletteAdapter paletteAdapter;
    private PaletteAsyncQueryHandler asyncHandler;

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
    private PaletteResultReceiver resultReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Butterknife is distributed under Apache License, Version 2.0
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        if (isNetworkConnection(this)){
            initializeMobileAd();
        }

        asyncHandler = new PaletteAsyncQueryHandler(getContentResolver(), this);

        setupRecyclerView();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.palette_top:
                        asyncHandler.startQuery(0, null, CONTENT_URI_TOP, null, null, null, null);
                        isTopButtonClicked = true; isNewButtonClicked = false; isFavoriteButtonClicked = false;
                        break;
                    case R.id.palette_new:
                        asyncHandler.startQuery(0, null, CONTENT_URI_NEW, null, null, null, null);
                        isTopButtonClicked = false; isNewButtonClicked = true; isFavoriteButtonClicked = false;
                        break;
                    case R.id.palette_favorite:
                        asyncHandler.startQuery(0, null, CONTENT_URI_FAVORITE, null, null, null, null);
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

    private void initializeMobileAd(){
        MobileAds.initialize(this,
                "ca-app-pub-3940256099942544~3347511713");

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

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

    private boolean isAnyButtonCLicked(){
        return !isTopButtonClicked && !isNewButtonClicked && !isFavoriteButtonClicked;
    }

    private void startService(){
        resultReceiver = new PaletteResultReceiver(new Handler());
        resultReceiver.setReceiver(this);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, PaletteIntentService.class);
        intent.putExtra("receiver", resultReceiver);
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
                asyncHandler.startDelete(0, null, CONTENT_URI_FAVORITE, null, null);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onQueryComplete(Cursor cursor) {

        if (cursor.getCount() != 0){
            palettes = cursorToArrayList(cursor);
            paletteAdapter.changePaletteData(palettes);
        } else {
            Snackbar.make(findViewById(R.id.main_layout), R.string.no_favorite, Snackbar.LENGTH_SHORT).show();
            if (lastButtonClicked != null && lastButtonClicked.equals(TOP)) {
                bottomNavigationView.setSelectedItemId(R.id.palette_top);
            }
            if (lastButtonClicked != null && lastButtonClicked.equals(NEW)) {
                bottomNavigationView.setSelectedItemId(R.id.palette_new);
            }
        }
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
                break;
        }
    }
}
