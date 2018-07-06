package szekelyistvan.com.colorpalette.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PaletteRemoteViewsService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PaletteRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
