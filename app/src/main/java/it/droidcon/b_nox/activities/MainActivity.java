package it.droidcon.b_nox.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.droidcon.b_nox.R;
import it.droidcon.b_nox.data.ArtDetail;
import it.droidcon.b_nox.data.FilesDownloader;
import it.droidcon.b_nox.utils.Constants;


public class MainActivity extends Activity {

    @InjectView(R.id.container)
    ViewGroup container;

    private View decorView;

    private Scene scene2;
    private Scene scene1;
    private Scene scene3;
    private ImageView img;
    private TextView artTitle;


    public ArtDetail currentDetail = null;
    public final static String DETAIL_EXTRA_TITOLO = "TITOLO";
    public final static String DETAIL_EXTRA_IMAGE = "IMAGE";
    public final static String DETAIL_EXTRA_DESC = "DESCRIPTION";
    public final static String DETAIL_EXTRA_AUDIO = "AUDIO";

    private STATE currentState = STATE.INTRO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getActionBar().hide();
        setContentView(R.layout.activity_main);


        decorView = getWindow().getDecorView();
        onWindowFocusChanged(true);

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

            currentState = STATE.MAIN_LOGO;

        }, 2000);


        scene2.setEnterAction(this::setUpBluetoothObserver);
        scene3.setEnterAction(this::setUpScene3ClickHandler);
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

    private void transitionToLoading() {
        currentState = STATE.LOADING_OPERA;
        TransitionManager.go(scene3, new AutoTransition());
        reset_opera_info();
    }

    private void reset_opera_info() {
        Picasso.with(this)
                .load(Constants.SERVER_ADDRESS + "/" + currentDetail.image)
                .fit().into((ImageView) findViewById(R.id.img));
        ((TextView) findViewById(R.id.title_content)).setText(currentDetail.title);
        Log.i("RESET", currentDetail.image + " --  " + currentDetail.title);
    }

    private void setUpBluetoothObserver() {
        img = (ImageView) findViewById(R.id.img);

        img.setOnClickListener(v -> transitionToLoading());

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bAdapter = bluetoothManager.getAdapter();

        if (bAdapter != null) {
            BluetoothLeScanner scanner = bAdapter.getBluetoothLeScanner();

            Log.i("ASD", "setup scan");


            scanner.startScan(new it.droidcon.b_nox.utils.ScanCallback(this));
        }

    }


    private void setUpScene3ClickHandler() {
        ImageView img = (ImageView) findViewById(R.id.img);
        img.setOnClickListener(v -> {this.transitionToDetailActivity(img);});
    }

    private void transitionToDetailActivity(final View v) {
        Intent intent = new Intent(this, DetailActivity.class);

        if (currentDetail != null) {
            intent.putExtra(DETAIL_EXTRA_TITOLO, currentDetail.title);
            intent.putExtra(DETAIL_EXTRA_AUDIO, currentDetail.audio);
            intent.putExtra(DETAIL_EXTRA_IMAGE, currentDetail.image);
            intent.putExtra(DETAIL_EXTRA_DESC, currentDetail.description);
        }

        artTitle = (TextView) findViewById(R.id.title_content);

        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this,
                        new Pair(v, "image"),
                        new Pair(artTitle, "title"));
        startActivity(intent, options.toBundle());
    }


    public void onDetailLoaded() {
        Log.i("onDetailLoaded", "Loaded!");

        if (currentState.equals(STATE.MAIN_LOGO)) {
            Log.i("DUNOOO", "TRANS");
            transitionToLoading();
        } else if (currentState.equals(STATE.LOADING_OPERA)) {
            Log.i("DUNOOO", "RESET");
            reset_opera_info();
        }

        FilesDownloader downloader;

		/* Fill the interface with the new data */


        downloader = new FilesDownloader(this.getApplicationContext(), this,
                this.currentDetail.audio, "audio");
        Log.i("DL", "Starting download of " + Constants.SERVER_ADDRESS + "/" + this.currentDetail.audio);
        downloader.execute(Constants.SERVER_ADDRESS + "/" + this.currentDetail.audio);

    }

    public void onAudioDownload(String filePath) {
        Log.i("DL", "Download completed of " + filePath);
    }


    enum STATE {
        INTRO, MAIN_LOGO, LOADING_OPERA;
    }


}
