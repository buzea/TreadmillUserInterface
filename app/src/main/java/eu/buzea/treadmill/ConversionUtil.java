package eu.buzea.treadmill;

import java.text.DecimalFormat;

/**
 * Created by Vlad on 08/01/2016.
 */
public class ConversionUtil {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###0.00");

    private ConversionUtil() {
    }

    public static CharSequence toTimeString(long seconds) {
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        String value;
        if (hours > 0) {
            value = String.format("%1d:%02d:%02d", hours, minutes, seconds);
        } else {
            value = String.format("%02d:%02d", minutes, seconds);
        }
        return value;
    }

    public static String formatTwoDigits(double value) {
        return DECIMAL_FORMAT.format(value);
    }
}
