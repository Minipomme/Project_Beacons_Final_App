package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdminActivity extends AppCompatActivity {
    private static final int SELECTED_PICTURE=1;

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

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String DIRECTORY_NAME = "imageDir";
    private static final String MAP_FILE_NAME = "map.png";

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
        if(Data != null)
            widthRoom.setText(Data);
        Data = loadAdminData("heightRoom");
        if(Data != null)
            heightRoom.setText(Data);

        // Load Data Offset
        Data = loadAdminData("offsetMap_x");
        if(Data != null)
            offsetMap_x.setText(Data);
        Data = loadAdminData("offsetMap_y");
        if(Data != null)
            offsetMap_y.setText(Data);

        // Load Data Position Beacon One
        Data = loadAdminData("positionXFixedBeaconOne");
        if(Data != null)
            position_x_fixedBeaconOne.setText(Data);
        Data = loadAdminData("positionYFixedBeaconOne");
        if(Data != null)
            position_y_fixedBeaconOne.setText(Data);

        // Load Data Position Beacon Two
        Data = loadAdminData("positionXFixedBeaconTwo");
        if(Data != null)
            position_x_fixedBeaconTwo.setText(Data);
        Data = loadAdminData("positionYFixedBeaconTwo");
        if(Data != null)
            position_y_fixedBeaconTwo.setText(Data);

        // Load Data Position Beacon Three
        Data = loadAdminData("positionXFixedBeaconThree");
        if(Data != null)
            position_x_fixedBeaconThree.setText(Data);
        Data = loadAdminData("positionYFixedBeaconThree");
        if(Data != null)
            position_y_fixedBeaconThree.setText(Data);

        // Load Data Position Beacon Four
        Data = loadAdminData("positionXFixedBeaconFour");
        if(Data != null)
            position_x_fixedBeaconFour.setText(Data);
        Data = loadAdminData("positionYFixedBeaconFour");
        if(Data != null)
            position_y_fixedBeaconFour.setText(Data);

        // Load Map
        if(loadAdminData("mapPath") != null) {
            buttonValid.setEnabled(true);
            validLoading.setVisibility(View.VISIBLE);

            loadImageFromStorage(loadAdminData("mapPath"));

        } else {
            validLoading.setVisibility(View.INVISIBLE);
            buttonValid.setEnabled(false);
        }

        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save Data Room
                if(widthRoom.getText().toString() != null)
                    saveAdminData("widthRoom", widthRoom.getText().toString());
                else
                    saveAdminData("widthRoom", "0");

                if(heightRoom.getText().toString() != null)
                    saveAdminData("heightRoom", heightRoom.getText().toString());
                else
                    saveAdminData("heightRoom", "0");

                // Save Data Offset
                if(offsetMap_x.getText().toString() != null)
                    saveAdminData("offsetMap_x", offsetMap_x.getText().toString());
                else
                    saveAdminData("offsetMap_x", "0");

                if(offsetMap_y.getText().toString() != null)
                    saveAdminData("offsetMap_y", offsetMap_y.getText().toString());
                else
                    saveAdminData("offsetMap_y", "0");

                // Save Data Position Beacon One
                if(position_x_fixedBeaconOne.getText().toString() != null)
                    saveAdminData("positionXFixedBeaconOne", position_x_fixedBeaconOne.getText().toString());
                else
                    saveAdminData("positionXFixedBeaconOne", "0");

                if(position_y_fixedBeaconOne.getText().toString() != null)
                    saveAdminData("positionYFixedBeaconOne", position_y_fixedBeaconOne.getText().toString());
                else
                    saveAdminData("positionYFixedBeaconOne", "0");

                // Save Data Position Beacon Two
                if(position_x_fixedBeaconTwo.getText().toString() != null)
                    saveAdminData("positionXFixedBeaconTwo", position_x_fixedBeaconTwo.getText().toString());
                else
                    saveAdminData("positionXFixedBeaconTwo", "0");

                if(position_y_fixedBeaconTwo.getText().toString() != null)
                    saveAdminData("positionYFixedBeaconTwo", position_y_fixedBeaconTwo.getText().toString());
                else
                    saveAdminData("positionYFixedBeaconTwo", "0");

                // Save Data Position Beacon Three
                if(position_x_fixedBeaconThree.getText().toString() != null)
                    saveAdminData("positionXFixedBeaconThree", position_x_fixedBeaconThree.getText().toString());
                else
                    saveAdminData("positionXFixedBeaconThree", "0");

                if(position_y_fixedBeaconThree.getText().toString() != null)
                    saveAdminData("positionYFixedBeaconThree", position_y_fixedBeaconThree.getText().toString());
                else
                    saveAdminData("positionYFixedBeaconThree", "0");

                // Save Data Position Beacon Four
                if(position_x_fixedBeaconFour.getText().toString() != null)
                    saveAdminData("positionXFixedBeaconFour", position_x_fixedBeaconFour.getText().toString());
                else
                    saveAdminData("positionXFixedBeaconFour", "0");

                if(position_y_fixedBeaconFour.getText().toString() != null)
                    saveAdminData("positionYFixedBeaconFour", position_y_fixedBeaconFour.getText().toString());
                else
                    saveAdminData("positionYFixedBeaconFour", "0");

                // Save Map
                if(filePath != null) {
                    saveAdminData("mapPath", saveToInternalStorage(BitmapFactory.decodeFile(filePath)));
                    Toast.makeText(AdminActivity.this, "File saved", Toast.LENGTH_SHORT).show();
                }


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

    private void saveAdminData(String Key, String Value) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Key, Value);

        editor.apply();
    }

    private String loadAdminData(String Key) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String Data = settings.getString(Key, null);

        return Data;
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,MAP_FILE_NAME);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private boolean loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, MAP_FILE_NAME);
            BitmapFactory.decodeStream(new FileInputStream(f));
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return false;
    }

}