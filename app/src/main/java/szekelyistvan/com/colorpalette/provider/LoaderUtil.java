package szekelyistvan.com.colorpalette.provider;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import static szekelyistvan.com.colorpalette.provider.PaletteLoader.LOADER_SELECTION;
import static szekelyistvan.com.colorpalette.provider.PaletteLoader.LOADER_SELECTION_ARGS;
import static szekelyistvan.com.colorpalette.provider.PaletteLoader.LOADER_URI;

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
