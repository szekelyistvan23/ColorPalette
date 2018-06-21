package szekelyistvan.com.colorpalette.ui;

/*Copyright 2018 Szekely Isyvan

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import szekelyistvan.com.colorpalette.util.InternetClient;
import szekelyistvan.com.colorpalette.util.PaletteAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL ="http://www.colourlovers.com/api/palettes/";
    List<Palette> palettes;
    @BindView(R.id.palette_recyclerview)
    RecyclerView recyclerView;
    PaletteAdapter paletteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
        downloadJsonData();
    }

    /**
     * Sets up a RecyclerView to display the palettes.
     */
    private void setupRecyclerView(){
        // Butterknife is distributed under Apache License, Version 2.0
        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
//        Based on: https://antonioleiva.com/recyclerview-listener/
//        adapter = new CakeAdapter(new ArrayList<Cake>(), new CakeAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Cake cake) {
//                Bundle args = new Bundle();
//                args.putParcelable(CAKE_OBJECT, cake);
//                Intent intent = new Intent(MainActivity.this, FragmentsActivity.class);
//                intent.putExtras(args);
//                startActivity(intent);
//            }
//        });

        paletteAdapter = new PaletteAdapter(new ArrayList<Palette>(), new PaletteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Palette palette) {

            }
        });

        recyclerView.setAdapter(paletteAdapter);
    }

    /** Downloads data from the Internet using Retrofit and converts it to a List using
     * Gson converter. The implementation is based on:
     * https://www.youtube.com/watch?v=R4XU8yPzSx0
     * */
    private void downloadJsonData(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.build();

         InternetClient internetClient= retrofit.create(InternetClient.class);
        Call<List<Palette>> call = internetClient.palettesData();

        call.enqueue(new Callback<List<Palette>>() {
            @Override
            public void onResponse(Call<List<Palette>> call, Response<List<Palette>> response) {
                palettes = response.body();
                paletteAdapter.changePaletteData(palettes);

            }

            @Override
            public void onFailure(Call<List<Palette>> call, Throwable t) {
                finish();
                Toast.makeText(MainActivity.this, R.string.no_internet,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
