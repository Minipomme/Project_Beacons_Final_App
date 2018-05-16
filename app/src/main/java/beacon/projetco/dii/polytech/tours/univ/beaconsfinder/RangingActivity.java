package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import java.util.Collection;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.util.Log;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static java.lang.Math.abs;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    /* Start Merge */
    private Thermometer thermometer;
    //private TextView txtRSSI;
    //private TextView txtRange;

    private float distance;

    private boolean firstStart = false;
    private int RSSI_Init;
    private int RSSI_Now;

    private float distCLOSE = 0;
    private float distFAR = 5;
    private int ColorCLOSE = Color.RED;
    private int ColorFAR = Color.CYAN;

    /* End Merge */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        thermometer = findViewById(R.id.thermometer);
        //txtRSSI = findViewById(R.id.RSSIText);
        //txtRange = findViewById(R.id.RangeText);
        beaconManager.bind(this);
    }

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override 
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override 
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
           @Override
           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

              if (beacons.size() > 0) {
                  /*for(Beacon firstBeacon : beacons){
                      logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away." + " Nb beacons : " + beacons.size());
                  }*/
                  //EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                  Beacon firstBeacon = beacons.iterator().next();

                  /* Start Merge */

                  if(!firstStart) {
                      firstStart = true;
                      RSSI_Init = firstBeacon.getRssi();
                      Log.d("STATE","Init : "+RSSI_Init);
                      //txtRSSI.setText(RSSI_Init);
                  } else {
                      RSSI_Now = firstBeacon.getRssi();
                      Log.d("STATE","Init : "+RSSI_Init+" Now : "+RSSI_Now);
                  }

                  float ratio = RSSI_Now*1.0f/RSSI_Init;
                  if (ratio < 1.0f) {
                      distance = (float) Math.pow(ratio,10);
                  }
                  else {
                      distance = (float) ((0.89976)*Math.pow(ratio,7.7095) + 0.111);
                  }
                  Log.d("STATE","Distance : "+distance + "m");


                  /*if(RSSI_Now >= RSSI_Init){
                      distance = (float) (0.89976*Math.pow((RSSI_Now / RSSI_Init), 7.7095) + 0.111);
                  }
                  else{
                      distance = (float) (Math.pow(RSSI_Now / RSSI_Init, 10));
                  }*/

                  //txtRange.setText(distance + " m");

                  if(distance < distCLOSE) {
                      thermometer.setCurrentInnerColor(ColorCLOSE);
                  } else if (distance > distFAR) {
                      thermometer.setCurrentInnerColor(ColorFAR);
                  } else {
                      float[] HSVColorCLOSE = new float[3];
                      float[] HSVColorFAR = new float[3];
                      Color.RGBToHSV(Color.red(ColorCLOSE), Color.green(ColorCLOSE), Color.blue(ColorCLOSE), HSVColorCLOSE);
                      Color.RGBToHSV(Color.red(ColorFAR), Color.green(ColorFAR), Color.blue(ColorFAR), HSVColorFAR);
                      float[] HSVColorNOW = new float[3];
                      float pas = abs(HSVColorCLOSE[0] - HSVColorFAR[0]) / abs(distCLOSE - distFAR);

                      HSVColorNOW[0] = distance * pas;
                      HSVColorNOW[1] = 1;
                      HSVColorNOW[2] = 1;
                      thermometer.setCurrentInnerColor(Color.HSVToColor(HSVColorNOW));
                  }

                  runOnUiThread(new Runnable() {
                      public void run() {
                          thermometer.setCurrentDist(distance);
                      }
                  });

                  //indicateDist( (int) distance );

                  /* End Merge */
              }
           }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    private void indicateDist(final int dist) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView txtView = findViewById(R.id.RangeText);
                if(txtView != null) {
                    txtView.setText( Integer.toString( dist ) );
                }
            }
        });
    }


}
