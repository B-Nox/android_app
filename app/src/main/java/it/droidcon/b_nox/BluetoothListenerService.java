package it.droidcon.b_nox;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.IBinder;

import rx.Observable;

public class BluetoothListenerService extends Service{
    public BluetoothListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bAdapter = bluetoothManager.getAdapter();

        BluetoothLeScanner scanner = bAdapter.getBluetoothLeScanner();

        scanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Observable.just(result.getDevice().toString()).subscribe(s -> {System.out.println(s);} );
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

}

