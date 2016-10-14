package peetme.app.trunimal.com.peetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_out);

        final ImageView iv = (ImageView) findViewById(R.id.logo_splash_screen);
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_splash);

        iv.startAnimation(an);
        //rl.startAnimation(an);

        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent i = new Intent(SplashActivity.this, AnimalMapsActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
