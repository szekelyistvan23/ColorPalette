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

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.util.ContrastColor;

import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_OBJECT;
import static szekelyistvan.com.colorpalette.util.PaletteAdapter.HASH;

public class DetailActivity extends AppCompatActivity {

    private Palette receivedPalette;

    @BindView(R.id.detailTextView)
    TextView detailTextView;
    @BindView(R.id.detailTextView1)
    TextView detailTextViewOne;
    @BindView(R.id.detailTextView2)
    TextView detailTextViewTwo;
    @BindView(R.id.detailTextView3)
    TextView detailTextViewThree;
    @BindView(R.id.detailTextView4)
    TextView detailTextViewFour;
    @BindView(R.id.badgeImage)
    ImageView badgeImageView;

    public static final String EMPTY_STRING = "";
    public static final String WHITE = "#FFFFFF";
    public static final int SMALLER_PATTERN = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receivedPalette = extras.getParcelable(PALETTE_OBJECT);
            setTitle(receivedPalette.getTitle());
        } else {
            finish();
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }

        setBackgroundColor();

        Glide.with(this).
                load(receivedPalette.getBadgeUrl()).
                into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        badgeImageView.setImageDrawable(resource);
                    }
                });
    }

    private void setBackgroundColor(){
        int size = receivedPalette.getColors().size();
        List<TextView> textViews =
                Arrays.asList(detailTextView, detailTextViewOne, detailTextViewTwo, detailTextViewThree, detailTextViewFour);
            for (int i = 0; i < size; i++){
                setTextViewProperties(textViews.get(i), i);
            }
            if (size == SMALLER_PATTERN){
                detailTextViewFour.setText(EMPTY_STRING);
                detailTextViewFour.setBackgroundColor(Color.parseColor(WHITE));
            }
    }

    private void setTextViewProperties(TextView textView, int position){
        textView.setBackgroundColor(Color.parseColor(HASH+receivedPalette.getColors().get(position)));
        textView.setTextColor(ContrastColor.getContrastColor(Color.parseColor(extractBackgroundColor(position))));
        textView.setText(HASH+receivedPalette.getColors().get(position));
    }

    private String extractBackgroundColor(int position){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HASH);
        stringBuilder.append(receivedPalette.getColors().get(position));

        return  stringBuilder.toString();
    }
}
