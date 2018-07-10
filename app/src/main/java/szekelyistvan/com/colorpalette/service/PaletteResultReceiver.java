package szekelyistvan.com.colorpalette.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class PaletteResultReceiver extends ResultReceiver{
    private Receiver receiver;

    public PaletteResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver{
        public void onReceiveResult(int resultCode);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode);
        }
    }
}
