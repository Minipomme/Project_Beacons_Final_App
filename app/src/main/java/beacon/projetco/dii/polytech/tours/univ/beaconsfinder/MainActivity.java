package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView adminCard, gameCard, infosCard;
    private String email_saved, password_saved;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Defining Cards
        adminCard = findViewById(R.id.admin_card);
        gameCard = findViewById(R.id.game_card);
        infosCard = findViewById(R.id.infos_card);

        // Add Click Listener to the cards
        adminCard.setOnClickListener(this);
        gameCard.setOnClickListener(this);
        infosCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i ;

        switch (view.getId()) {
            case R.id.admin_card :
                if(loadLogin()) {
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
                                            //save the login
                                            saveLogin(mNewEmail.getText().toString(), mNewPassword.getText().toString());
                                            Toast.makeText(MainActivity.this, R.string.succ_login_created_msg, Toast.LENGTH_SHORT).show();

                                            //start the next activity
                                            j = new Intent(MainActivity.this, AdminActivity.class);
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

                    mLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent j ;

                            if(!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
                                if (compareWithEmailSaved(mEmail.getText().toString())) {
                                    if (compareWithPwdSaved(mPassword.getText().toString())) {
                                        //start the next activity
                                        Toast.makeText(MainActivity.this, R.string.success_login_msg, Toast.LENGTH_SHORT).show();
                                        j = new Intent(MainActivity.this, AdminActivity.class); startActivity(j);
                                    } else {
                                        mPassword.setError("Invalid Password");
                                        mPassword.requestFocus();
                                    }
                                } else {
                                    mEmail.setError("Invalid Email");
                                    mEmail.requestFocus();
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
            case R.id.infos_card : i = new Intent(this, WIPActivity.class); startActivity(i); break;
            case R.id.game_card : i = new Intent(this, GameActivity.class); startActivity(i); break;
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

    protected void saveLogin(String email, String password) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        // Sauvegarde du mot de passe hashé SHA-256
        editor.putString(PASSWORD, stringifyHashedPassword(getHash(password)));
        editor.putString(EMAIL, email);

        editor.apply();
    }

    // Convertion des mot de passe hashé (byte[] -> String)
    private static String stringifyHashedPassword(byte[] hash) {
        StringBuffer hashedPassword = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hashedPassword.append('0');
            hashedPassword.append(hex);
        }
        return hashedPassword.toString();
    }

    // Hashage du mot de passe en SHA-256
    public byte[] getHash(String password) {
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    protected boolean loadLogin() {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        email_saved = settings.getString(EMAIL, "");
        password_saved = settings.getString(PASSWORD, "");

        if(email_saved.isEmpty() || password_saved.isEmpty())
            return true;
        else
            return false;
    }

    protected boolean compareWithEmailSaved(String email) {
        return email.equals(email_saved);
    }

    protected boolean compareWithPwdSaved(String password) {
        // Hashage du password en SHA-256 puis comparaison avec celui sauvegarder
        return stringifyHashedPassword(getHash(password)).equals(password_saved);
    }
}
