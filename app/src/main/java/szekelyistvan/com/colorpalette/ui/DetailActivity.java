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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.loaders.FavoriteLoader;
import szekelyistvan.com.colorpalette.loaders.PaletteLoader;
import szekelyistvan.com.colorpalette.utils.DepthPageTransformer;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_ARRAY;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_INDEX;
import static szekelyistvan.com.colorpalette.utils.DatabaseUtils.cursorToArrayList;
import static szekelyistvan.com.colorpalette.utils.LoaderUtil.makeBundle;
import static szekelyistvan.com.colorpalette.widget.PaletteWidget.POSITION_FROM_WIDGET;

/**
 * Displays a ViewPager with color palettes' details.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private int paletteIndex;
    private List<Palette> baseArray;
    private ArrayList<String> favoriteArray;
    private DetailFragment detailFragment;
    public static final int DETAIL_LOADER_ID = 22;
    public static final int DETAIL_FAVORITE_LOADER_ID = 44;
    public static final String FAVORITE_ARRAY_SAVED = "favorite_array_saved";
    public static final String CURRENT_POSITION = "current_position";
    public static final String ONPAUSE_CALLED = "onpause_called";
    public static final String PALETTE_PREFERENCES="palette_preferences";

    private PalettePagerAdapter palettePagerAdapter;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Fabric.with(this, new Crashlytics());
        queryFavoriteList();

        if (getIntent().hasExtra(PALETTE_INDEX)){
            paletteIndex = getIntent().getIntExtra(PALETTE_INDEX, 0);
        }

        if (getIntent().hasExtra(PALETTE_ARRAY)){
            baseArray = getIntent().getParcelableArrayListExtra(PALETTE_ARRAY);
        }

        if(savedInstanceState != null){
            favoriteArray = savedInstanceState.getStringArrayList(FAVORITE_ARRAY_SAVED);
            int savedPosition = savedInstanceState.getInt(CURRENT_POSITION);
            if (savedPosition >= 0){
                paletteIndex = savedPosition;
            }
        }

        if (baseArray == null && getIntent().hasExtra(POSITION_FROM_WIDGET)) {
            if (paletteIndex == 0){
                saveValue(this, ONPAUSE_CALLED, 0);
                paletteIndex = getIntent().getIntExtra(POSITION_FROM_WIDGET, 0);
            }
            getSupportLoaderManager().restartLoader(DETAIL_LOADER_ID, makeBundle(CONTENT_URI_TOP,null,null), this);
        }
    }

    /**
     * Loads the favorite list with a Loader.
     */
    private void queryFavoriteList(){
        LoaderManager.LoaderCallbacks<Cursor> queryResult = new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                return new FavoriteLoader(DetailActivity.this);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                favoriteArray = paletteToStringArray(cursorToArrayList(data));
                Log.d("ColorPalette", "onLoadFinished: " + favoriteArray.toString());
                if (baseArray != null && baseArray.size() > 0){
                    setTitle(baseArray.get(paletteIndex).getTitle());
                    setUpViewPager();
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            }
        };
        getSupportLoaderManager()
                .restartLoader(DETAIL_FAVORITE_LOADER_ID, null, queryResult);

    }

    /**
     * Transforms a Palette array in a String array, the elements are the palettes's titles.
     * @param paletteList the Palette array
     * @return the String array
     */
    private ArrayList<String> paletteToStringArray(List<Palette> paletteList){
        ArrayList<String> result = new ArrayList<>();
        for (Palette palette:paletteList) {
            result.add(palette.getTitle());
        }
        return result;
    }

    /**
     * Sets a ViewPager with FragmentStatePagerAdapter.
     */
    private void setUpViewPager(){
        palettePagerAdapter = new PalettePagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(palettePagerAdapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTitle(baseArray.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                detailFragment = (DetailFragment) palettePagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
                if (detailFragment != null && state == 1) {
                    detailFragment.closeFab();
                }
            }
        });

        viewPager.setCurrentItem(paletteIndex, false);
    }

    /**
     * The adapter of the ViewPager.
     */
    public class PalettePagerAdapter extends FragmentStatePagerAdapter {

        public PalettePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            saveValue(DetailActivity.this, CURRENT_POSITION, position);
            return DetailFragment.newInstance(baseArray.get(position), favoriteArray);
        }

        @Override
        public int getCount() {
            return baseArray.size();
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
        if (data !=null && data.getCount() > 0){
            baseArray = cursorToArrayList(data);
            setTitle(baseArray.get(paletteIndex).getTitle());
            setUpViewPager();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    /**
     * Saves an int to shared preferences.
     */
    public static void saveValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PALETTE_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
    }

    /**
     * Reads an int from shared preferences.
     */
    public int readValue(String key) {
        SharedPreferences sharedPreferences =
                this.getSharedPreferences(PALETTE_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * Deletes all the position related data from shared preferences.
     */
    public static void clearPosition(Context context){
        saveValue(context, CURRENT_POSITION, 0);
        saveValue(context, ONPAUSE_CALLED, 0);
    }

    @Override
    protected void onResume() {
        int savedPosition = readValue(CURRENT_POSITION);
        int onPause = readValue(ONPAUSE_CALLED);
        if (savedPosition >= 0 && onPause == 1){
            paletteIndex = savedPosition;
            clearPosition(this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem >= 0) {
            saveValue(this, CURRENT_POSITION, currentItem);
            saveValue(this, ONPAUSE_CALLED, 1);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        int onPause = readValue(ONPAUSE_CALLED);
        if (onPause == 1) {
            clearPosition(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(FAVORITE_ARRAY_SAVED, favoriteArray);
        int currentItem = viewPager.getCurrentItem();
        if (currentItem >= 0) {
            outState.putInt(CURRENT_POSITION, currentItem);
        }
    }

    /**
     * Overrides the home button's behaviour.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Closes the FAB menu if it is open.
     */
    @Override
    public void onBackPressed() {
        detailFragment = (DetailFragment) palettePagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        if (detailFragment != null) {
            detailFragment.closeFab();
        }
        super.onBackPressed();
        // Based on: https://stackoverflow.com/a/12092980
        overridePendingTransition(R.anim.no_animation, R.anim.slide_out);
    }
}
