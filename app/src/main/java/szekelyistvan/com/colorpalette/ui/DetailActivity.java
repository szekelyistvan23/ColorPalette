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
import szekelyistvan.com.colorpalette.provider.FavoriteLoader;
import szekelyistvan.com.colorpalette.provider.PaletteLoader;
import szekelyistvan.com.colorpalette.utils.DepthPageTransformer;

import static szekelyistvan.com.colorpalette.provider.DatabaseUtils.removeDuplicates;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.ui.MainActivity.FAVORITE_ARRAY;
import static szekelyistvan.com.colorpalette.ui.MainActivity.MAIN_LOADER_ID;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_ARRAY;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_INDEX;
import static szekelyistvan.com.colorpalette.provider.DatabaseUtils.cursorToArrayList;
import static szekelyistvan.com.colorpalette.provider.LoaderUtil.makeBundle;
import static szekelyistvan.com.colorpalette.widget.PaletteWidget.POSITION_FROM_WIDGET;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private int paletteIndex;
    private List<Palette> baseArray;
    private ArrayList<String> favoriteArray;
    private DetailFragment detailFragment;
    public static final int DETAIL_LOADER_ID = 22;
    public static final int DETAIL_FAVORITE_LOADER_ID = 44;

    PalettePagerAdapter palettePagerAdapter;
    ViewPager viewPager;
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

        if (baseArray != null && baseArray.size() > 0){
            setTitle(baseArray.get(paletteIndex).getTitle());
            setUpViewPager();
        } else {
            if (getIntent().hasExtra(POSITION_FROM_WIDGET)) {
                paletteIndex = getIntent().getIntExtra(POSITION_FROM_WIDGET, 0);
                getSupportLoaderManager().restartLoader(DETAIL_LOADER_ID, makeBundle(CONTENT_URI_TOP,null,null), this);
            }
        }
    }

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
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            }
        };
        getSupportLoaderManager()
                .restartLoader(DETAIL_FAVORITE_LOADER_ID, null, queryResult);

    }

    private ArrayList<String> paletteToStringArray(List<Palette> paletteList){
        ArrayList<String> result = new ArrayList<>();
        for (Palette palette:paletteList) {
            result.add(palette.getTitle());
        }
        return result;
    }

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
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (favoriteArray != null && favoriteArray.contains(baseArray.get(paletteIndex).getTitle())) {
                    detailFragment = (DetailFragment) palettePagerAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
                    detailFragment.showHeart();
                }
            }
        }, 50);
    }

    public class PalettePagerAdapter extends FragmentStatePagerAdapter {

        public PalettePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DetailFragment.newInstance(baseArray.get(position));
        }

        @Override
        public int getCount() {
            return baseArray.size();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new PaletteLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data !=null && data.getCount() > 0){
            baseArray = cursorToArrayList(data);
            baseArray = removeDuplicates(baseArray);
            setTitle(baseArray.get(paletteIndex).getTitle());
            setUpViewPager();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
