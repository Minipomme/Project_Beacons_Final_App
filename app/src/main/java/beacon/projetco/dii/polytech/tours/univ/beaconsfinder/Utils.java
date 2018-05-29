package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * This class to convert dp in pixels
 * Created by Minipomme.
 */
public class Utils {

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
