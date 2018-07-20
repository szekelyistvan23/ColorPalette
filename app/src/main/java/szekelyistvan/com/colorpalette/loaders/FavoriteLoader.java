package szekelyistvan.com.colorpalette.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_FAVORITE;
import static szekelyistvan.com.colorpalette.ui.MainActivity.TAG;

/**
 * Loads the favorite list's content.
 */

public class FavoriteLoader extends AsyncTaskLoader<Cursor> {

    public FavoriteLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {
        try {
            return getContext().getContentResolver().query(CONTENT_URI_FAVORITE,
                    null,
                    null,
                    null,
                    null);

        } catch (NullPointerException e) {
            Log.d(TAG, "loadInBackground: " + e);
            return null;
        }
    }
}