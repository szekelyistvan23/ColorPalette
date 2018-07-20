package szekelyistvan.com.colorpalette.utils;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Makes a ContentValues object, in this way the specified palette object can be inserted in
     * a database.
     * @param palette the object with the needed data
     * @return the ContentValues object
     */
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

    /**
     * Extracts data from a Cursor and transforms it into an array.
     * @param cursor the data holder
     * @return the returned Palette array
     */
    public static List<Palette> cursorToArrayList (Cursor cursor){
        List<Palette> resultArrayList = new ArrayList<>();
        List<String> color;
        while (cursor.moveToNext()){
            color = new ArrayList<>();
            String title = cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_PALETTE_NAME));
            String url = cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_LINK));
            String data;
            for (int i = 0; i < 5; i++){
                data = cursor.getString(cursor.getColumnIndex(columns[i]));
                if (data != null && !data.equals("") ) {
                    color.add(data);
                }
            }
            resultArrayList.add(new Palette(title, color, url));
        }
        return resultArrayList;
    }
}
