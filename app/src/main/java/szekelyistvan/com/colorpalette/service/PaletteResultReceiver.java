package szekelyistvan.com.colorpalette.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * A custom ResultReceiver that sends the service's state to MainActivity.
 * Based on: Based on: http://stacktips.com/tutorials/android/creating-a-background-service-in-android
 */

public class PaletteResultReceiver extends ResultReceiver{
    private Receiver receiver;

    public PaletteResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver{
        void onReceiveResult(int resultCode);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode);
        }
    }
}
