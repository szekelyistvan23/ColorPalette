package szekelyistvan.com.colorpalette.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.ui.DetailActivity;

/**
 * Implementation of App Widget functionality that displays a list of color palettes and on click
 * opens the corresponding details.
 * Based on: https://www.sitepoint.com/killer-way-to-show-a-list-of-items-in-android-collection-widget/
 */
public class PaletteWidget extends AppWidgetProvider {

    public static final String POSITION_FROM_WIDGET ="position_from_widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.palette_widget);
            Intent intent = new Intent(context, PaletteRemoteViewsService.class);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);

            Intent onClickIntent = new Intent(context, DetailActivity.class);
            PendingIntent onClickPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(onClickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, onClickPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

