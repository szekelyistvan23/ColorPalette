package szekelyistvan.com.colorpalette.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class PaletteContract {

    public static final String AUTHORITY = "szekelyistvan.com.colorpalette";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class PaletteEntry implements BaseColumns {

        public static final String DB_NAME = "palettes.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME_TOP = "top";
        public static final String TABLE_NAME_NEW = "new";
        public static final String TABLE_NAME_FAVORITE = "favorite";
        public static final String PALETTES_COLUMN_PALETTE_NAME = "PALETTE_NAME";
        public static final String PALETTES_COLUMN_COLOR_ONE = "COLOR_ONE";
        public static final String PALETTES_COLUMN_COLOR_TWO = "COLOR_TWO";
        public static final String PALETTES_COLUMN_COLOR_THREE = "COLOR_THREE";
        public static final String PALETTES_COLUMN_COLOR_FOUR = "COLOR_FOUR";
        public static final String PALETTES_COLUMN_COLOR_FIVE = "COLOR_FIVE";
        public static final String PALETTES_COLUMN_LINK = "LINK";
        public static final String CREATE_TABLE_TOP = "CREATE TABLE " + TABLE_NAME_TOP + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PALETTES_COLUMN_PALETTE_NAME + " TEXT, "
                + PALETTES_COLUMN_COLOR_ONE + " TEXT, "
                + PALETTES_COLUMN_COLOR_TWO + " TEXT, "
                + PALETTES_COLUMN_COLOR_THREE + " TEXT, "
                + PALETTES_COLUMN_COLOR_FOUR + " TEXT, "
                + PALETTES_COLUMN_COLOR_FIVE + " TEXT, "
                + PALETTES_COLUMN_LINK + " TEXT);";
        public static final String CREATE_TABLE_NEW = "CREATE TABLE " + TABLE_NAME_NEW + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PALETTES_COLUMN_PALETTE_NAME + " TEXT, "
                + PALETTES_COLUMN_COLOR_ONE + " TEXT, "
                + PALETTES_COLUMN_COLOR_TWO + " TEXT, "
                + PALETTES_COLUMN_COLOR_THREE + " TEXT, "
                + PALETTES_COLUMN_COLOR_FOUR + " TEXT, "
                + PALETTES_COLUMN_COLOR_FIVE + " TEXT, "
                + PALETTES_COLUMN_LINK + " TEXT);";
        public static final String CREATE_TABLE_FAVORITE = "CREATE TABLE " + TABLE_NAME_FAVORITE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PALETTES_COLUMN_PALETTE_NAME + " TEXT, "
                + PALETTES_COLUMN_COLOR_ONE + " TEXT, "
                + PALETTES_COLUMN_COLOR_TWO + " TEXT, "
                + PALETTES_COLUMN_COLOR_THREE + " TEXT, "
                + PALETTES_COLUMN_COLOR_FOUR + " TEXT, "
                + PALETTES_COLUMN_COLOR_FIVE + " TEXT, "
                + PALETTES_COLUMN_LINK + " TEXT);";

        public static final Uri CONTENT_URI_TOP =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME_TOP).build();
        public static final Uri CONTENT_URI_NEW =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME_NEW).build();
        public static final Uri CONTENT_URI_FAVORITE =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME_FAVORITE).build();
    }
}