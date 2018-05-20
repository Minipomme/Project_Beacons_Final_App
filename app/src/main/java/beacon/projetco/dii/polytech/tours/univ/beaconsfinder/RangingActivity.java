package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import java.util.Collection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.ClickEffectType;
import com.nightonke.boommenu.Types.OrderType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static java.lang.Math.abs;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer,
        BoomMenuButton.OnSubButtonClickListener {
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    private Thermometer thermometer;

    private Button button_reset;

    private Beacon selectedBeacon = null;

    private float distance;

    private boolean firstStart = false;
    private int RSSI_Init;
    private int RSSI_Now;
    private int num_beacons = 0;

    private float distCLOSE = 0;
    private float distFAR = 5;
    private int ColorCLOSE = Color.RED;
    private int ColorFAR = Color.CYAN;

    private BoomMenuButton boomMenuButton;
    private String selectedButton;

    private Context mContext;

    private boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        thermometer = findViewById(R.id.thermometer);
        button_reset = findViewById(R.id.button_reset);

        button_reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstStart = false;
            }
        } );

        beaconManager.bind(this);

        mContext = this;
        boomMenuButton = findViewById(R.id.boom);
        initViews();
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
            Log.e("e", "onBeaconServiceConnect");
            if (beacons.size() > 0) {
                initViews();
                num_beacons = beacons.size();
                selectedBeacon = null;
                for(Beacon b: beacons) {
                    Log.e("TEST",b.getBluetoothName());
                    if(!firstStart) {
                        selectedBeacon = b;
                        Log.e("TEST", "Selected beacon " + b.getBluetoothName());
                    } else if (selectedButton.equals( b.getBluetoothName() )) {
                        selectedBeacon = b;
                        Log.e("TEST", "Selected beacon " + b.getBluetoothName());
                    }
                }
                if(selectedBeacon != null) {
                    if(!firstStart) {
                        firstStart = true;
                        RSSI_Init = selectedBeacon.getRssi();
                        Log.d("STATE","Init : "+RSSI_Init);
                    } else {
                        RSSI_Now = selectedBeacon.getRssi();
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
                    public void run() {thermometer.setCurrentDist(distance);
                    }
                    });
                }
            }
           }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isInit) {
            initBoom();
        }
        isInit = true;
    }

    private void initBoom() {
        int number = num_beacons;

        if(number > 0) {
            boomMenuButton.setVisibility(View.VISIBLE);
            Drawable[] drawables = new Drawable[number];
            int drawableResource = R.drawable.ic_place_white;
            for (int i = 0; i < number; i++)
                drawables[i] = ContextCompat.getDrawable(mContext, drawableResource);

            String[] STRINGS = new String[]{
                    "Beacon 1",
                    "Beacon 2",
                    "Beacon 3",
                    "Beacon 4",
                    "Beacon 5",
                    "Beacon 6",
                    "Beacon 7",
                    "Beacon 8",
                    "Beacon 9"
            };
            String[] strings = new String[number];
            for (int i = 0; i < number; i++)
                strings[i] = STRINGS[i];

            int[][] colors = new int[number][2];
            for (int i = 0; i < number; i++) {
                colors[i][1] = Color.parseColor(Colors[i]);
                colors[i][0] = Util.getInstance().getPressedColor(colors[i][1]);
            }

            ButtonType buttonType = ButtonType.CIRCLE;

            // Now with Builder, you can init BMB more convenient
            new BoomMenuButton.Builder()
                    .subButtons(drawables, colors, strings)
                    .button(buttonType)
                    .boom(getBoomType())
                    .place(getPlaceType())
                    .boomButtonShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                    .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                    .onSubButtonClick(this)
                    .init(boomMenuButton);
        } else {
            boomMenuButton.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "No beacons founded !", Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews() {
        initBoom();
        boomMenuButton.setDuration(500);
        boomMenuButton.setDuration(500);
        boomMenuButton.setDelay(100);
        boomMenuButton.setDelay(100);
        boomMenuButton.setRotateDegree(360);
        boomMenuButton.setAutoDismiss(true);
        boomMenuButton.setShowOrderType(OrderType.RANDOM);
        boomMenuButton.setHideOrderType(OrderType.RANDOM);
        boomMenuButton.setClickEffectType(ClickEffectType.NORMAL);
    }

    private BoomType getBoomType() {
        return BoomType.PARABOLA_2;
    }

    private PlaceType getPlaceType() {
        if (num_beacons == 1) {
            return PlaceType.CIRCLE_1_1;
        } else if (num_beacons == 2) {
            return PlaceType.CIRCLE_2_1;
        } else if (num_beacons == 3) {
            return PlaceType.CIRCLE_3_1;
        } else if (num_beacons == 4) {
            return PlaceType.CIRCLE_4_1;
        } else if (num_beacons == 5) {
            return PlaceType.CIRCLE_5_1;
        } else if (num_beacons == 6) {
            return PlaceType.CIRCLE_6_1;
        } else if (num_beacons == 7) {
            return PlaceType.CIRCLE_7_1;
        } else if (num_beacons == 8) {
            return PlaceType.CIRCLE_8_1;
        } else if (num_beacons == 9) {
            return PlaceType.CIRCLE_9_1;
        }
        return PlaceType.CIRCLE_1_1;
    }

    private String[] Colors = {
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#FFC107",
            "#FFB300",
            "#FFA000",
            "#009688",
            "#4CAF50",
            "#8BC34A"};

    @Override
    public void onClick(int buttonIndex) {
        Toast.makeText(this, "You search " + boomMenuButton.getTextViews()[buttonIndex].getText().toString(), Toast.LENGTH_SHORT).show();
        selectedButton = boomMenuButton.getTextViews()[buttonIndex].getText().toString();
        firstStart = false;
    }

    @Override
    public void onBackPressed() {
        if (boomMenuButton.isClosed()) {
            super.onBackPressed();
        } else {
            boomMenuButton.dismiss();
        }
    }
}
