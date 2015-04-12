package it.droidcon.b_nox.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.droidcon.b_nox.R;
import it.droidcon.b_nox.data.ArtDetail;
import it.droidcon.b_nox.utils.Constants;

public class DetailActivity extends Activity {

    private View decorView;
    private ArtDetail detail;

    @InjectView(R.id.title_content)
    protected TextView title;
    @InjectView(R.id.img)
    protected ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        getActionBar().hide();
        setContentView(R.layout.activity_detail);

        ButterKnife.inject(this);

        decorView = getWindow().getDecorView();
        onWindowFocusChanged(true);
        Intent i = getIntent();

        detail = new ArtDetail();

        detail.title = i.getStringExtra(MainActivity.DETAIL_EXTRA_TITOLO);
        detail.audio = i.getStringExtra(MainActivity.DETAIL_EXTRA_AUDIO);
        detail.image = i.getStringExtra(MainActivity.DETAIL_EXTRA_IMAGE);

        title.setText(detail != null || detail.title.isEmpty() ? "COOKIE JAR!" : detail.title);

        Picasso.with(this).load(Constants.SERVER_ADDRESS+"/"+detail.image)
                .fit().into(img);

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


    public void back(View v){
        finishAfterTransition();
    }
}
