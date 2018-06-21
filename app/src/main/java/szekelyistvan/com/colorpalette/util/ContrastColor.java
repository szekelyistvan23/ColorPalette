package szekelyistvan.com.colorpalette.util;

import android.graphics.Color;

public class ContrastColor {
    /**
     * Based on:
     * https://stackoverflow.com/a/39031835
     * @param colorIntValue
     * @return
     */
    public static int getContrastColor(int colorIntValue) {
        int red = Color.red(colorIntValue);
        int green = Color.green(colorIntValue);
        int blue = Color.blue(colorIntValue);
        double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
        return lum > 186 ? 0xFF000000 : 0xFFFFFFFF;
    }
}
