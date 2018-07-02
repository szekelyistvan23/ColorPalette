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

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.util.NewInternetClient;
import szekelyistvan.com.colorpalette.util.TopInternetClient;
import szekelyistvan.com.colorpalette.util.PaletteAdapter;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.util.DatabaseUtils.paletteToContentValues;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL ="http://www.colourlovers.com/api/palettes/";
    public static final String PALETTE_DETAIL = "palette_detail";
    public static final String PALETTE_ARRAY = "palette_array";
    List<Palette> palettes;
    @BindView(R.id.palette_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    PaletteAdapter paletteAdapter;

    @Retention(SOURCE)
    @StringDef({ TOP, NEW})
    public @interface InternetClient {}
    public static final String TOP = "top";
    public static final String NEW = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Butterknife is distributed under Apache License, Version 2.0
        ButterKnife.bind(this);

        setupRecyclerView();
        downloadJsonData(TOP);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.palette_top:
                        downloadJsonData(TOP);
                        break;
                    case R.id.palette_new:
                        downloadJsonData(NEW);
                        break;
                    case R.id.palette_favorite:
                }
                return true;
            }
        });
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
            public void onItemClick(int position) {
                Bundle args = new Bundle();
                args.putInt(PALETTE_DETAIL, position);
                args.putParcelableArrayList(PALETTE_ARRAY, (ArrayList<? extends Parcelable>) palettes);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(paletteAdapter);
    }

    /** Downloads data from the Internet using Retrofit and converts it to a List using
     * Gson converter. The implementation is based on:
     * https://www.youtube.com/watch?v=R4XU8yPzSx0
     * */
    private void downloadJsonData(final @InternetClient String client){

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        TopInternetClient topInternetClient= retrofit.create(TopInternetClient.class);
        NewInternetClient newInternetClient= retrofit.create(NewInternetClient.class);
        Call<List<Palette>> call = null;

        switch(client){
            case TOP:
                call = topInternetClient.topPalettesData();
                break;
            case NEW:
                call = newInternetClient.newPalettesData();
                break;
        }

        call.enqueue(new Callback<List<Palette>>() {
            @Override
            public void onResponse(Call<List<Palette>> call, Response<List<Palette>> response) {
                palettes = response.body();
                checkArray();
                paletteAdapter.changePaletteData(palettes);
                arrayToContentProvider(client);
            }

            @Override
            public void onFailure(Call<List<Palette>> call, Throwable t) {
                finish();
                Toast.makeText(MainActivity.this, t.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void checkArray(){
        List<Palette> resultArray = new ArrayList<>();
        for (Palette palette:palettes) {
            if (palette.getColors().size() >= 4){
                resultArray.add(palette);
            }
        }
        palettes = resultArray;
    }

    private void arrayToContentProvider(@InternetClient String database){
        Uri uri = null;
        switch(database){
            case TOP:
                uri = CONTENT_URI_TOP;
                break;
            case NEW:
                uri = CONTENT_URI_NEW;
                break;
        }
        for (Palette palette:palettes) {
            getContentResolver().insert(uri, paletteToContentValues(palette));
        }
    }

}
