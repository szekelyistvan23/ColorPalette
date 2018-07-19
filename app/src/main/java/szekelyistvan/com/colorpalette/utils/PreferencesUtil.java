package szekelyistvan.com.colorpalette.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static szekelyistvan.com.colorpalette.ui.MainActivity.DEFAULT_SHARED_PREFERENCES;

public class PreferencesUtil {
    public static final String SERVICE_DOWNLOAD_FINISHED = "service_download_finished";

    public static void writeBoolean(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean readBoolean(Context context, String key, boolean defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
