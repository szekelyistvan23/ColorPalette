package szekelyistvan.com.colorpalette.utils;

import android.net.Uri;
import android.os.Bundle;

import static szekelyistvan.com.colorpalette.loaders.PaletteLoader.LOADER_SELECTION;
import static szekelyistvan.com.colorpalette.loaders.PaletteLoader.LOADER_SELECTION_ARGS;
import static szekelyistvan.com.colorpalette.loaders.PaletteLoader.LOADER_URI;

/**
 * Makes a bundle with the necessary data to query the Content Provider with a Loader.
 */
public class LoaderUtil {

    public static Bundle makeBundle(Uri uri, String selection, String selectionArgs){
        Bundle args = new Bundle();
        args.putString(LOADER_URI, uri.toString());
        if (selection != null){
            args.putString(LOADER_SELECTION, selection);
        }
        if (selectionArgs != null){
            args.putString(LOADER_SELECTION_ARGS, selectionArgs);
        }
        if (uri != null){
            return args;
        }
        return null;
    }
}
