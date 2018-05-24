package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class GameActivity extends AppCompatActivity {

    String arrayName[]={"Multiple Beacons","Hot Cold"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        CircleMenu circleMenu = findViewById(R.id.circle_game_menu);
        circleMenu.setMainMenu(getResources().getColor(R.color.gray),R.drawable.rotate_play_arrow_grey, R.drawable.ic_clear_grey_24dp)
                    .addSubMenu(getResources().getColor(R.color.green), R.drawable.rotate_location_pin_multiple_white)
                    .addSubMenu(getResources().getColor(R.color.deeppurple), R.drawable.rotate_thermometer_half_white)
                    .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                        @Override
                        public void onMenuSelected(final int index) {
                            Toast.makeText(GameActivity.this, "You selected "+arrayName[index], Toast.LENGTH_SHORT).show();

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
                            }, 500);   //5 seconds
                        }
                    });
        circleMenu.setRotation(90);
    }
}
