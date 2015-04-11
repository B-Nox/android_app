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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.droidcon.b_nox.R;
import it.droidcon.b_nox.data.ArtDetail;
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

    }




    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(ArtDetail artDetail) {
        artTitle.setText(artDetail.getTitle());
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
        artTitle = (TextView) findViewById(R.id.title_content);

        img.setOnClickListener(v -> { TransitionManager.go(scene3, new AutoTransition()); });



        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bAdapter = bluetoothManager.getAdapter();

        if (bAdapter != null) {
            BluetoothLeScanner scanner = bAdapter.getBluetoothLeScanner();

            scanner.startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Observable.just(new ArtDetail(result.toString(), "url")).subscribe
                            (MainActivity.this);
                }
            });
        }

    }
}
