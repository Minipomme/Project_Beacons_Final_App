package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * UI class that manage every part of the design. It doesn't care about data.
 */
public class MapActivity extends AppCompatActivity {
    private String filePath;

    private int NB_Arduinos;
    private int NB_Beacons;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String MAP_FILE_NAME = "map.png";
    private int deviceWidth;
    private int deviceHeight;


    //Gestion des données internes de l'application
    private boolean ConfigNotComplete = false;
    String [] dataTitleTable = {"heightRoom","widthRoom","offsetMap_x","offsetMap_y","positionXFixedBeaconOne","positionYFixedBeaconOne","positionXFixedBeaconTwo","positionYFixedBeaconTwo","positionXFixedBeaconThree","positionYFixedBeaconThree","positionXFixedBeaconFour","positionYFixedBeaconFour"};
    String [] dataTable = new String [dataTitleTable.length];

    //Variables de gestion de l'affichage
    private ImageView map;
    private ImageView fixedBeaconOne;
    private ImageView fixedBeaconTwo;
    private ImageView fixedBeaconThree;
    private ImageView fixedBeaconFour;
    private ImageButton selectGoals;

    //Selection des beacons à afficher
    private FireMissilesDialogFragment fragment;

    //Classe de gestion du bluetooth
    private BleManager bleManager;



