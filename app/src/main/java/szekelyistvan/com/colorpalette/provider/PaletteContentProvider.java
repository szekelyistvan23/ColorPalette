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

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.TABLE_NAME_FAVORITE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.TABLE_NAME_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.TABLE_NAME_TOP;

public class PaletteContentProvider extends ContentProvider {


    public static final int TOP = 100;
    public static final int TOP_WITH_ID = 101;
    public static final int NEW = 200;
    public static final int NEW_WITH_ID = 201;
    public static final int FAVORITE = 300;
    public static final int FAVORITE_WITH_ID = 301;
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
        String querySelection = SELECTION;

        Cursor cursor;

        switch (match){
            case TOP:
                cursor = db.query(TABLE_NAME_TOP,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TOP_WITH_ID:
                String idTop = uri.getPathSegments().get(DATA_PART);
                String[] topSelectionArgs = new String[] {idTop};

                cursor = db.query(TABLE_NAME_TOP,
                        projection,
                        querySelection,
                        topSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case NEW:
                cursor = db.query(TABLE_NAME_NEW,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case NEW_WITH_ID:

                String idNew = uri.getPathSegments().get(DATA_PART);
                String[] newSelectionArgs = new String[] {idNew};

                cursor = db.query(TABLE_NAME_NEW,
                        projection,
                        querySelection,
                        newSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE:
                cursor = db.query(TABLE_NAME_FAVORITE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_WITH_ID:

                String idFavorite = uri.getPathSegments().get(DATA_PART);
                String[] favoriteSelectionArgs = new String[] {idFavorite};

                cursor = db.query(TABLE_NAME_FAVORITE,
                        projection,
                        querySelection,
                        favoriteSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

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
            case TOP:
                long idTop = db.insert(TABLE_NAME_TOP, null, values);
                if (idTop > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI_TOP, idTop);
                } else {
                    throw new android.database.SQLException(getContext().getString(R.string.failed_insert) + uri);
                }
                break;
            case NEW:
                long idNew = db.insert(TABLE_NAME_NEW, null, values);
                if (idNew > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI_NEW, idNew);
                } else {
                    throw new android.database.SQLException(getContext().getString(R.string.failed_insert) + uri);
                }
                break;
            case FAVORITE:
                long idFavorite = db.insert(TABLE_NAME_FAVORITE, null, values);
                if (idFavorite > 0) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI_FAVORITE, idFavorite);
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
        int paletteDeleted;

        switch (match) {

            case TOP:
                paletteDeleted = db.delete(TABLE_NAME_TOP,
                        selection,
                        selectionArgs);
                break;

            case TOP_WITH_ID:
                String idTop = uri.getPathSegments().get(1);
                paletteDeleted = db.delete(TABLE_NAME_TOP, SELECTION, new String[]{idTop});
                break;
            case NEW:
                paletteDeleted = db.delete(TABLE_NAME_NEW,
                        selection,
                        selectionArgs);
                break;

            case NEW_WITH_ID:
                String idNew = uri.getPathSegments().get(1);
                paletteDeleted = db.delete(TABLE_NAME_NEW, SELECTION, new String[]{idNew});
                break;
            case FAVORITE:
                paletteDeleted = db.delete(TABLE_NAME_FAVORITE,
                        selection,
                        selectionArgs);
                break;

            case FAVORITE_WITH_ID:
                String idFavorite = uri.getPathSegments().get(1);
                paletteDeleted = db.delete(TABLE_NAME_FAVORITE, SELECTION, new String[]{idFavorite});
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        if (paletteDeleted != UNSUCCESSFUL_DELETE) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return paletteDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException(getContext().getString(R.string.not_yet));
    }

    public static UriMatcher buildUriMatcher (){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PaletteContract.AUTHORITY, TABLE_NAME_TOP, TOP);
        uriMatcher.addURI(PaletteContract.AUTHORITY, TABLE_NAME_TOP + NUMBER_OF_ROW, TOP_WITH_ID);

        uriMatcher.addURI(PaletteContract.AUTHORITY, TABLE_NAME_NEW, NEW);
        uriMatcher.addURI(PaletteContract.AUTHORITY, TABLE_NAME_NEW + NUMBER_OF_ROW, NEW_WITH_ID);

        uriMatcher.addURI(PaletteContract.AUTHORITY, TABLE_NAME_FAVORITE, FAVORITE);
        uriMatcher.addURI(PaletteContract.AUTHORITY, TABLE_NAME_FAVORITE + NUMBER_OF_ROW, FAVORITE_WITH_ID);

        return uriMatcher;
    }
}
