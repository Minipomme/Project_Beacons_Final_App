#include <CurieBLE.h>

//Number of the arduino to change
int NumArduino=1;
int NumBeacon=0;
unsigned long data=0;

//Name of the service to change
char serviceID[40]="ScanBeaconsbyArduino1";

int flagConnected=0;
unsigned long time;

BLEService beaconService ("19B10010-E8F2-537E-4F6C-D104768A1214"); // create service
BLEIntCharacteristic dataCharacteristic("19B10012-E8F2-537E-4F6C-D104768A1214", BLERead | BLENotify);

void setup()
{
  //Set of the tx power (from arduino to peripherals)
  BLE.setTxPower(4);

  Serial.begin(9600);
  // BLE object is actually now defined in CurieBLE.h
  BLE.begin();

  Serial.println("BLE Central scan");
  Serial.println(serviceID);
  
  // When device is discovered handle centralDiscovered() is called.
  BLE.setEventHandler(BLEDiscovered, centralDiscovered);
  //Log to change
  Serial.println("Arduino 1 starts scanning");
  BLE.scan(true);

  // set the local name peripheral advertises
  BLE.setLocalName(serviceID);
  // set the UUID for the service this peripheral advertises:
  BLE.setAdvertisedService(beaconService);

  // add the characteristics to the service
  beaconService.addCharacteristic(dataCharacteristic);

  // add the service
  BLE.addService(beaconService);
  dataCharacteristic.setValue(0);

  // start advertising
  BLE.advertise();
  //Fin du test
}

void loop() 
{
  BLE.poll(); // This is BLE pulling loop. 
  //Program which diconnect the arduino from the central in 10 seconds
  /*if(BLE.central() && flagConnected==0) {
    time = millis();
    flagConnected=1;
  }
  
  if(BLE.central() && (millis()-time>10000)){
    BLE.disconnect();
    time=0;
    flagConnected=0;
  }*/  
}

void centralDiscovered(BLEDevice peripheral) 
{
  //If the arduino detect a new peripheral
  if (peripheral) {
    if(peripheral.hasLocalName()){
      // print the manufacturer data
      if (peripheral.hasManufacturerData()) {
        unsigned char manu_data[255];
        unsigned char manu_data_length;

        bool success = peripheral.getManufacturerData (manu_data, manu_data_length);
        
        if (success){
          int i;
          char* buf_str = (char*) malloc (2*manu_data_length + 1);
          char* buf_ptr = buf_str;
          char* uuidBeacon = (char*) malloc (2*manu_data_length + 1);
          
          bzero(uuidBeacon,2*manu_data_length + 1);
          
          for (i = 0; i < manu_data_length; i++){
              buf_ptr += sprintf(buf_ptr, "%02X", (unsigned char)manu_data[i]);
          }
          *(buf_ptr + 1) = '\0';

          //Extracting of the UUID from the manufacturer data
          extractFromString(uuidBeacon,buf_str);
  
          if(buf_str[0]=='4' && buf_str[1]=='C'){        
              // print the local name, if present
              Serial.print("----------------------START SCAN----------------------\n");
              //Print the UUID
              Serial.print("UUID : ");
              Serial.println(uuidBeacon);
              Serial.print("\n");
              NumBeacon=peripheral.localName()[6]-48;
              Serial.print("Beacon Number : ");
              Serial.println(NumBeacon);
              Serial.print("\n");
              
              // print the RSSI
              Serial.print("RSSI : ");
              Serial.println(peripheral.rssi());
              Serial.print("\n");
              Serial.print("Characteristic : ");
              data=(NumArduino<<24)+(NumBeacon<<16)+((short)(peripheral.rssi()*(-1)));
              Serial.print(data);
              dataCharacteristic.setValue(data);
              Serial.print("\n");
              Serial.print("----------------------END SCAN----------------------\n");            
          }          
          free(buf_str);
          free(uuidBeacon);
        }
      }
    }
  }
}

void extractFromString(char* dest, char* src)
{
   int i=0;
   int j=0;
   *dest = 0;
   while(src[i] != '\0')
   {
      if(i>=8 && i<40){
        dest[j]=src[i];
        j++;
      }
      else if(i>=40){
        break;
      }
      i++;  
   }
}
