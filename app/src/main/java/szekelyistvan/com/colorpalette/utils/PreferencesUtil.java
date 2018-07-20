package szekelyistvan.com.colorpalette.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static szekelyistvan.com.colorpalette.ui.MainActivity.DEFAULT_SHARED_PREFERENCES;
import static szekelyistvan.com.colorpalette.ui.MainActivity.SERVICE_NEVER_RUN;
import static szekelyistvan.com.colorpalette.ui.MainActivity.SERVICE_STATUS;

public class PreferencesUtil {
    public static final String SERVICE_DOWNLOAD_FINISHED = "service_download_finished";

    /**
     * Writes a boolean to Shared Preferences.
     * @param context the method's context
     * @param key the key paired with the value
     * @param value the boolean to be written
     */
    public static void writeBoolean(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Reads a boolean from Shared Preferences.
     * @param context the method's context
     * @param key the key paired with the value
     * @param defaultValue the boolean's default value
     */
    public static boolean readBoolean(Context context, String key, boolean defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Writes a String to Shared Preferences.
     * @param context the method's context
     * @param value the String to be written
     */
    public static void writeString(Context context, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVICE_STATUS, value);
        editor.apply();
    }

    /**
     * Reads a String from Shared Preferences.
     * @param context the method's context
     * @return the String read from Shared Preferences
     */
    public static String readString(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SERVICE_STATUS, SERVICE_NEVER_RUN);
    }
}
