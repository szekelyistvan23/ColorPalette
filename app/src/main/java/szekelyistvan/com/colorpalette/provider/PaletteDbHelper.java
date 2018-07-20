package szekelyistvan.com.colorpalette.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CREATE_TABLE_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CREATE_TABLE_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CREATE_TABLE_TOP;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.DB_NAME;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.DB_VERSION;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.TABLE_NAME_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.TABLE_NAME_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.TABLE_NAME_TOP;

/**
 * Custom DBHelper class.
 */

public class PaletteDbHelper extends SQLiteOpenHelper {

    public PaletteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TOP);
        db.execSQL(CREATE_TABLE_NEW);
        db.execSQL(CREATE_TABLE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TOP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_NEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAVORITE);
        onCreate(db);
    }
}

