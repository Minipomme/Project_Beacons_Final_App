package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BLEManager : manage the BLE (thread)
 */
public class BleManager extends Thread{
    private BluetoothManager bluetoothManager;
    /**adapter : object which allow to do Bluetooth operations*/
    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;
    private BluetoothDevice device;
    /**gatt : object which allow to communicate with GATT.
     * GATT : communication with services and characteristics*/
    private BluetoothGatt gatt;
    private BluetoothGatt OnConnectionStateChangeGatt;
    private BluetoothGatt OnServicesDiscoveredGatt;
    private BluetoothGatt OnCharacteristicReadGatt;

    private MyScanCallback scanCallback;

    private ScanResult scanResult;

    /**uuidService : UUID of the fixedBeacons services*/
    private String uuidService="19B10010-E8F2-537E-4F6C-D104768A1214";
    private MapActivity currentActivity;
    private DataManager dataManager;

    private int onConnectionStateChangeStatus;
    private int onServicesDiscoveredStatus;
    private int onCharacteristicReadStatus;

    private BluetoothGattCharacteristic onCharactericticReadCharacteristic;
    private BluetoothGattCharacteristic onCharactericticChangedCharacteristic;

    /**Flags of the state machine*/
    private boolean flagOnScanResult=false;
    private boolean flagOnConnectionStateChange=false;
    private boolean flagOnServicesDiscovered=false;
    private boolean flagOnCharacteristicRead=false;
    private boolean flagOnCharacteristicChanged=false;
    private boolean stopThread=false;
    private boolean flagState1=false;
    private boolean flagState2=false;

    // Storage Permissions
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public BleManager(final MapActivity currentActivity){
        this.currentActivity=currentActivity;

        /**Ask for the access to the location*/
        if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(currentActivity, PERMISSIONS_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
        }
        /**Enable the Bluetooth*/
        bluetoothManager = (BluetoothManager) currentActivity.getApplicationContext().getSystemService( Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            //Handle this issue. Report to the user that the device does not support BLE
        } else {
            adapter = bluetoothManager.getAdapter();
        }
        verifyBluetoothAndLocation();

        /**dataManager allow to make some treatments on the read data*/
        scanCallback=new MyScanCallback();
        if(dataManager==null){
            dataManager = new DataManager(this.currentActivity,adapter.getBluetoothLeScanner(),scanCallback);
        }
    }

