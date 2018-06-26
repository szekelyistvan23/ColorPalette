package szekelyistvan.com.colorpalette.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.FavouritesEntry.CREATE_TABLE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.FavouritesEntry.DB_NAME;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.FavouritesEntry.DB_VERSION;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.FavouritesEntry.TABLE_NAME;

public class PaletteDbHelper extends SQLiteOpenHelper {

    public PaletteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

