package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView adminCard, gameCard, languageCard, day_nightCard, infosCard;
    private String verif_password, verif_email, password, email;

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
                //load the email & password
                SharedPreferences verif_settings = getSharedPreferences("PREFS", 0);
                verif_password = verif_settings.getString("password", "");
                verif_email = verif_settings.getString("email", "");

                if(verif_email.isEmpty() || verif_password.isEmpty()) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_create_or_login, null);

                    final EditText mNewEmail = mView.findViewById(R.id.editTextNewEmail);
                    final EditText mNewPassword = mView.findViewById(R.id.editTextNewPwd);
                    final EditText mNewPasswordAgain = mView.findViewById(R.id.editTextNewPwdAgain);
                    Button mNewLogin = mView.findViewById(R.id.btnCreateLogin);

                    mNewLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent j ;

                            if(!mNewEmail.getText().toString().isEmpty() && !mNewPassword.getText().toString().isEmpty() && !mNewPasswordAgain.getText().toString().isEmpty()) {
                                if (validateEmail(mNewEmail.getText().toString())) {
                                    if (validatePwd(mNewPassword.getText().toString())) {
                                        if (mNewPassword.getText().toString().equals(mNewPasswordAgain.getText().toString())) {
                                            //save the password
                                            Toast.makeText(MainActivity.this, R.string.succ_login_created_msg, Toast.LENGTH_SHORT).show();
                                            SharedPreferences settings = getSharedPreferences("PREFS", 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("email", mNewEmail.getText().toString());
                                            editor.putString("password", mNewPassword.getText().toString());

                                            //start the next activity
                                            j = new Intent(MainActivity.this, WIPActivity.class);
                                            startActivity(j);
                                        } else {
                                            //there is no match on the passwords
                                            mNewPasswordAgain.setError("Passwords doesn't match !");
                                            mNewPasswordAgain.requestFocus();
                                        }
                                    } else {
                                        mNewPassword.setError("Invalid Password");
                                        mNewPassword.requestFocus();
                                    }
                                } else {
                                    mNewEmail.setError("Invalid Email");
                                    mNewEmail.requestFocus();
                                }
                            } else {
                                //there is no password
                                Toast.makeText(MainActivity.this, R.string.error_login_msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();

                } else {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
                    final EditText mEmail = mView.findViewById(R.id.editTextEmail);
                    final EditText mPassword = mView.findViewById(R.id.editTextPwd);
                    Button mLogin = mView.findViewById(R.id.btnLogin);

                    //load the password
                    SharedPreferences settings = getSharedPreferences("PREFS", 0);
                    password = settings.getString("password", "");
                    email = settings.getString("email", "");

                    mLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent j ;

                            if(!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
                                if(mEmail.getText().toString().equals(email) && mPassword.getText().toString().equals(password)) {

                                    //start the next activity
                                    Toast.makeText(MainActivity.this, R.string.success_login_msg, Toast.LENGTH_SHORT).show();
                                    j = new Intent(MainActivity.this, WIPActivity.class); startActivity(j);

                                } else {
                                    Toast.makeText(MainActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, R.string.error_login_msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
                break;
            case R.id.game_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.language_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.day_night_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.infos_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            default:break;
        }
    }

    //Return true if password is valid and false if password is invalid
    protected boolean validatePwd(String password) {
        if(password!=null && password.length()>9) {
            return true;
        } else {
            return false;
        }
    }

    //Return true if email is valid and false if email is invalid
    protected boolean validateEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
