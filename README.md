[![Coverage Status](https://coveralls.io/repos/github/elaInnovation/Android-Blue-Tool-Box/badge.svg?branch=master)](https://coveralls.io/github/elaInnovation/Android-Blue-Tool-Box?branch=master)
[![Build Status](https://travis-ci.com/elaInnovation/Android-Blue-Tool-Box.svg?branch=master)](https://travis-ci.com/elaInnovation/Android-Blue-Tool-Box)


# Android BlueTool Box
ELA Innovation provide this project for Android Studio to help users to intergrate easily the Tags provide by ELA Innovation Company. You can directly clone this project from Github. This project contains the code of a simple app to manage Bluetooth scanner for your mobile project and use Gatt and Services to use the connected mode from our Bluetooth tag. You can found the app on the Play Store : https://play.google.com/store/apps/details?id=com.ela.deviceadvertisingmobile

# Build
Before starting, please download Android Studio and install it. Then, to build the application, open the solution file using Android Studio and generate the solution. You can use your own phone

## Requirements
This app is build for Android 5 and above. Note that this app could not work on old phones. The phone must support BLE.
Only ELA BLE tags form ELA Innovation can work, other BLE devices will be not recognized by the app

## Code
### Scanner BLE
To start the Scanner, you have to get the BLE adapter of your phone and handle this. You also have to intent this to get event from the adapter. To use the ELA scanner don't forget to instantiate the adapter to the BlueScanner instance.

```java
  btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
  btAdapter = btManager.getAdapter();
  btScanner = btAdapter.getBluetoothLeScanner();

  if (btAdapter != null && !btAdapter.isEnabled()) {
      Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
  }
  BlueScanner.getInstance().setBtScanner(this,btScanner);
```

To start the scanner, use the scanner instance BlueScanner. Events are automatically subscribe to the instance
```
  BlueScanner.getInstance().startScanning();
```

Stop the scanner by calling the method : stopScanning()
```
  BlueScanner.getInstance().disconnect();
```

### Connect
To connect to an ELA Innovation Tag, you need to know the target tag. Then, you can create use the instance of BlueScanner and the conenct function to connect to the tag. 
```
 BlueScanner.getInstance().connectToTag(getContext(),tagSelected,tagStateConnection);
```

After connection you have access to some commands to enable or disable LED, BUZZER or some other stuffs. An exemple below to power on the LED on the tag.
```
 BlueScanner.getInstance().sendData("LED_ON");
```

When all operations is finished, disconnect from the tag with the disconnect method from BlueScanner
```
  BlueScanner.getInstance().disconnect();
```

### Sensors values
To decode advertising values from our tags, a couple of functions are in the tag factory. You can access them on the BleFactory file. Here, is an example of a BLUE tag Temperature.

```java
  TagTemperature tagT = (TagTemperature) tag;
  tagT.updateData(rawData, data);
  this.tagList.put(rawData.getDevice().getName(), tag);
  this.macList.put(rawData.getDevice().getName(), rawData.getDevice().getAddress());

```




