package szekelyistvan.com.colorpalette.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static szekelyistvan.com.colorpalette.ui.MainActivity.TAG;

public class CheckInternet {

    /**
     *  Checks the state of the network connection.
     */
    public static boolean isNetworkConnection(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected() ||
                        networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                    return true;
                }
            }
        }catch (NullPointerException e){
            Log.d(TAG, "isNetworkConnection: " + e);
        }
        return false;
    }
}
