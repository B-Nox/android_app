package it.droidcon.b_nox.utils;

import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.droidcon.b_nox.activities.MainActivity;
import it.droidcon.b_nox.data.ArtDetail;

/**
 * Created by erinda on 4/12/15.
 */
public class ScanCallback extends android.bluetooth.le.ScanCallback{

    private String currentDevice;
    MainActivity act;

    public ScanCallback(MainActivity act){
        this.act = act;
    }

    HashMap<String, Integer> mymap = new HashMap<>();

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        Log.d("DEBUG" , result.getDevice().toString() );
        final List<String> lsbeacon = Constants.BEACONS;
        mymap.put(result.getDevice().toString(), Math.abs(result.getRssi()));

        int min = 20000;
        String nearestBeacon = null;

        for(Map.Entry<String, Integer> entry : mymap.entrySet()){
            if(min > entry.getValue()){
                min = entry.getValue();
                nearestBeacon = entry.getKey();
            }
        }

        if (currentDevice != null && nearestBeacon != null && lsbeacon.contains(nearestBeacon)) {

            if( !nearestBeacon.equals(currentDevice)) {
                Log.d("DEBUG" , "sending " + result.getDevice().toString() );
                currentDevice = result.getDevice().toString();
                onNewDevice(currentDevice);
            }
        }else if (lsbeacon.contains(nearestBeacon)){
            currentDevice = nearestBeacon;
            onNewDevice(currentDevice);
        }




//        if (currentDevice != null) {
//
//            if (lsbeacon.contains(currentDevice.getDevice().toString())) {
//
//                if (lsbeacon.contains(result.getDevice().toString()) && (result.getRssi() < Math.abs(currentDevice.getRssi())) &&
//                        !(result.getDevice().getAddress().equals(currentDevice.getDevice().getAddress()))) {
//
//                    Log.i("NEW BLE", "Old: " + currentDevice.getDevice().toString() + " " + currentDevice.getRssi() +
//                            " New: " + result.getDevice().toString() + " " + result.getRssi());
//
//                    onNewDevice(result);
//                }
//            } else if (lsbeacon.contains(result.getDevice().toString())) {
//                Log.i("NEW BLE", "Old: " + currentDevice.getDevice().toString() + " " + currentDevice.getRssi() +
//                        " New: " + result.getDevice().toString() + " " + result.getRssi());
//
//                onNewDevice(result);
//            }
//        } else if (lsbeacon.contains(result.getDevice().toString()) ){
//            Log.i("NEW BLE", "New: " + result.getDevice().toString() + " " + result.getRssi());
//            onNewDevice(result);
//        }
    }


    private void onNewDevice(String device) {
 
        act.currentDetail = new ArtDetail(act, device, Constants.SERVER_ADDRESS);

    }

}
