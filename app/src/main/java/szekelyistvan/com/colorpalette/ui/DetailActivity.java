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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;

import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_OBJECT;

public class DetailActivity extends AppCompatActivity {

    private Palette receivedPalette;

    public static final String DETAIL_FRAGMENT = "detail_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getIntent().hasExtra(PALETTE_OBJECT)){
            receivedPalette = getIntent().getParcelableExtra(PALETTE_OBJECT);
        }
        if (receivedPalette != null){
            setTitle(receivedPalette.getTitle());
            displayDetailFragment();
        }
    }
    private void displayDetailFragment(){
        ButterKnife.bind(this);
        Bundle args = new Bundle();
        args.putParcelable(PALETTE_OBJECT, receivedPalette);

        DetailFragment searchDetailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);

        if (searchDetailFragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, searchDetailFragment, DETAIL_FRAGMENT)
                    .commit();
        } else {

            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, detailFragment, DETAIL_FRAGMENT)
                    .commit();
        }
    }
}
