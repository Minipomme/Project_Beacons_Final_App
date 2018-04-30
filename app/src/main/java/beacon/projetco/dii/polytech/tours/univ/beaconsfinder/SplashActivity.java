package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Minipomme on 30/04/2018.
 */

public class SplashActivity extends AppCompatActivity {
    private TextView TVLoading;
    private ImageView IVLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TVLoading = findViewById(R.id.TVloading);
        IVLogo = findViewById(R.id.IVlogo);
        Animation LoadAnim = AnimationUtils.loadAnimation(this, R.anim.splash_transition);

        TVLoading.startAnimation(LoadAnim);
        IVLogo.startAnimation(LoadAnim);

        final Intent i = new Intent(this, MainActivity.class);
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };

        timer.start();
    }
}