    private DataManager dataManager;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);

        NB_Arduinos=Integer.parseInt(getString(R.string.NB_ARDUINO));
        NB_Beacons=Integer.parseInt(getString(R.string.NB_BEACONS));
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        deviceWidth = point.x;
        deviceHeight = point.y;

        //Recuperation des informations sauvegardées
        for(int i=0;i<12;i++){
            dataTable[i]=loadAdminData(dataTitleTable[i]);
            if(dataTable[i]==null){
                dataTable[i]="0";
                ConfigNotComplete = true;
            }
        }

        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.DefaultScene);

        fixedBeaconOne = findViewById(R.id.FixedBeaconOne);
        changeTheme(wrapper.getTheme(), fixedBeaconOne, R.drawable.ic_number_one_in_a_circle);

        fixedBeaconTwo = findViewById(R.id.FixedBeaconTwo);
        changeTheme(wrapper.getTheme(), fixedBeaconTwo, R.drawable.ic_number_two_in_a_circle);

        fixedBeaconThree = findViewById(R.id.FixedBeaconThree);
        changeTheme(wrapper.getTheme(), fixedBeaconThree, R.drawable.ic_number_three_in_a_circle);

        fixedBeaconFour = findViewById(R.id.FixedBeaconFour);
        changeTheme(wrapper.getTheme(), fixedBeaconFour, R.drawable.ic_number_four_in_a_circle);

        map = findViewById(R.id.map);

        String Data = loadAdminData("mapPath");
        if(Data != null) {
            filePath = Data;
            Bitmap SavedMap = loadImageFromStorage(filePath);
            map.setImageBitmap(SavedMap);
        }
        else {
            filePath = null;
            ConfigNotComplete =  true;
        }

        if(ConfigNotComplete) {
            Toast.makeText(MapActivity.this, "Configuration is not complete, contact an administrator", Toast.LENGTH_SHORT).show();
        }

        selectGoals = findViewById(R.id.selectGoals);
        selectGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new FireMissilesDialogFragment();
                fragment.setInput(ParcBeacon.getBeaconsToFindString(NB_Beacons));
                fragment.show(getFragmentManager(),"SELECT");
            }
        });
        bleManager=new BleManager(this);
        dataManager = bleManager.getDataManager();

        for(Beacon bcn : ParcBeacon.getBeaconsToFind()){
            changeTheme(new ContextThemeWrapper(this, R.style.Beacon_One).getTheme(),bcn.getImage(),R.drawable.ic_place_black_24dp);
            addContentView(bcn.getImage(),bcn.getImage().getLayoutParams());
        }

        bleManager.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bleManager.getGatt() == null) {
            return;
        }
        bleManager.getGatt().disconnect();
        bleManager.getGatt().close();
        bleManager.setGatt(null);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //Mise à l'échelle
            //      0           1           2               3               4                           5                           6                       7                           8                       9                           10                          11
            //"heightRoom","widthRoom","offsetMap_x","offsetMap_y","positionXFixedBeaconOne","positionYFixedBeaconOne","positionXFixedBeaconTwo","positionYFixedBeaconTwo","positionXFixedBeaconThree","positionYFixedBeaconThree","positionXFixedBeaconFour","positionYFixedBeaconFour"
            fixedBeaconOne.setX(settingScale(dataTable[4],fixedBeaconOne,"x"));
            fixedBeaconOne.setY(settingScale(dataTable[5],fixedBeaconOne,"y"));

            fixedBeaconTwo.setX(settingScale(dataTable[6],fixedBeaconTwo,"x"));
            fixedBeaconTwo.setY(settingScale(dataTable[7],fixedBeaconTwo,"y"));

            fixedBeaconThree.setX(settingScale(dataTable[8],fixedBeaconThree,"x"));
            fixedBeaconThree.setY(settingScale(dataTable[9],fixedBeaconThree,"y"));

            fixedBeaconFour.setX(settingScale(dataTable[10],fixedBeaconFour,"x"));
            fixedBeaconFour.setY(settingScale(dataTable[11],fixedBeaconFour,"y"));
        }
    }

    @Override
    public void onBackPressed() {
        MapActivity.this.finish();
    }

    //Mise à l'échelle en fonction de la position
    public Float settingScale(String positionXY, ImageView object, String type){
        if(type=="x"){
            return ((Float.parseFloat(positionXY) * deviceWidth / Float.parseFloat(dataTable[1]))
                    + Float.parseFloat(dataTable[2])) - object.getWidth()/2;
        }
        else if(type=="y"){
            return ((Float.parseFloat(positionXY) * deviceHeight / Float.parseFloat(dataTable[0]))
                    + Float.parseFloat(dataTable[3])) - object.getHeight()/2;
        }
        else{
            return -1f;
        }
    }

    public void setGoalsPosition(double[] calculatedPosition, Beacon bcn){
        bcn.getImage().setX(settingScale(Float.toString((float) calculatedPosition[0]),bcn.getImage(),"x")); //bcn.getImage() de temps en temps il est null donc ça plante (java.lang.NullPointerException: Attempt to invoke virtual method 'int android.widget.ImageView.getWidth()' on a null object reference)
        bcn.getImage().setY(settingScale(Float.toString((float) calculatedPosition[1]),bcn.getImage(),"y"));


        if(fragment!=null) {
            if(fragment.getmSelectedItems().contains(bcn.getName())){
                bcn.getImage().setVisibility(View.VISIBLE);
            }
            else{
                bcn.getImage().setVisibility(View.INVISIBLE);
            }
        }
    }



    public ImageView getFixedBeaconOne() {
        return fixedBeaconOne;
    }

    public void setFixedBeaconOne(ImageView fixedBeaconOne) {
        this.fixedBeaconOne = fixedBeaconOne;
    }

    public ImageView getFixedBeaconTwo() {
        return fixedBeaconTwo;
    }

    public void setFixedBeaconTwo(ImageView fixedBeaconTwo) {
        this.fixedBeaconTwo = fixedBeaconTwo;
    }

    public ImageView getFixedBeaconThree() {
        return fixedBeaconThree;
    }

    public void setFixedBeaconThree(ImageView fixedBeaconThree) {
        this.fixedBeaconThree = fixedBeaconThree;
    }

    public ImageView getFixedBeaconFour() {
        return fixedBeaconFour;
    }

    public void setFixedBeaconFour(ImageView fixedBeaconFour) {
        this.fixedBeaconFour = fixedBeaconFour;
    }

    public String getHeightRoom() {
        return dataTable[0];
    }

    public void setHeightRoom(String heightRoom) { this.dataTable[0] = heightRoom; }

    public String getWidthRoom() {
        return dataTable[1];
    }

    public void setWidthRoom(String widthRoom) { this.dataTable[1] = widthRoom; }

    public String getPosition_x_fixed_beacon_one() { return dataTable[4]; }

    public void setPosition_x_fixed_beacon_one(String position_x_fixed_beacon_one) { this.dataTable[4] = position_x_fixed_beacon_one; }

    public String getPosition_y_fixed_beacon_one() {
        return dataTable[5];
    }

    public void setPosition_y_fixed_beacon_one(String position_y_fixed_beacon_one) { this.dataTable[5] = position_y_fixed_beacon_one; }

    public String getPosition_x_fixed_beacon_two() {
        return dataTable[6];
    }

    public void setPosition_x_fixed_beacon_two(String position_x_fixed_beacon_two) { this.dataTable[6] = position_x_fixed_beacon_two; }

    public String getPosition_y_fixed_beacon_two() {
        return dataTable[7];
    }

    public void setPosition_y_fixed_beacon_two(String position_y_fixed_beacon_two) { this.dataTable[7] = position_y_fixed_beacon_two; }

    public String getPosition_x_fixed_beacon_three() {
        return dataTable[8];
    }

    public void setPosition_x_fixed_beacon_three(String position_x_fixed_beacon_three) { this.dataTable[8] = position_x_fixed_beacon_three; }

    public String getPosition_y_fixed_beacon_three() {
        return dataTable[9];
    }

    public void setPosition_y_fixed_beacon_three(String position_y_fixed_beacon_three) { this.dataTable[9] = position_y_fixed_beacon_three; }

    public String getPosition_x_fixed_beacon_four() {
        return dataTable[10];
    }

    public void setPosition_x_fixed_beacon_four(String position_x_fixed_beacon_four) { this.dataTable[10] = position_x_fixed_beacon_four; }

    public String getPosition_y_fixed_beacon_four() {
        return dataTable[11];
    }

    public void setPosition_y_fixed_beacon_four(String position_y_fixed_beacon_four) { this.dataTable[11] = position_y_fixed_beacon_four; }

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public void setDeviceWidth(int deviceWidth) {
        this.deviceWidth = deviceWidth;
    }

    public int getDeviceHeight() {
        return deviceHeight;
    }

    public void setDeviceHeight(int deviceHeight) {
        this.deviceHeight = deviceHeight;
    }

    public String getOffsetMap_x() {
        return dataTable[2];
    }

    public void setOffsetMap_x(String offsetMap_x) {
        this.dataTable[2] = offsetMap_x;
    }

    public String getOffsetMap_y() {
        return dataTable[3];
    }

    public void setOffsetMap_y(String offsetMap_y) {
        this.dataTable[3] = offsetMap_y;
    }

    public View getView(){return this.getView();}

    public void changeTheme(final Resources.Theme theme, ImageView imageView, int source_drawable) {
        final Drawable drawable = ResourcesCompat.getDrawable(getResources(), source_drawable, theme);
        imageView.setImageDrawable(drawable);
    }

    private String loadAdminData(String Key) {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String Data = settings.getString(Key, null);

        return Data;
    }

    private Bitmap loadImageFromStorage(String path)
    {
        try {
            File f=new File(path, MAP_FILE_NAME);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static class FireMissilesDialogFragment extends DialogFragment {
        private String[] input;
        private ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();  // Where we track the selected items

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle(R.string.title)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(input, null,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        mSelectedItems.add(which);
                                    } else if (mSelectedItems.contains(which)) {
                                        // Else, if the item is already in the array, remove it
                                        mSelectedItems.remove(Integer.valueOf(which));
                                    }
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            return builder.create();
        }

        public String[] getInput() {
            return input;
        }

        public ArrayList<Integer> getmSelectedItems() {
            return mSelectedItems;
        }

        public void setInput(String[] input) {
            this.input = input;
        }

        public void setmSelectedItems(ArrayList mSelectedItems) {
            this.mSelectedItems = mSelectedItems;
        }
    }

    public FireMissilesDialogFragment getFragment() {
        return fragment;
    }

    public void setFragment(FireMissilesDialogFragment fragment) {
        this.fragment = fragment;
    }
}
