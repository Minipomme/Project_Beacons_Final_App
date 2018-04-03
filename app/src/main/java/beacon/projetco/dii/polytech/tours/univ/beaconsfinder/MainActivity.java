package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView adminCard, gameCard, languageCard, day_nightCard, infosCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Defining Cards
        adminCard = findViewById(R.id.admin_card);
        gameCard = findViewById(R.id.game_card);
        languageCard = findViewById(R.id.language_card);
        day_nightCard = findViewById(R.id.day_night_card);
        infosCard = findViewById(R.id.infos_card);

        // Add Click Listener to the cards
        adminCard.setOnClickListener(this);
        gameCard.setOnClickListener(this);
        languageCard.setOnClickListener(this);
        day_nightCard.setOnClickListener(this);
        infosCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i ;

        switch (view.getId()) {
            case R.id.admin_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.game_card : i = new Intent(this, GameActivity.class); startActivity(i); break;
            case R.id.language_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.day_night_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.infos_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            default:break;
        }
    }
}
