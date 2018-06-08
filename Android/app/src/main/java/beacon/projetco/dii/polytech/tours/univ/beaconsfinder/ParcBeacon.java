package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Class that manages the entire parc of beacons. It is used to separate the UI from data
 */
public class ParcBeacon {
    static ArrayList<Beacon> listBeacon;

    private int NB_Arduinos;
    private int NB_Beacons;

    public ParcBeacon(MapActivity currentActivity){
        NB_Arduinos=Integer.parseInt(currentActivity.getApplicationContext().getString(R.string.NB_ARDUINO));
        NB_Beacons=Integer.parseInt(currentActivity.getApplicationContext().getString(R.string.NB_BEACONS));

        listBeacon = new ArrayList<Beacon>();

        float[] HSVColorSTART = new float[3];
        float[] HSVColorEND = new float[3];
        Color.RGBToHSV(Color.red(Color.RED), Color.green(Color.RED), Color.blue(Color.RED), HSVColorSTART);
        Color.RGBToHSV(Color.red(Color.MAGENTA), Color.green(Color.MAGENTA), Color.blue(Color.MAGENTA), HSVColorEND);
        float[] HSVColorBeacon = new float[3];
        float pas = abs(HSVColorSTART[0] - HSVColorEND[0]) / NB_Beacons;

        Beacon bcn;
        ImageView imgView;
        for(int i=0; i< NB_Beacons;i++){
            bcn=new Beacon(i+1);


            HSVColorBeacon[0] = i * pas;
            HSVColorBeacon[1] = 1;
            HSVColorBeacon[2] = 1;

            imgView = new ImageView(currentActivity.getApplicationContext());
            imgView.setImageResource(R.drawable.ic_place_black);
            imgView.setLayoutParams(new RelativeLayout.LayoutParams(100,100));
            imgView.setColorFilter(Color.HSVToColor(HSVColorBeacon));
            bcn.setImage(imgView);

            listBeacon.add(bcn);
        }
    }

    /**
     * Return a list of beacon (parc)
     * @return
     */
    public List<Beacon> getBeaconsToFind(){
        return listBeacon;
    }

    /**
     * Return a string of all the beacons in the parc (names)
     * @return
     */
    public String[] getBeaconsToFindString(){
        String [] beacons = new String[NB_Beacons];
        int indexBeacons = 0;
        for(Beacon bcn : listBeacon){
            beacons[indexBeacons]="Beacon " + bcn.getName();
            indexBeacons++;
        }
        return beacons;
    }
}
