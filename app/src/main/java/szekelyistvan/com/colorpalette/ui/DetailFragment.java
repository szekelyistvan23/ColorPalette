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
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.util.ContrastColor;
import szekelyistvan.com.colorpalette.util.PaletteAsyncQueryHandler;

import static szekelyistvan.com.colorpalette.network.CheckInternet.isNetworkConnection;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_PALETTE_NAME;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_INDEX;
import static szekelyistvan.com.colorpalette.util.DatabaseUtils.paletteToContentValues;
import static szekelyistvan.com.colorpalette.util.PaletteAdapter.HASH;
import static szekelyistvan.com.colorpalette.util.PaletteAdapter.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements PaletteAsyncQueryHandler.AsyncQueryListener{

    private Palette palette;
    private PaletteAsyncQueryHandler asyncHandler;

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
    @BindView(R.id.speed_dial_view)
    SpeedDialView speedDialView;
    @BindView(R.id.favorite_image)
    ImageView favoriteImage;
    private Unbinder unbinder;

    public static final String EMPTY_STRING = "";
    public static final String WHITE = "#FFFFFF";
    public static final int SMALLER_SIZE = 4;
    public static final String NO_COLOR = "no_color";

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        asyncHandler = new PaletteAsyncQueryHandler(getActivity().getContentResolver(), this);

        if (getArguments() != null) {
            palette = getArguments().getParcelable(PALETTE_INDEX);
        } else {
            getActivity().finish();
            Toast.makeText(getActivity(), R.string.no_data, Toast.LENGTH_SHORT).show();
        }

        setBackgroundColor();

        //TODO after load no internet in detail activity, crash

        speedDialView.inflate(R.menu.speed_dial_menu);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_favorite:
                        if (favoriteImage.getVisibility() == View.GONE) {
                            favoriteImage.setVisibility(View.VISIBLE);
                            asyncHandler.startInsert(0, null, CONTENT_URI_FAVORITE, paletteToContentValues(palette));
                        } else {
                            favoriteImage.setVisibility(View.GONE);
                            String[] selectionArgs ={palette.getTitle()};
                            asyncHandler.startDelete(0, null, CONTENT_URI_FAVORITE, "PALETTE_NAME =?", selectionArgs);
                        }
                        return false;
                    case R.id.fab_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sharePalette());
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_palette)));
                        return false;
                    case R.id.fab_link:
                if (isNetworkConnection(getActivity())) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(palette.getUrl()));
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            Snackbar.make(DetailFragment.this.getView(), R.string.install_browser, Snackbar.LENGTH_SHORT).show();
                        }
                } else {
                    Snackbar.make(DetailFragment.this.getView(), R.string.no_internet, Snackbar.LENGTH_SHORT).show();
                }
                        return false;
                    default:
                        return false;
                }
            }
        });

        String[] projection = { PALETTES_COLUMN_PALETTE_NAME};
        String[] selectionArgs ={palette.getTitle()};
        asyncHandler.startQuery(0, null, CONTENT_URI_FAVORITE, projection, "PALETTE_NAME =?", selectionArgs, null);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onQueryComplete(Cursor cursor) {
    String query = extractPaletteName(cursor);
    if (query.equals(palette.getTitle())){
        favoriteImage.setVisibility(View.VISIBLE);
    }
        Log.d(TAG, "onQueryComplete: " + query);
    }

    public static Fragment newInstance(Palette palette) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PALETTE_INDEX, palette);
        Fragment fragment = new DetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private String sharePalette() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(palette.getTitle());
        stringBuilder.append(":");
        int size = palette.getColors().size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(" #");
            stringBuilder.append(palette.getColors().get(i));
            if (i != size - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    private void setBackgroundColor() {
        int size = palette.getColors().size();
        List<TextView> textViews =
                Arrays.asList(detailTextView, detailTextViewOne, detailTextViewTwo, detailTextViewThree, detailTextViewFour);
        for (int i = 0; i < size; i++) {
            setTextViewProperties(textViews.get(i), i);
        }
        if (size == SMALLER_SIZE) {
            detailTextViewFour.setText(EMPTY_STRING);
            detailTextViewFour.setBackgroundColor(Color.parseColor(WHITE));
        }
    }

    private void setTextViewProperties(TextView textView, int position) {
        textView.setBackgroundColor(Color.parseColor(HASH + palette.getColors().get(position)));
        textView.setTextColor(ContrastColor.getContrastColor(Color.parseColor(extractBackgroundColor(position))));
        textView.setText(extractBackgroundColor(position));
    }

    private String extractBackgroundColor(int position) {
        return HASH + palette.getColors().get(position);
    }

    public void closeFab(){
        if (speedDialView.isOpen()){
            speedDialView.close(true);
        }
    }

    private String extractPaletteName (Cursor cursor){
        String result="";
        while (cursor.moveToNext()){
            result = cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_PALETTE_NAME));
        }
        return result;
    }

}
