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
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner scanner;
    private BluetoothDevice device;
    private BluetoothGatt gatt;
    private String uuidService="19B10010-E8F2-537E-4F6C-D104768A1214";
    private BluetoothGattCharacteristic characteristic;
    private MapActivity currentActivity;
    private DataManager dataManager;
    private boolean stopThread=false;

    private ScanResult scanResult;
    private int onConnectionStateChangeStatus;
    private int onServicesDiscoveredStatus;
    private int onCharacteristicReadStatus;

    private boolean flagOnScanResult=false;
    private boolean flagOnConnectionStateChange=false;
    private boolean flagOnServicesDiscovered=false;
    private boolean flagOnCharacteristicRead=false;
    private boolean flagOnCharacteristicChanged=false;

    private boolean flagState1=false;
    private boolean flagState2=false;
    private boolean flagState3=false;
    private boolean flagState4=false;

    // Storage Permissions
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public BleManager(MapActivity currentActivity){
        this.currentActivity=currentActivity;

        if(ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(currentActivity, PERMISSIONS_LOCATION, REQUEST_ACCESS_COARSE_LOCATION);
        }
        statusCheck();

        bluetoothManager = (BluetoothManager) currentActivity.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null) {
            //Handle this issue. Report to the user that the device does not support BLE
        } else {
            adapter = bluetoothManager.getAdapter();
        }

        adapter.disable();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!adapter.isEnabled()){
            adapter.enable();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("BLE on!");
        }
        while(!adapter.isEnabled());
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) currentActivity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage("Votre GPS est semble être désactivé, voulez-vous l'activer ?")
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

    @Override
    public void run() {
        while(!adapter.isEnabled());
        startScanning();
        while(!stopThread){
            if(flagOnScanResult){
                doConnect(scanResult);
                flagOnScanResult=false;
                flagState1=true;
            }
            if(flagOnConnectionStateChange && flagState1){
                discoverServices(onConnectionStateChangeStatus);
                flagOnConnectionStateChange=false;
                flagState1=false;
                flagState2=true;
            }
            if(flagOnServicesDiscovered && flagState2){
                getCharacteristic(onServicesDiscoveredStatus);
                flagOnServicesDiscovered=false;
                flagState2=false;
                flagState3=true;
            }
            if(flagOnCharacteristicRead && flagState3){
                setDescriptor(onCharacteristicReadStatus);
                flagOnCharacteristicRead=false;
                flagState3=false;
                flagState4=true;
            }
            if(flagOnCharacteristicChanged && flagState4){
                getData();
                flagOnCharacteristicChanged=false;
                flagState4=false;
            }
        }
    }

    public void pleaseStop() {stopThread=false;}

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

    public class MyScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            if(result.getDevice()!=null){
                Log.d("TEST",result.getDevice().getName());
            }
            scanResult=result;
            flagOnScanResult=true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class myGattCallBack extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("TEST","onConnectionStateChange");
            onConnectionStateChangeStatus=newState;
            flagOnConnectionStateChange=true;
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("TEST","onServicesDiscovered");
            onServicesDiscoveredStatus=status;
            flagOnServicesDiscovered=true;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("TEST","onCharacteristicRead");
            onCharacteristicReadStatus=status;
            flagOnCharacteristicRead=true;
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("TEST","onCharacteristicChanged");
            //Log.d("TEST",bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).toString());
            flagOnCharacteristicChanged=true;
        }
    }

    public void doConnect(ScanResult result){
        device = adapter.getRemoteDevice(result.getDevice().getAddress());
        gatt = device.connectGatt(currentActivity.getApplicationContext(), false, new myGattCallBack());
    }

    public void discoverServices(int newState){
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i("TEST", "Connected to GATT peripheral. Attempting to start service discovery");
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i( "TEST", "Disconnected from GATT peripheral" );
        }
    }

    public void getCharacteristic(int status){
        if (status == BluetoothGatt.GATT_SUCCESS) {
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                if (service.getUuid().toString().equalsIgnoreCase(uuidService)) {
                    characteristic = service.getCharacteristics().get(0);
                    gatt.readCharacteristic(characteristic);
                }
            }
        } else {
            Log.i("TEST", "onServicesDiscovered received: " + status);
        }
    }

    public void setDescriptor(int status){
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gatt.setCharacteristicNotification(characteristic,true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    public void getData(){
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
        if(dataManager==null){
            dataManager = new DataManager(currentActivity,scanner);
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
}
