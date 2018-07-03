package szekelyistvan.com.colorpalette.util;

import android.content.ContentValues;

import szekelyistvan.com.colorpalette.model.Palette;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_FIVE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_FOUR;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_ONE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_THREE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_TWO;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_LINK;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_PALETTE_NAME;

public class DatabaseUtils {
    public static final String[] columns = {PALETTES_COLUMN_COLOR_ONE, PALETTES_COLUMN_COLOR_TWO,
            PALETTES_COLUMN_COLOR_THREE, PALETTES_COLUMN_COLOR_FOUR,
            PALETTES_COLUMN_COLOR_FIVE};
    public static ContentValues paletteToContentValues(Palette palette){
        int size = palette.getColors().size();

        ContentValues contentValues = new ContentValues();
        contentValues.put(PALETTES_COLUMN_PALETTE_NAME, palette.getTitle());

        for (int i =0; i < size; i++){
            contentValues.put(columns[i], palette.getColors().get(i));
        }
        contentValues.put(PALETTES_COLUMN_LINK, palette.getUrl());
        return contentValues;
    }
}
