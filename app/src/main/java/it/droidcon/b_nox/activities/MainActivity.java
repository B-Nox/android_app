package it.droidcon.b_nox.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
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

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.droidcon.b_nox.R;
import it.droidcon.b_nox.data.ArtDetail;
import it.droidcon.b_nox.utils.Constants;
import rx.Observable;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        scene3.setEnterAction(this::setUpScene3ClickHandler);
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
        Log.i("NEXT", artDetail+"");
        Log.i("NEXT", artTitle+"");
        if (artTitle!=null) {
            artTitle.setText(artDetail.getTitle().substring(9));
            artTitle.postInvalidate();
        }
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
                    if (Constants.BEACONS.contains(result.getDevice().toString())) {

                        Observable.just(new ArtDetail(result.getDevice().toString(), "url")).subscribe
                                (MainActivity.this);
                    }

                    Log.i("SCAN", result.getDevice().toString() + "" + result.getRssi());
                }
            });
        } else {
            Observable.timer(5, 4, TimeUnit.SECONDS).subscribe(lo -> {
                String mac = Constants.BEACONS.get(lo.intValue() % Constants.BEACONS.size());
                Log.i("FAKE MAC", mac);
                Observable.just(new ArtDetail(mac, "url")).subscribe(MainActivity.this);
            });


        }
    }


    private void setUpScene3ClickHandler() {
        ImageView img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(v ->{this.transitionToDetailActivity(img);});
    }
    private void transitionToDetailActivity(final View v) {
        Intent intent = new Intent(this, DetailActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, v, "image");
        startActivity(intent, options.toBundle());
    }

}