    /**Method which verify if the Bluetooth and the location are enabled*/
    private void verifyBluetoothAndLocation() {
        try {
            final LocationManager manager = (LocationManager) currentActivity.getSystemService( Context.LOCATION_SERVICE );
            if (!adapter.isEnabled() || !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(currentActivity);
                builder.setTitle("Bluetooth and / or GPS disabled");
                builder.setMessage("Enable the bluetooth and the GPS then restart the application");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        currentActivity.finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        }
        catch (RuntimeException e) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(currentActivity);
            builder.setTitle("BLE not available");
            builder.setMessage("Sorry, this device doesn't support the BLE");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    System.exit(0);
                }

            });
            builder.show();
        }

    }

    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e("TEST", "An exception occured while refreshing device");
        }
        return false;
    }

    /**Method executed at the starting of the thread which manage the communication and the treatment*/
    @Override
    public void run() {
        while(!adapter.isEnabled());
        startScanning();
        /**Machine à états*/
        while(stopThread!=true){
            if(flagOnScanResult){
                doConnect(scanResult);
                flagOnScanResult=false;
            }
            if(flagOnConnectionStateChange){
                discoverServices(onConnectionStateChangeStatus,OnConnectionStateChangeGatt);
                flagOnConnectionStateChange=false;
                flagState1=true;
            }
            if(flagOnServicesDiscovered && flagState1){
                getCharacteristic(onServicesDiscoveredStatus,OnServicesDiscoveredGatt);
                flagOnServicesDiscovered=false;
                flagState1=false;
                flagState2=true;
            }
            if(flagOnCharacteristicRead && flagState2){
                setDescriptor(onCharacteristicReadStatus,onCharactericticReadCharacteristic,OnCharacteristicReadGatt);
                flagOnCharacteristicRead=false;
                flagState2=false;
            }
            if(flagOnCharacteristicChanged){
                getData(onCharactericticChangedCharacteristic);
                flagOnCharacteristicChanged=false;
            }
        }
    }

    /**
     * Method called when the application exit the mapActivity
     */
    public void pleaseStop() {
        Log.e("Test","PleaseStop");
        if(gatt!=null && scanner!=null){
            scanner.stopScan(scanCallback);
            scanner=null;
            this.gatt.disconnect();
            this.gatt.close();
            this.gatt=null;
        }
        this.stopThread=true;
        Log.e("TEST", String.valueOf(this.isAlive()));
    }

    /**
     * Launcher of the scanner
     */
    public void startScanning(){
        scanner = adapter.getBluetoothLeScanner();
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino1").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino2").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino3").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino4").build());
        scanner.flushPendingScanResults(scanCallback);
        scanner.startScan(scanFilters, scanSettings,scanCallback);
        System.out.println("Scanner on !");
    }

    /**
     * Class MyScanCallback which get the ScanResults
     */
    public class MyScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            if(result.getDevice().getName()!=null){
                Log.e("TEST",result.getDevice().getName());
            }
            scanResult=result;
            flagOnScanResult=true;
        }
    }

    /**
     * Classe myGattCallBack allow to read the services and the characteristics
     */
    private class myGattCallBack extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("TEST","onConnectionStateChange");
            OnConnectionStateChangeGatt=gatt;
            onConnectionStateChangeStatus=newState;
            flagOnConnectionStateChange=true;
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("TEST","onServicesDiscovered");
            OnServicesDiscoveredGatt=gatt;
            onServicesDiscoveredStatus=status;
            flagOnServicesDiscovered=true;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("TEST","onCharacteristicRead");
            OnCharacteristicReadGatt=gatt;
            onCharacteristicReadStatus=status;
            onCharactericticReadCharacteristic=characteristic;
            flagOnCharacteristicRead=true;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("TEST",bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).toString());
            onCharactericticChangedCharacteristic=characteristic;
            flagOnCharacteristicChanged=true;
        }
    }

    /**
     * Connection to a peripheral
     * @param result
     */
    public void doConnect(ScanResult result){
        device = adapter.getRemoteDevice(result.getDevice().getAddress());
        new Thread(new Runnable() {
            public void run() {
                gatt = device.connectGatt(currentActivity.getApplicationContext(), true, new myGattCallBack());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshDeviceCache(gatt);
            }
        }).start();
    }

    /**
     * Researching of services
     * @param newState
     * @param gatt
     */
    public void discoverServices(int newState, BluetoothGatt gatt){
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i("TEST", "Connected to GATT peripheral. Attempting to start service discovery");
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i( "TEST", "Disconnected from GATT peripheral" );
            gatt.disconnect();
        }
    }

    /**
     * Collecting of the first characteristic
     * @param status
     * @param gatt
     */
    public void getCharacteristic(int status, BluetoothGatt gatt){
        if (status == BluetoothGatt.GATT_SUCCESS) {
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                if (service.getUuid().toString().equalsIgnoreCase(uuidService)) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristics().get(0);
                    gatt.readCharacteristic(characteristic);
                }
            }
        } else {
            Log.i("TEST", "onServicesDiscovered received: " + status);
        }
    }

    /**
     * Collecting of the descriptor (describe the data of the characteristic)
     * @param status
     * @param characteristic
     * @param gatt
     */
    public void setDescriptor(int status, BluetoothGattCharacteristic characteristic, BluetoothGatt gatt){
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gatt.setCharacteristicNotification(characteristic,true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Collecting of data
     * @param characteristic
     */
    public void getData(BluetoothGattCharacteristic characteristic){
        Log.e("TEST","getData");
        Long[] result = {0l,0l,0l,0l};
        byte[] data = characteristic.getValue();
        Log.e("Data", Arrays.toString(data));
        for(int j=0;j<=data.length-1;j++){
            if(data[j]<0) {
                result[j] = (long) data[j] + 256 ;
            }
            else{
                result[j]= (long) data[j];
            }
        }
        dataManager.extractData(result,data);
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public BluetoothLeScanner getScanner() {
        return scanner;
    }

    public void setScanner(BluetoothLeScanner scanner) {
        this.scanner = scanner;
    }

    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    public boolean isStopThread() {
        return stopThread;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public MyScanCallback getScanCallback() {
        return scanCallback;
    }

    public void setScanCallback(MyScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }
}
