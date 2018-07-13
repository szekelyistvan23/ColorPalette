package szekelyistvan.com.colorpalette.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Based on: https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */

public class PaletteRemoteViewsService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PaletteRemoteViewsFactory(this.getApplicationContext());
    }
}
