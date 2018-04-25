package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
            case R.id.admin_card :
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
                final EditText mEmail = mView.findViewById(R.id.editTextEmail);
                final EditText mPassword = mView.findViewById(R.id.editTextPwd);
                Button mLogin = mView.findViewById(R.id.btnLogin);

                mLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent j ;

                        if(!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, R.string.success_login_msg, Toast.LENGTH_SHORT).show();
                            j = new Intent(MainActivity.this, WIPActivity.class); startActivity(j);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.error_login_msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                break;
            case R.id.game_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.language_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.day_night_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.infos_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            default:break;
        }
    }
}
