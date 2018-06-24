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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.List;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;

import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_ARRAY;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_INDEX;

public class DetailActivity extends AppCompatActivity {

    private int paletteIndex;
    private List<Palette> baseArray;


    PalettePagerAdapter palettePagerAdapter;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getIntent().hasExtra(PALETTE_INDEX)){
            paletteIndex = getIntent().getIntExtra(PALETTE_INDEX, 0);
        }

        if (getIntent().hasExtra(PALETTE_ARRAY)){
            baseArray = getIntent().getParcelableArrayListExtra(PALETTE_ARRAY);
        }

        if (baseArray != null){

            palettePagerAdapter = new PalettePagerAdapter(getSupportFragmentManager());

            viewPager = findViewById(R.id.viewpager);
            viewPager.setAdapter(palettePagerAdapter);

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
                }
            });

         viewPager.setCurrentItem(paletteIndex, false);
        }
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
}
