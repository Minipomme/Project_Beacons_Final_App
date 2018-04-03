package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class GameActivity extends AppCompatActivity {

    String arrayName[]={"One Beacon","Multiple Beacons","Hot Cold","IDK"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        CircleMenu circleMenu = findViewById(R.id.circle_game_menu);
        circleMenu.setMainMenu(getResources().getColor(R.color.gray),R.drawable.ic_play_arrow_grey_24dp, R.drawable.ic_clear_grey_24dp)
                    .addSubMenu(getResources().getColor(R.color.deeppurple), R.drawable.ic_location_on_white_24dp)
                    .addSubMenu(getResources().getColor(R.color.yellow), R.drawable.ic_location_pin_multiple_white)
                    .addSubMenu(getResources().getColor(R.color.green), R.drawable.ic_thermometer_half_white)
                    .addSubMenu(getResources().getColor(R.color.colorAccent), R.drawable.ic_network_check_white_24dp)
                    .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                        @Override
                        public void onMenuSelected(final int index) {
                            Toast.makeText(GameActivity.this, "You selected "+arrayName[index], Toast.LENGTH_SHORT).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Intent i ;

                                    switch (index) {
                                        case 0 : i = new Intent(GameActivity.this, WIPActivity.class); startActivity(i); break;
                                        case 1 : i = new Intent(GameActivity.this, WIPActivity.class); startActivity(i); break;
                                        case 2 : i = new Intent(GameActivity.this, WIPActivity.class); startActivity(i); break;
                                        case 3 : i = new Intent(GameActivity.this, WIPActivity.class); startActivity(i); break;
                                        default:break;
                                    }
                                }
                            }, 500);   //5 seconds
                        }
                    });
    }
}
