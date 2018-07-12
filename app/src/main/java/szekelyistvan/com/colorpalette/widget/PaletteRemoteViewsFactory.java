package szekelyistvan.com.colorpalette.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import szekelyistvan.com.colorpalette.R;

import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_FIVE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_FOUR;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_ONE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_THREE;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.PALETTES_COLUMN_COLOR_TWO;
import static szekelyistvan.com.colorpalette.util.PaletteAdapter.HASH;

/**
 * Based on: https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */

public class PaletteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Cursor cursor;

    public PaletteRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }

        long identityToken = Binder.clearCallingIdentity();

        cursor = context
                        .getContentResolver()
                        .query(CONTENT_URI_TOP,null, null, null, null);

        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || cursor == null || !cursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.palette_widget_list_item);
        remoteViews.setInt(R.id.appwidget_text_one, "setBackgroundColor", Color.parseColor(HASH +
                cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_COLOR_ONE))));
        remoteViews.setInt(R.id.appwidget_text_two, "setBackgroundColor", Color.parseColor(HASH +
                cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_COLOR_TWO))));
        remoteViews.setInt(R.id.appwidget_text_three, "setBackgroundColor", Color.parseColor(HASH +
                cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_COLOR_THREE))));
        remoteViews.setInt(R.id.appwidget_text_four, "setBackgroundColor", Color.parseColor(HASH +
                cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_COLOR_FOUR))));

        String color = cursor.getString(cursor.getColumnIndex(PALETTES_COLUMN_COLOR_FIVE));
        if (color != null) {
            remoteViews.setInt(R.id.appwidget_text_five, "setBackgroundColor", Color.parseColor(HASH + color));
        } else {
            remoteViews.setInt(R.id.appwidget_text_five, "setBackgroundColor", Color.WHITE);
        }

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(PaletteWidget.POSITION_FROM_WIDGET, position);
        remoteViews.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return cursor.moveToPosition(position) ? cursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
