package szekelyistvan.com.colorpalette.utils;

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
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;

import static szekelyistvan.com.colorpalette.ui.DetailFragment.WHITE;

/**
 * Custom RecyclerView adapter with a click listener to open an activity with the selected item's
 * details.
 */

public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.PaletteViewHolder>{

    private List<Palette> paletteList;
    private OnItemClickListener paletteListener;
    public static final String HASH = "#";

    public PaletteAdapter(List<Palette> paletteList, OnItemClickListener paletteListener) {
        this.paletteList = paletteList;
        this.paletteListener = paletteListener;
    }

    @NonNull
    @Override
    public PaletteAdapter.PaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.palette_item, parent, false);
        return new PaletteAdapter.PaletteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PaletteAdapter.PaletteViewHolder holder, int position) {
        int textViewColor = Color.parseColor(HASH+paletteList.get(position).getColors().get(0));

        holder.textView.setTextColor(ContrastColor.getContrastColor(textViewColor));
        holder.textView.setBackgroundColor(textViewColor);
        holder.textViewOne.setBackgroundColor(Color.parseColor(HASH+paletteList.get(position).getColors().get(1)));
        holder.textViewTwo.setBackgroundColor(Color.parseColor(HASH+paletteList.get(position).getColors().get(2)));
        holder.textViewThree.setBackgroundColor(Color.parseColor(HASH+paletteList.get(position).getColors().get(3)));
        if (paletteList.get(position).getColors().size() > 4) {
            holder.textViewFour.setBackgroundColor(Color.parseColor(HASH + paletteList.get(position).getColors().get(4)));
        } else {
            holder.textViewFour.setBackgroundColor(Color.parseColor(WHITE));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paletteListener.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.textViewFive.setText(paletteList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return paletteList != null ? paletteList.size() : 0 ;
    }

    class PaletteViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.textView1)
        TextView textViewOne;
        @BindView(R.id.textView2)
        TextView textViewTwo;
        @BindView(R.id.textView3)
        TextView textViewThree;
        @BindView(R.id.textView4)
        TextView textViewFour;
        @BindView(R.id.textView5)
        TextView textViewFive;

        public PaletteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void changePaletteData(List<Palette> newPaletteList) {
        paletteList = newPaletteList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
