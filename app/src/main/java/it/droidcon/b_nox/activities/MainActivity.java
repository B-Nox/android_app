package it.droidcon.b_nox.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.droidcon.b_nox.R;
import it.droidcon.b_nox.data.ArtDetail;
import it.droidcon.b_nox.data.FilesDownloader;
import rx.Observer;


public class MainActivity extends Activity implements Observer<ArtDetail> {

    @InjectView(R.id.container)
    ViewGroup container;

    private View decorView;

    private Scene scene2;
    private Scene scene1;
    private Scene scene3;
    private ImageView img;
    private TextView artTitle;

	private ArtDetail currentDetail;
	private ScanResult currentDevice;
	private String serverAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		this.currentDevice = null;
		this.serverAddress = "http://192.168.23.254";

        decorView = getWindow().getDecorView();

        ButterKnife.inject(this);

        scene1 = Scene.getSceneForLayout(container, R.layout.scene_main_activity_intro, this);
        scene2 = Scene.getSceneForLayout(container, R.layout.scene_main_activity_main, this);
        scene3 = Scene.getSceneForLayout(container, R.layout.scene_main_activity_loading, this);


        scene1.enter();
        container.postDelayed(() -> {

            TransitionSet set = new TransitionSet();
            set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

            set.addTransition(new Fade())
                    .addTransition(new ChangeBounds());
            TransitionManager.go(scene2, set);
        }, 2000);


        scene2.setEnterAction(this::setUpBluetoothObserver);

    }


    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(ArtDetail artDetail) {
        Log.i("NEXT", "onnext");
        artTitle = (TextView) findViewById(R.id.title_content);
        artTitle.setText(artDetail.title.substring(9));
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    private void setUpBluetoothObserver() {
        img = (ImageView) findViewById(R.id.img);

        img.setOnClickListener(v -> { TransitionManager.go(scene3, new AutoTransition()); });

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bAdapter = bluetoothManager.getAdapter();

        if (bAdapter != null) {
            BluetoothLeScanner scanner = bAdapter.getBluetoothLeScanner();

            Log.i("ASD", "setup scan");

            scanner.startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {

					if (currentDevice != null) {

						if(currentDevice.getDevice().getAddress().startsWith("00:07:80")) {

							if(result.getDevice().getAddress().startsWith("00:07:80") && (result.getRssi() > currentDevice.getRssi()) &&
								!(result.getDevice().getAddress().equals(currentDevice.getDevice().getAddress()))) {

								Log.i("NEW BLE", "Old: " + currentDevice.getDevice().toString() + " " + currentDevice.getRssi() +
										" New: " + result.getDevice().toString() + " " + result.getRssi());

								onNewDevice(result);
							}
						}
						else if(result.getDevice().getAddress().startsWith("00:07:80")) {
								Log.i("NEW BLE", "Old: " + currentDevice.getDevice().toString() + " " + currentDevice.getRssi() +
										" New: " + result.getDevice().toString() + " " + result.getRssi());

								onNewDevice(result);
						}
					}
					else if(result.getDevice().getAddress().startsWith("00:07:80")) {
						Log.i("NEW BLE", "New: " + result.getDevice().toString() + " " + result.getRssi());
						onNewDevice(result);
					}
                }
            });
        }

    }

	private void onNewDevice(ScanResult device) {

		currentDevice = device;
		currentDetail = new ArtDetail(this, currentDevice.getDevice().toString(), this.serverAddress);

	}

	public void onDetailLoaded() {

		FilesDownloader downloader;

		/* Fill the interface with the new data */

		for(int i=0; i<currentDetail.images.length; i++) {
			downloader = new FilesDownloader(this.getApplicationContext(), this,
					this.currentDetail.images[i][0], "image");
			Log.i("DL", "Starting download of " + this.serverAddress + "/" + this.currentDetail.images[i][0]);
			downloader.execute(this.serverAddress + "/" + this.currentDetail.images[i][0]);
		}

		for(int i=0; i<currentDetail.audios.length; i++) {
			downloader = new FilesDownloader(this.getApplicationContext(), this,
					this.currentDetail.audios[i][0], "audio");
			Log.i("DL", "Starting download of " + this.serverAddress + "/" + this.currentDetail.audios[i][0]);
			downloader.execute(this.serverAddress + "/" + this.currentDetail.audios[i][0]);
		}

		for(int i=0; i<currentDetail.videos.length; i++) {
			downloader = new FilesDownloader(this.getApplicationContext(), this,
					this.currentDetail.videos[i][0], "video");
			Log.i("DL", "Starting download of " + this.serverAddress + "/" + this.currentDetail.videos[i][0]);
			downloader.execute(this.serverAddress + "/" + this.currentDetail.videos[i][0]);
		}

	}

    public void onImageDownload(String filePath) {
		Log.i("DL", "Download completed of " + filePath);
    }

    public void onAudioDownload(String filePath) {
		Log.i("DL", "Download completed of " + filePath);
    }

    public void onVideoDownload(String filePath) {
		Log.i("DL", "Download completed of " + filePath);
    }


}
