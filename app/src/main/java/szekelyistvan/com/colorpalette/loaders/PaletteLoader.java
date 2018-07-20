package szekelyistvan.com.colorpalette.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import static szekelyistvan.com.colorpalette.ui.MainActivity.TAG;

/**
 * Handles the data loading from the Content Provider for all the three lists of the app.
 */

public class PaletteLoader extends AsyncTaskLoader<Cursor>{

    private Bundle arguments;
    public static final String LOADER_URI = "loader_uri";
    public static final String LOADER_SELECTION = "loader_selection";
    public static final String LOADER_SELECTION_ARGS = "loader_selection_args";

    public PaletteLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.arguments = args;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {
        Uri uri = Uri.parse(arguments.getString(LOADER_URI));
        String selection =
                arguments.containsKey(LOADER_SELECTION) ? arguments.getString(LOADER_SELECTION) : null;

        String[] selectionArgs = {""};
        if (arguments.containsKey(LOADER_SELECTION_ARGS)){
            String item= arguments.getString(LOADER_SELECTION_ARGS);
            selectionArgs[0] = item;
        } else {
            selectionArgs = null;
        }

        try {
            return getContext().getContentResolver().query(uri,
                    null,
                    selection,
                    selectionArgs,
                    null);

        } catch (NullPointerException e) {
            Log.d(TAG, "loadInBackground: " + e);
            return null;
        }
    }
}
