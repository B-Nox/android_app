package it.droidcon.b_nox.activities;

import android.app.Activity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.droidcon.b_nox.R;
import rx.Observer;


public class MainActivity extends Activity implements Observer {

    @InjectView(R.id.container)
    ViewGroup container;
    private Scene scene2;
    private Scene scene1;
    private View decorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decorView = getWindow().getDecorView();

        ButterKnife.inject(this);

        scene1 = Scene.getSceneForLayout(container, R.layout.scene_main_activity_intro, this);
        scene2 = Scene.getSceneForLayout(container, R.layout.scene_main_activity_main, this);


        scene1.enter();
        container.postDelayed(() -> {

            TransitionSet set = new TransitionSet();
            set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

            set.addTransition(new Fade())
                    .addTransition(new ChangeBounds());
            TransitionManager.go(scene2, set);
        }, 2000);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Object o) {

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
}
