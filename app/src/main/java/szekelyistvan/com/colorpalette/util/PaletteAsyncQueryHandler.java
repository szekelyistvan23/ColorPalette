package szekelyistvan.com.colorpalette.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

public class PaletteAsyncQueryHandler extends AsyncQueryHandler{

    public PaletteAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
