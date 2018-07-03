package szekelyistvan.com.colorpalette.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

public class PaletteAsyncQueryHandler extends AsyncQueryHandler{

    private AsyncQueryListener listener;

    public PaletteAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public PaletteAsyncQueryHandler(ContentResolver cr, AsyncQueryListener listener) {
        super(cr);
        this.listener = listener;
    }

    public interface AsyncQueryListener{
        void onQueryComplete(Cursor cursor);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            listener.onQueryComplete(cursor);
    }
}
