package szekelyistvan.com.colorpalette.provider;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

public class PaletteAsyncQueryHandler extends com.madrapps.asyncquery.AsyncQueryHandler{
    public PaletteAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
