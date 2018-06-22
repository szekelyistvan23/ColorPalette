package szekelyistvan.com.colorpalette.ui;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.util.ContrastColor;

import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_OBJECT;
import static szekelyistvan.com.colorpalette.util.PaletteAdapter.HASH;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

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
    @BindView(R.id.badgeImage)
    ImageView badgeImageView;
    @BindView(R.id.detailCardView)
    CardView detailCardView;
    private Unbinder unbinder;

    public static final String EMPTY_STRING = "";
    public static final String WHITE = "#FFFFFF";
    public static final int SMALLER_SIZE = 4;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            palette = getArguments().getParcelable(PALETTE_OBJECT);
        } else {
            getActivity().finish();
            Toast.makeText(getActivity(), R.string.no_data, Toast.LENGTH_SHORT).show();
        }

        setBackgroundColor();

        //TODO disable clicklistener on error
        //TODO after load no internet in detail activity, crash

        Glide.with(this).
                load(palette.getBadgeUrl()).
                into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        badgeImageView.setImageDrawable(resource);
                        detailCardView.setCardElevation(2);
                    }
                });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setBackgroundColor(){
        int size = palette.getColors().size();
        List<TextView> textViews =
                Arrays.asList(detailTextView, detailTextViewOne, detailTextViewTwo, detailTextViewThree, detailTextViewFour);
        for (int i = 0; i < size; i++){
            setTextViewProperties(textViews.get(i), i);
        }
        if (size == SMALLER_SIZE){
            detailTextViewFour.setText(EMPTY_STRING);
            detailTextViewFour.setBackgroundColor(Color.parseColor(WHITE));
        }
    }

    private void setTextViewProperties(TextView textView, int position){
        textView.setBackgroundColor(Color.parseColor(HASH+palette.getColors().get(position)));
        textView.setTextColor(ContrastColor.getContrastColor(Color.parseColor(extractBackgroundColor(position))));
        textView.setText(HASH+palette.getColors().get(position));
    }

    private String extractBackgroundColor(int position){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HASH);
        stringBuilder.append(palette.getColors().get(position));

        return  stringBuilder.toString();
    }
}
