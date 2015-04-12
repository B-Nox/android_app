package it.droidcon.b_nox.utils;

import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.List;

import it.droidcon.b_nox.activities.MainActivity;
import it.droidcon.b_nox.data.ArtDetail;

/**
 * Created by erinda on 4/12/15.
 */
public class ScanCallback extends android.bluetooth.le.ScanCallback{

    private ScanResult currentDevice;
    MainActivity act;

    public ScanCallback(MainActivity act){
        this.act = act;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        final List<String> lsbeacon = Constants.BEACONS;

        if (currentDevice != null) {

            if (lsbeacon.contains(currentDevice.getDevice().toString())) {

                if (lsbeacon.contains(result.getDevice().toString()) && (result.getRssi() < Math.abs(currentDevice.getRssi())) &&
                        !(result.getDevice().getAddress().equals(currentDevice.getDevice().getAddress()))) {

                    Log.i("NEW BLE", "Old: " + currentDevice.getDevice().toString() + " " + currentDevice.getRssi() +
                            " New: " + result.getDevice().toString() + " " + result.getRssi());

                    onNewDevice(result);
                }
            } else if (lsbeacon.contains(result.getDevice().toString())) {
                Log.i("NEW BLE", "Old: " + currentDevice.getDevice().toString() + " " + currentDevice.getRssi() +
                        " New: " + result.getDevice().toString() + " " + result.getRssi());

                onNewDevice(result);
            }
        } else if (lsbeacon.contains(result.getDevice().toString()) ){
            Log.i("NEW BLE", "New: " + result.getDevice().toString() + " " + result.getRssi());
            onNewDevice(result);
        }
    }


    private void onNewDevice(ScanResult device) {

        currentDevice = device;
        act.currentDetail = new ArtDetail(act, currentDevice.getDevice().toString(), Constants.SERVER_ADDRESS);

    }

}
