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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class BleManager extends Thread{
    private MapActivity currentActivity;

    //Variables fixes
    private String uuidService="19B10010-E8F2-537E-4F6C-D104768A1214";

    //Gestion du bluetooth
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;
    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic characteristic;

    //Classe de gestion des informations
    private DataManager dataManager;

    // Storage Permissions
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    /**
     * Create the BleManager and bluetooth adapter. Verify the permissions concerning location and bluetooth
     * @param currentActivity
     */
    public BleManager(MapActivity currentActivity){
        this.currentActivity=currentActivity;

        if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(currentActivity, PERMISSIONS_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
        }
        statusCheck();

        bluetoothManager = (BluetoothManager) currentActivity.getApplicationContext().getSystemService( Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            //Handle this issue. Report to the user that the device does not support BLE
        } else {
            adapter = bluetoothManager.getAdapter();
        }

        if (adapter != null && !adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            currentActivity.startActivityForResult(intent,1);
        } else {
            System.out.println("BLE on!");
        }

        dataManager = new DataManager(currentActivity,scanner);
        Log.e("Test julien","Creation du BLEManager");
    }


    /**
     * Run the Thread and start scanning. Do nothing if the bluetooth isn't active
     */
    @Override
    public void run() {
        while(!adapter.isEnabled());
        startScanning();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    /**
     * Advertise the user if the location isn't active
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage("Votre GPS semble être désactivé, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        currentActivity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        System.exit(1);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Start scanning with filters corresponding to the services sent by the arduino(s)
     */
    public void startScanning(){
        scanner = adapter.getBluetoothLeScanner();
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino1").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino2").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino3").build());
        scanFilters.add(new ScanFilter.Builder().setDeviceName("ScanBeaconsbyArduino4").build());
        scanner.startScan(scanFilters, scanSettings, new MyScanCallback());
        System.out.println("Scanner on !");
    }

    /**
     * Callback that read the characteristics into the service
     */
    public class MyScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            Log.e("TEST",result.getDevice().getName());
            device = adapter.getRemoteDevice(result.getDevice().getAddress());
            gatt = device.connectGatt(currentActivity.getApplicationContext(), true, new myGattCallBack());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            //Do something with batch of results
        }

        @Override
        public void onScanFailed(int errorCode) {
            //Handle error
        }
    }

    private class myGattCallBack extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("TEST","onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("TEST", "Connected to GATT peripheral. Attempting to start service discovery");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("TEST", "Disconnected from GATT peripheral");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("TEST","onServicesDiscovered");
            List<BluetoothGattService> services = gatt.getServices();
            for(BluetoothGattService service: services){
                if(service.getUuid().toString().equalsIgnoreCase(uuidService)){
                    characteristic=service.getCharacteristics().get(0);
                    gatt.readCharacteristic(characteristic);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("TEST","onCharacteristicRead");
            gatt.setCharacteristicNotification(characteristic,true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("TEST","onCharacteristicChanged");

            Long[] result = {0l,0l,0l,0l};
            byte[] data = characteristic.getValue();
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

    public DataManager getDataManager() {
        return dataManager;
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
}
