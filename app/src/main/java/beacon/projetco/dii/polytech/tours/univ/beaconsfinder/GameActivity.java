package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ramotion.circlemenu.CircleMenuView;

public class GameActivity extends AppCompatActivity {

    String arrayName[]={"Multiple Beacons","Hot Cold"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final CircleMenuView menu = findViewById(R.id.circle_game_menu);
        menu.setEventListener(new CircleMenuView.EventListener() {
            @Override
            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, final int index) {
                Toast.makeText(GameActivity.this, "You selected "+ arrayName[index], Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent i ;

                        switch (index) {
                            case 0 : i = new Intent(GameActivity.this, MapActivity.class); startActivity(i); break;
                            case 1 : i = new Intent(GameActivity.this, MonitoringActivity.class); startActivity(i); break;
                            default:break;
                        }
                    }
                }, 500);
            }
        });
    }

    @Override
    public void onBackPressed() {
        GameActivity.this.finish();
    }
}
