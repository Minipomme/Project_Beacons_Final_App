package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

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

        Beacon bcn;
        ImageView imgView;
        for(int i=0; i< NB_Beacons;i++){
            bcn=new Beacon(i+1);

            imgView = new ImageView(currentActivity.getApplicationContext());
            imgView.setImageResource(R.drawable.ic_place_black_24dp);
            imgView.setLayoutParams(new RelativeLayout.LayoutParams(50,50));
            bcn.setImage(imgView);

            listBeacon.add(bcn);
        }
    }

    public static List<Beacon> getBeaconsToFind(){
        return listBeacon;
    }

    public static String[] getBeaconsToFindString(int nb_beacons){
        String [] beacons = new String[nb_beacons];
        int indexBeacons = 0;
        for(Beacon bcn : listBeacon){
            beacons[indexBeacons]="Beacon " + bcn.getName();
            indexBeacons++;
        }
        return beacons;
    }
}
