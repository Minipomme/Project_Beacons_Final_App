package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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

        validLoading.setVisibility(View.INVISIBLE);
        buttonValid.setEnabled(false);

        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent mapIntent = new Intent(view.getContext(), MapActivity.class);

                mapIntent.putExtra("widthRoom", widthRoom.getText().toString());
                mapIntent.putExtra("heightRoom", heightRoom.getText().toString());

                mapIntent.putExtra("offsetMap_x", offsetMap_x.getText().toString());
                mapIntent.putExtra("offsetMap_y", offsetMap_y.getText().toString());

                mapIntent.putExtra("positionXFixedBeaconOne", position_x_fixedBeaconOne.getText().toString());
                mapIntent.putExtra("positionXFixedBeaconTwo", position_x_fixedBeaconTwo.getText().toString());
                mapIntent.putExtra("positionXFixedBeaconThree", position_x_fixedBeaconThree.getText().toString());
                mapIntent.putExtra("positionXFixedBeaconFour", position_x_fixedBeaconFour.getText().toString());

                mapIntent.putExtra("positionYFixedBeaconOne", position_y_fixedBeaconOne.getText().toString());
                mapIntent.putExtra("positionYFixedBeaconTwo", position_y_fixedBeaconTwo.getText().toString());
                mapIntent.putExtra("positionYFixedBeaconThree", position_y_fixedBeaconThree.getText().toString());
                mapIntent.putExtra("positionYFixedBeaconFour", position_y_fixedBeaconFour.getText().toString());

                mapIntent.putExtra("imageToLoad",filePath);

                startActivity(mapIntent);*/
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

}