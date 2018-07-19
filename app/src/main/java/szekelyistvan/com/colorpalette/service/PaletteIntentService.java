package szekelyistvan.com.colorpalette.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import szekelyistvan.com.colorpalette.model.Palette;
import szekelyistvan.com.colorpalette.network.NewInternetClient;
import szekelyistvan.com.colorpalette.network.TopInternetClient;
import szekelyistvan.com.colorpalette.ui.MainActivity;
import szekelyistvan.com.colorpalette.provider.PaletteAsyncQueryHandler;

import static szekelyistvan.com.colorpalette.network.CheckInternet.isNetworkConnection;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_NEW;
import static szekelyistvan.com.colorpalette.provider.PaletteContract.PaletteEntry.CONTENT_URI_TOP;
import static szekelyistvan.com.colorpalette.ui.MainActivity.APP_HAS_RUN_BEFORE;
import static szekelyistvan.com.colorpalette.ui.MainActivity.BASE_URL;
import static szekelyistvan.com.colorpalette.ui.MainActivity.NEW;
import static szekelyistvan.com.colorpalette.ui.MainActivity.TOP;
import static szekelyistvan.com.colorpalette.provider.DatabaseUtils.paletteToContentValues;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.SERVICE_DOWNLOAD_FINISHED;
import static szekelyistvan.com.colorpalette.utils.PreferencesUtil.writeBoolean;

/**
 * Based on: http://stacktips.com/tutorials/android/creating-a-background-service-in-android
 */

public class PaletteIntentService extends IntentService {

    public static final int STATUS_STARTED = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private ResultReceiver resultReceiver;
    private List<Palette> palettes;

    public PaletteIntentService() {
        super(PaletteIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        resultReceiver = intent.getParcelableExtra("receiver");

        if (!isNetworkConnection(getApplicationContext())) {
            resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
        } else {
            resultReceiver.send(STATUS_STARTED, Bundle.EMPTY);
            downloadJsonData(TOP);
            downloadJsonData(NEW);
        }

    }

    private void downloadJsonData(final @MainActivity.InternetClient String client) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        TopInternetClient topInternetClient = retrofit.create(TopInternetClient.class);
        NewInternetClient newInternetClient = retrofit.create(NewInternetClient.class);
        Call<List<Palette>> call = null;

        switch (client) {
            case TOP:
                call = topInternetClient.topPalettesData();
                break;
            case NEW:
                call = newInternetClient.newPalettesData();
                break;
        }

        call.enqueue(new Callback<List<Palette>>() {
            @Override
            public void onResponse(Call<List<Palette>> call, Response<List<Palette>> response) {
                palettes = response.body();
                checkArray();
                arrayToContentProvider(client);
            }

            @Override
            public void onFailure(Call<List<Palette>> call, Throwable t) {
                resultReceiver.send(STATUS_ERROR, Bundle.EMPTY);
            }
        });
    }

    private void checkArray() {
        List<Palette> resultArray = new ArrayList<>();
        for (Palette palette : palettes) {
            if (palette.getColors().size() >= 4) {
                resultArray.add(palette);
            }
        }
        palettes = resultArray;
    }

    private void arrayToContentProvider(@MainActivity.InternetClient String database) {
        Uri uri = null;
        switch (database) {
            case TOP:
                uri = CONTENT_URI_TOP;
                break;
            case NEW:
                uri = CONTENT_URI_NEW;
                break;
        }

        ContentValues[] contentValues = new ContentValues[palettes.size()];
        PaletteAsyncQueryHandler asyncQueryHandler = new PaletteAsyncQueryHandler(getContentResolver());
        for (int i = 0; i < palettes.size(); i++) {
            contentValues[i] = paletteToContentValues(palettes.get(i));
        }

        if (getContentResolver().delete(uri, null, null) == 0) {
            asyncQueryHandler.startBulkInsert(0, null, uri, contentValues);
        }
        if (uri.equals(CONTENT_URI_NEW)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    resultReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
                    writeBoolean(PaletteIntentService.this, APP_HAS_RUN_BEFORE, true);
                    writeBoolean(PaletteIntentService.this, SERVICE_DOWNLOAD_FINISHED, true);
                }
            }, 10000);
        }
    }
}
