package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {
    public int NB_Arduinos=4;
    public int NB_Beacons=3;
    private float[][][] arrayAverage = new float[NB_Arduinos][NB_Beacons][21];
    private List<List<Float>> arrayArduino;
    private double[] distancesBeacon1;
    private double[] distancesBeacon2;
    private double[] distancesBeacon3;
    private float RSSI_Init=55;
    private Trilateration Localization;
    private MapActivity currentActivity;
    private BluetoothLeScanner scanner;
    private boolean flagScan = true;

    public DataManager(MapActivity currentActivity, BluetoothLeScanner scanner){
        this.currentActivity=currentActivity;
        this.scanner=scanner;

        arrayArduino = new ArrayList<List<Float>>();
        for (int i = 0; i <= NB_Arduinos - 1; i++) {
            arrayArduino.add(new ArrayList<Float>());
            for (int j = 0; j <= NB_Beacons - 1; j++) {
                arrayArduino.get(i).add((float) 0);
            }
        }
    }

    public void setLocalisationResult(Long[] result,byte[] data){
        float distance;
        distance = calcDist(result);

        addValue(distance,data[3]-1,data[2]-1);
        arrayArduino.get(data[3]-1).set(data[2]-1,getAverage(data[3]-1,data[2]-1));

        Log.d("RESULT","---------------------------------------------");
        Log.d("RESULT", "[Distances Arduino 1] : "+arrayArduino.get(0));
        Log.d("RESULT", "[Distances Arduino 2] : "+arrayArduino.get(1));
        Log.d("RESULT", "[Distances Arduino 3] : "+arrayArduino.get(2));
        Log.d("RESULT", "[Distances Arduino 4] : "+arrayArduino.get(3));
        Log.d("RESULT","---------------------------------------------");


        distancesBeacon1 = new double[] {arrayArduino.get(0).get(0),
                arrayArduino.get(1).get(0),
                arrayArduino.get(2).get(0),
                arrayArduino.get(3).get(0)};
        distancesBeacon2 = new double[] {arrayArduino.get(0).get(1),
                arrayArduino.get(1).get(1),
                arrayArduino.get(2).get(1),
                arrayArduino.get(3).get(1)};
        distancesBeacon3 = new double[] {arrayArduino.get(0).get(2),
                arrayArduino.get(1).get(2),
                arrayArduino.get(2).get(2),
                arrayArduino.get(3).get(2)};

        if(dataComplete(distancesBeacon1)==true && dataComplete(distancesBeacon2)==true && dataComplete(distancesBeacon3)==true && flagScan){
            scanner.stopScan( new ScanCallback(){});
            flagScan=false;
        }

        if(Localization==null){
            Localization = new Trilateration(currentActivity);
        }

        double [][] positions = new double[][]{{Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_one()),
                Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_one())},
                {Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_two()),
                        Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_two())},
                {Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_three()),
                        Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_three())},
                {Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_four()),
                        Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_four())}};

        Localization.launchTrilateration(positions,distancesBeacon1,1);
        Localization.launchTrilateration(positions,distancesBeacon2,2);
        Localization.launchTrilateration(positions,distancesBeacon3,3);
    }

    public boolean dataComplete(double[] array){
        for(int i=0;i<array.length;i++){
            if(array[i]==0.0){
                return false;
            }
        }
        return true;
    }

    public float calcDist(Long[] result){
        short RSSI;
        float ratio;
        float distance;

        RSSI=(short) ((result[1] << 8) | (result[0] & 0xFF));
        ratio = RSSI*1.0f/RSSI_Init;
        if (ratio < 1.0f) {
            return distance = (float) Math.pow(ratio,10);
        }
        else {
            return distance = (float) ((0.89976)*Math.pow(ratio,7.7095) + 0.111);
        }
    }

    public void addValue(float new_value, int fixedBeacon, int beacon){

        float average = getAverage(fixedBeacon,beacon);

        if((new_value - average <= 2 &&  new_value - average >= -2) || Arrays.asList(arrayAverage[fixedBeacon][beacon]).contains(0)){
            float temp;
            for(int i = 18; i > -1; i--){
                temp=arrayAverage[fixedBeacon][beacon][i];
                arrayAverage[fixedBeacon][beacon][i + 1]=temp;
            }
            arrayAverage[fixedBeacon][beacon][0]=new_value;
            setAverage(fixedBeacon,beacon);
        }
    }

    public void setAverage(int fixedBeacon, int beacon){
        float average = 0f;
        for(int i = 0;i<20;i++){
            average += arrayAverage[fixedBeacon][beacon][i];
        }
        arrayAverage[fixedBeacon][beacon][20]=average/20;
    }

    public float getAverage(int fixedBeacon, int beacon){
        return arrayAverage[fixedBeacon][beacon][20];
    }
}
