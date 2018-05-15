package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminActivity extends AppCompatActivity {
    private static final int SELECTED_PICTURE=1;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String ERROR = "ERROR";
    private Context context;

    private Button buttonValid;

    private TextView widthRoom;
    private TextView heightRoom;

    private TextView offsetMap_x;
    private TextView offsetMap_y;

    private TextView position_x_fixedBeaconOne;
    private TextView position_x_fixedBeaconTwo;
    private TextView position_x_fixedBeaconThree;
    private TextView position_x_fixedBeaconFour;

    private TextView position_y_fixedBeaconOne;
    private TextView position_y_fixedBeaconTwo;
    private TextView position_y_fixedBeaconThree;
    private TextView position_y_fixedBeaconFour;

    private ImageView validLoading;
    private String filePath;
    private String MapName = "Map.png";
    private String MapDirectory = "images";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        verifyStoragePermissions(this);

        validLoading = this.findViewById(R.id.validLoadingImg);

        buttonValid = this.findViewById(R.id.buttonValid);

        widthRoom = this.findViewById(R.id.editText_widthMap );
        heightRoom = this.findViewById(R.id.editText_heightMap);

        offsetMap_x = this.findViewById(R.id.offsetMap_x);
        offsetMap_y = this.findViewById(R.id.offsetMap_y);

        position_x_fixedBeaconOne = this.findViewById(R.id.editText_fixedBeaconOne_X );
        position_x_fixedBeaconTwo = this.findViewById(R.id.editText_fixedBeaconTwo_X );
        position_x_fixedBeaconThree = this.findViewById(R.id.editText_fixedBeaconThree_X );
        position_x_fixedBeaconFour = this.findViewById(R.id.editText_fixedBeaconFour_X );

        position_y_fixedBeaconOne = this.findViewById(R.id.editText_fixedBeaconOne_Y );
        position_y_fixedBeaconTwo = this.findViewById(R.id.editText_fixedBeaconTwo_Y );
        position_y_fixedBeaconThree = this.findViewById(R.id.editText_fixedBeaconThree_Y );
        position_y_fixedBeaconFour = this.findViewById(R.id.editText_fixedBeaconFour_Y);

        // Load Data Room
        String Data = loadAdminData("widthRoom");
        if(Data != ERROR)
            widthRoom.setText(Data);
        Data = loadAdminData("heightRoom");
        if(Data != ERROR)
            heightRoom.setText(Data);

        // Load Data Offset
        Data = loadAdminData("offsetMap_x");
        if(Data != ERROR)
            offsetMap_x.setText(Data);
        Data = loadAdminData("offsetMap_y");
        if(Data != ERROR)
            offsetMap_y.setText(Data);

        // Load Data Position Beacon One
        Data = loadAdminData("positionXFixedBeaconOne");
        if(Data != ERROR)
            position_x_fixedBeaconOne.setText(Data);
        Data = loadAdminData("positionYFixedBeaconOne");
        if(Data != ERROR)
            position_y_fixedBeaconOne.setText(Data);

        // Load Data Position Beacon Two
        Data = loadAdminData("positionXFixedBeaconTwo");
        if(Data != ERROR)
            position_x_fixedBeaconTwo.setText(Data);
        Data = loadAdminData("positionYFixedBeaconTwo");
        if(Data != ERROR)
            position_y_fixedBeaconTwo.setText(Data);

        // Load Data Position Beacon Three
        Data = loadAdminData("positionXFixedBeaconThree");
        if(Data != ERROR)
            position_x_fixedBeaconThree.setText(Data);
        Data = loadAdminData("positionYFixedBeaconThree");
        if(Data != ERROR)
            position_y_fixedBeaconThree.setText(Data);

        // Load Data Position Beacon Four
        Data = loadAdminData("positionXFixedBeaconFour");
        if(Data != ERROR)
            position_x_fixedBeaconFour.setText(Data);
        Data = loadAdminData("positionYFixedBeaconFour");
        if(Data != ERROR)
            position_y_fixedBeaconFour.setText(Data);

        // Load Map FilePath
        Data = loadAdminData("map_name");
        if(Data != ERROR) {
            Data = loadAdminData("map_directory");
            if(Data != ERROR) {
                buttonValid.setEnabled(true);
                validLoading.setVisibility(View.VISIBLE);
            } else {
                validLoading.setVisibility(View.INVISIBLE);
                buttonValid.setEnabled(false);
            }
        } else {
            validLoading.setVisibility(View.INVISIBLE);
            buttonValid.setEnabled(false);
        }

        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save Data Room
                saveAdminData("widthRoom", widthRoom.getText().toString());
                saveAdminData("heightRoom", heightRoom.getText().toString());

                // Save Data Offset
                saveAdminData("offsetMap_x", offsetMap_x.getText().toString());
                saveAdminData("offsetMap_y", offsetMap_y.getText().toString());

                // Save Data Position Beacon One
                saveAdminData("positionXFixedBeaconOne", position_x_fixedBeaconOne.getText().toString());
                saveAdminData("positionYFixedBeaconOne", position_y_fixedBeaconOne.getText().toString());

                // Save Data Position Beacon Two
                saveAdminData("positionXFixedBeaconTwo", position_x_fixedBeaconTwo.getText().toString());
                saveAdminData("positionYFixedBeaconTwo", position_y_fixedBeaconTwo.getText().toString());

                // Save Data Position Beacon Three
                saveAdminData("positionXFixedBeaconThree", position_x_fixedBeaconThree.getText().toString());
                saveAdminData("positionYFixedBeaconThree", position_y_fixedBeaconThree.getText().toString());

                // Save Data Position Beacon Four
                saveAdminData("positionXFixedBeaconFour", position_x_fixedBeaconFour.getText().toString());
                saveAdminData("positionYFixedBeaconFour", position_y_fixedBeaconFour.getText().toString());

                // Save Map
                saveAdminData("map_name", MapName);
                saveAdminData("map_directory", MapDirectory);
                Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

                new ImageSaver(context).
                        setFileName(MapName).
                        setDirectoryName(MapDirectory).
                        save(yourSelectedImage);

                Intent mainIntent = new Intent(view.getContext(), MainActivity.class); startActivity(mainIntent);
            }
        });
    }

    public void loadMap(View v){
        Log.d("LOG","Click sur le bouton loadMap");
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,SELECTED_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch(requestCode){
            case SELECTED_PICTURE:
                if(resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    String[] projection={MediaStore.Images.Media.DATA};

                    Cursor cursor=getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();

                    int columnIndex=cursor.getColumnIndex(projection[0]);
                    filePath=cursor.getString(columnIndex);
                    cursor.close();

                    if(filePath!=null){
                        buttonValid.setEnabled(true);
                        validLoading.setVisibility(View.VISIBLE);
                    }

                }
                break;
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    protected void saveAdminData(String Key, String Value) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Key, Value);

        editor.apply();
    }

    protected String loadAdminData(String Key) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String Data = settings.getString(Key, ERROR);

        return Data;
    }

}