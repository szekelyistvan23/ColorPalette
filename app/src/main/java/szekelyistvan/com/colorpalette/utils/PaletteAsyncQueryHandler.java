package szekelyistvan.com.colorpalette.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

public class PaletteAsyncQueryHandler extends AsyncQueryHandler{
    public PaletteAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
