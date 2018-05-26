package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that manages all the data from BLEManager. It
 */
public class DataManager {
    private MapActivity currentActivity;

    //Variables fixes
    private int NB_Arduinos;
    private int NB_Beacons;
    private float RSSI_Init=55;

    //Gestion des informations
    private boolean [] flagsArduinoAllBeacons;
    private boolean [] flagsArduinoNotAllBeacons;
    private float[][][] arrayAverage;
    private List<List<Float>> arrayArduino;

    //Gestion du BLE et des balises de détection
    private BluetoothLeScanner scanner;
    private boolean flagScan = false;

    private ParcBeacon ensembleBeacon;
    private Trilateration Localizer;

    public DataManager(MapActivity currentActivity, BluetoothLeScanner scanner){
        this.currentActivity=currentActivity;
        this.scanner=scanner;

        NB_Arduinos=Integer.parseInt(currentActivity.getApplicationContext().getString(R.string.NB_ARDUINO));
        NB_Beacons=Integer.parseInt(currentActivity.getApplicationContext().getString(R.string.NB_BEACONS));
        ensembleBeacon = new ParcBeacon(currentActivity);

        this.arrayArduino = new ArrayList<List<Float>>();
        this.arrayAverage= new float[NB_Arduinos][NB_Beacons][21];
        this.flagsArduinoAllBeacons = new boolean[NB_Arduinos];
        this.flagsArduinoNotAllBeacons = new boolean[NB_Arduinos];

        for (int i = 0; i <= NB_Arduinos - 1; i++) {
            arrayArduino.add(new ArrayList<Float>());
            for (int j = 0; j <= NB_Beacons - 1; j++) {
                arrayArduino.get(i).add((float) 0);
            }
        }
    }

    /**
     * Extract data from arrayArduino to print them and calculate the new distances
     * @param result
     * @param data
     */
    public void extractData(Long[] result,byte[] data){
        float distance;
        distance = calcDist(result);

        addValue(distance,data[3]-1,data[2]-1);
        arrayArduino.get(data[3]-1).set(data[2]-1,getAverage(data[3]-1,data[2]-1));

        Log.e("RESULT","---------------------------------------------");
        Log.e("RESULT", "[Distances Arduino 1] : "+arrayArduino.get(0));
        Log.e("RESULT", "[Distances Arduino 2] : "+arrayArduino.get(1));
        Log.e("RESULT", "[Distances Arduino 3] : "+arrayArduino.get(2));
        Log.e("RESULT", "[Distances Arduino 4] : "+arrayArduino.get(3));
        Log.e("RESULT","---------------------------------------------");

        //Setting arduino distance
        for(Beacon bcn : ensembleBeacon.getBeaconsToFind()){
            bcn.setDistances(new double[] {
                    arrayArduino.get(0).get(bcn.getName()-1),
                    arrayArduino.get(1).get(bcn.getName()-1),
                    arrayArduino.get(2).get(bcn.getName()-1),
                    arrayArduino.get(3).get(bcn.getName()-1)
            } );
        }

        //Setting flags for arduino
        int count;
        for(int i=0;i<NB_Arduinos;i++){
            count = dataComplete(arrayArduino.get(i));
            if(count > 0) {
                if(count == NB_Beacons) {
                    flagsArduinoAllBeacons[i] = true;
                    flagsArduinoNotAllBeacons[i] = false;
                } else {
                    flagsArduinoAllBeacons[i] = false;
                    flagsArduinoNotAllBeacons[i] = true;
                }
            } else {
                flagsArduinoAllBeacons[i] = false;
                flagsArduinoNotAllBeacons[i] = false;
            }
        }

        currentActivity.runOnUiThread(new Runnable() {
            public void run() {
                ContextThemeWrapper wrapper;
                if(flagsArduinoAllBeacons[0]){
                    wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon1_ON);
                }
                else{
                    if(flagsArduinoNotAllBeacons[0]) {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon1_NotAll);
                    } else {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon1_OFF);
                    }
                }
                currentActivity.changeTheme(wrapper.getTheme(), currentActivity.getFixedBeaconOne(), R.drawable.ic_number_one_in_a_circle);

                if(flagsArduinoAllBeacons[1]){
                    wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon2_ON);
                }
                else{
                    if(flagsArduinoNotAllBeacons[1]) {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon2_NotAll);
                    } else {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon2_OFF);
                    }
                }
                currentActivity.changeTheme(wrapper.getTheme(), currentActivity.getFixedBeaconTwo(), R.drawable.ic_number_two_in_a_circle);

                if(flagsArduinoAllBeacons[2]){
                    wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon3_ON);
                }
                else{
                    if(flagsArduinoNotAllBeacons[2]) {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon3_NotAll);
                    } else {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon3_OFF);
                    }
                }
                currentActivity.changeTheme(wrapper.getTheme(), currentActivity.getFixedBeaconThree(), R.drawable.ic_number_three_in_a_circle);

                if(flagsArduinoAllBeacons[3]){
                    wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon4_ON);
                }
                else{
                    if(flagsArduinoNotAllBeacons[3]) {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon4_NotAll);
                    } else {
                        wrapper = new ContextThemeWrapper(currentActivity, R.style.Fixed_Beacon4_OFF);
                    }
                }
                currentActivity.changeTheme(wrapper.getTheme(), currentActivity.getFixedBeaconFour(), R.drawable.ic_number_four_in_a_circle);
            }
        });

        if(flagsArduinoAllBeacons[0] && flagsArduinoAllBeacons[1] && flagsArduinoAllBeacons[2] && flagsArduinoAllBeacons[3] && !flagScan){
            scanner.stopScan( new ScanCallback(){});
            flagScan=true;
        }

        if(Localizer==null){
            Localizer = new Trilateration(currentActivity);
        }

        double [][] positions = new double[][]{{Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_one()),
                Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_one())},
                {Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_two()),
                        Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_two())},
                {Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_three()),
                        Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_three())},
                {Double.parseDouble(currentActivity.getPosition_x_fixed_beacon_four()),
                        Double.parseDouble(currentActivity.getPosition_y_fixed_beacon_four())}};

        for(Beacon bcn : ensembleBeacon.getBeaconsToFind()){
            Localizer.launchTrilateration(positions,bcn.getDistances(),bcn);
        }

    }

    /**
     * If the list is complete return NB_Beacons but isn't complete return count of Beacons are connected
     * @param list
     * @return
     */
    public int dataComplete(List<Float> list){
        if(list.contains(0f)){
            int count = 0;
            for(int i=0;i<NB_Beacons;i++){
                if(list.get(i) != 0.0)
                    count++;
            }
            return count;
        } else {
            return NB_Beacons;
        }
    }

    /**
     * Calculate the distance from RSSI to meters
     * @param result
     * @return
     */
    public float calcDist(Long[] result){
        short RSSI;
        float ratio;

        RSSI=(short) ((result[1] << 8) | (result[0] & 0xFF));
        ratio = RSSI*1.0f/RSSI_Init;
        if (ratio < 1.0f) {
            return (float) Math.pow(ratio,10);
        }
        else {
            return (float) ((0.89976)*Math.pow(ratio,7.7095) + 0.111);
        }
    }

    /**
     * Add the value into the tab to smooth the values. Using shift.
     * @param new_value
     * @param fixedBeacon
     * @param beacon
     */
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

    /**
     * Set the average into the tab. Corresponding to the last column of the tab.
     * @param fixedBeacon
     * @param beacon
     */
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

    public ParcBeacon getEnsembleBeacon() {
        return ensembleBeacon;
    }
}
