package szekelyistvan.com.colorpalette.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class PaletteContract {

    public static final String AUTHORITY = "szekelyistvan.com.colorpalette";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_PALETTES = "palettes";

    public static final class FavouritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PALETTES).build();

        public static final String DB_NAME = "palettes.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "palettes";
        public static final String FAVOURITES_COLUMN_ID = "PALETTE_ID";
        public static final String FAVOURITES_COLUMN_PALETTE_NAME = "PALETTE_NAME";
        public static final String FAVOURITES_COLUMN_COLOR_ONE = "COLOR_ONE";
        public static final String FAVOURITES_COLUMN_COLOR_TWO = "COLOR_TWO";
        public static final String FAVOURITES_COLUMN_COLOR_THREE = "COLOR_THREE";
        public static final String FAVOURITES_COLUMN_COLOR_FOUR = "COLOR_FOUR";
        public static final String FAVOURITES_COLUMN_COLOR_FIVE = "COLOR_FIVE";
        public static final String FAVOURITES_COLUMN_FAVORITE = "FAVORITE";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FAVOURITES_COLUMN_ID + " TEXT, "
                + FAVOURITES_COLUMN_PALETTE_NAME + " TEXT, "
                + FAVOURITES_COLUMN_COLOR_ONE + " TEXT, "
                + FAVOURITES_COLUMN_COLOR_TWO + " TEXT, "
                + FAVOURITES_COLUMN_COLOR_THREE + " TEXT, "
                + FAVOURITES_COLUMN_COLOR_FOUR + " TEXT, "
                + FAVOURITES_COLUMN_COLOR_FIVE + " TEXT, "
                + FAVOURITES_COLUMN_FAVORITE + " TEXT);";
    }
}