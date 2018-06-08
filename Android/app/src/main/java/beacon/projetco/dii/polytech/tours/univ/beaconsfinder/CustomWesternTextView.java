package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Font of the text : Indicon Join
 * Created by Minipomme on 23/05/2018.
 */

public class CustomWesternTextView extends android.support.v7.widget.AppCompatTextView {


    public CustomWesternTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/RioGrande.ttf"));
    }
}
