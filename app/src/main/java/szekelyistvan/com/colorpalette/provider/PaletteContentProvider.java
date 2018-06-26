package szekelyistvan.com.colorpalette.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import szekelyistvan.com.colorpalette.R;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.FavouritesEntry.CONTENT_URI;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.FavouritesEntry.TABLE_NAME;

public class PaletteContentProvider extends ContentProvider {


    public static final int PALETTES = 200;
    public static final int PALETTES_WITH_ID = 201;
    public static final int DATA_PART = 1;
    public static final String SELECTION = "_id=?";
    public static final String NUMBER_OF_ROW = "/#";
    public static final int UNSUCCESSFUL_DELETE = 0;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private PaletteDbHelper paletteDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        paletteDbHelper = new PaletteDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = paletteDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch (match){
            case PALETTES:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case PALETTES_WITH_ID:

                String id = uri.getPathSegments().get(DATA_PART);
                String mSelection = SELECTION;
                String[] mSelectionArgs = new String[] {id};

                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);


        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException(getContext().getString(R.string.not_yet));
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        final SQLiteDatabase db = paletteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case PALETTES:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException(getContext().getString(R.string.failed_insert) + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = paletteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int favouritesDeleted;

        switch (match) {

            case PALETTES:
                favouritesDeleted = db.delete(TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case PALETTES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                favouritesDeleted = db.delete(TABLE_NAME, SELECTION, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        if (favouritesDeleted != UNSUCCESSFUL_DELETE) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return favouritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException(getContext().getString(R.string.not_yet));
    }

    public static UriMatcher buildUriMatcher (){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PaletteContract.AUTHORITY, PaletteContract.PATH_PALETTES, PALETTES);

        uriMatcher.addURI(PaletteContract.AUTHORITY, PaletteContract.PATH_PALETTES + NUMBER_OF_ROW, PALETTES_WITH_ID);


        return uriMatcher;
    }
}
