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

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.utils.ContrastColor;
import szekelyistvan.com.colorpalette.provider.PaletteAsyncQueryHandler;

import static szekelyistvan.com.colorpalette.network.CheckInternet.isNetworkConnection;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_INDEX;
import static szekelyistvan.com.colorpalette.utils.DatabaseUtils.paletteToContentValues;
import static szekelyistvan.com.colorpalette.utils.PaletteAdapter.HASH;

/**
 * Contains details about the color palettes.
 */
public class DetailFragment extends Fragment{

    private Palette palette;

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
    @BindView(R.id.detail_fragment_layout)
    CoordinatorLayout detailFragmentLayout;
    private Unbinder unbinder;

    public static final String EMPTY_STRING = "";
    public static final String WHITE = "#FFFFFF";
    public static final int SMALLER_SIZE = 4;
    public static final String SELECTION = "PALETTE_NAME =?";
    public static final String INTENT_TYPE = "text/plain";
    public static final String SPACE_HASH = " #";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String FAVORITE_ARRAY = "favorite_array";
    private ArrayList<String> favoriteArray;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            palette = getArguments().getParcelable(PALETTE_INDEX);
            favoriteArray = getArguments().getStringArrayList(FAVORITE_ARRAY);
        } else {
            getActivity().finish();
            Toast.makeText(getActivity(), R.string.no_data, Toast.LENGTH_SHORT).show();
        }

        setBackgroundColor();

        setupFabMenu();

        if (favoriteArray !=null && favoriteArray.contains(palette.getTitle())){
            showHeart();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Creates a fragment from the given Palette object.
     * @param palette the base data of the fragment
     * @return the new fragment
     */
    public static Fragment newInstance(Palette palette, ArrayList<String> favorite) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PALETTE_INDEX, palette);
        bundle.putStringArrayList(FAVORITE_ARRAY, favorite);
        Fragment fragment = new DetailFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Builds a Share Intent extra text.
     * @return the returned text
     */
    private String sharePalette() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(palette.getTitle());
        stringBuilder.append(COLON);
        int size = palette.getColors().size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(SPACE_HASH);
            stringBuilder.append(palette.getColors().get(i));
            if (i != size - 1) {
                stringBuilder.append(COMMA);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Customizes the displayed TextViews.
     */
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

    /**
     * Sets a TexView's text, text color and background color.
     * @param textView the TextView that will be customized
     * @param position the data is taken from an array, this is the index
     */
    private void setTextViewProperties(TextView textView, int position) {
        textView.setBackgroundColor(Color.parseColor(HASH + palette.getColors().get(position)));
        textView.setTextColor(ContrastColor.getContrastColor(Color.parseColor(extractBackgroundColor(position))));
        textView.setText(extractBackgroundColor(position));
    }

    /**
     * Builds a color code, with a hash.
     * @param position the data is taken from an array, this is the index
     * @return returns the color code
     */
    private String extractBackgroundColor(int position) {
        return HASH + palette.getColors().get(position);
    }

    /**
     * Closes the FAB menu if it is open.
     */
    public void closeFab(){
        if (speedDialView.isOpen()){
            speedDialView.close(true);
        }
    }

    /**
     * Shows a heart if the palette is in the favorite list.
     */
    public void showHeart(){
        if (favoriteImage != null) {
            favoriteImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets up a FAB menu.
     */
    private void setupFabMenu(){
        final PaletteAsyncQueryHandler asyncHandler =
                new PaletteAsyncQueryHandler(getActivity().getContentResolver());

        speedDialView.inflate(R.menu.speed_dial_menu);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_favorite:
                        if (favoriteImage.getVisibility() == View.GONE) {
                            favoriteImage.setVisibility(View.VISIBLE);
                            favoriteArray.add(palette.getTitle());
                            asyncHandler.startInsert(0, null, CONTENT_URI_FAVORITE, paletteToContentValues(palette));
                        } else {
                            favoriteImage.setVisibility(View.GONE);
                            favoriteArray.remove(palette.getTitle());
                            String[] selectionArgs ={palette.getTitle()};
                            asyncHandler.startDelete(0, null, CONTENT_URI_FAVORITE, SELECTION, selectionArgs);
                        }
                        return false;
                    case R.id.fab_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sharePalette());
                        sendIntent.setType(INTENT_TYPE);
                        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_palette)));
                        return false;
                    case R.id.fab_link:
                        if (isNetworkConnection(getActivity())) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(palette.getUrl()));
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Snackbar.make(detailFragmentLayout, R.string.install_browser, Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(detailFragmentLayout, R.string.no_internet, Snackbar.LENGTH_SHORT).show();
                        }
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}
