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

public class MapActivity extends AppCompatActivity {
    private String heightRoom;
    private String widthRoom;
    private String position_x_fixed_beacon_one;
    private String position_y_fixed_beacon_one;
    private String position_x_fixed_beacon_two;
    private String position_y_fixed_beacon_two;
    private String position_x_fixed_beacon_three;
    private String position_y_fixed_beacon_three;
    private String position_x_fixed_beacon_four;
    private String position_y_fixed_beacon_four;
    private String offsetMap_x;
    private String offsetMap_y;
    private String filePath;

    private ImageView map;
    private ImageView fixedBeaconOne;
    private ImageView fixedBeaconTwo;
    private ImageView fixedBeaconThree;
    private ImageView fixedBeaconFour;
    private ImageView goal1;
    private ImageView goal2;
    private ImageView goal3;

    private int deviceWidth;
    private int deviceHeight;

    private ImageButton selectGoals;

    private FireMissilesDialogFragment fragment;
    private String[] beaconsToFind = {"Beacon1","Beacon2","Beacon3"};

    private BleManager bleManager;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String MAP_FILE_NAME = "map.png";

    private boolean ConfigNotComplete = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        deviceWidth = point.x;
        deviceHeight = point.y;

        //Recuperation des informations sauvegardées
        String Data = loadAdminData("heightRoom");
        if(Data!= null)
            heightRoom = Data;
        else {
            heightRoom = "0";
            ConfigNotComplete =  true;
        }
        Data = loadAdminData("widthRoom");
        if(Data!= null)
            widthRoom = Data;
        else {
            widthRoom = "0";
            ConfigNotComplete =  true;
        }

        Data = loadAdminData("offsetMap_x");
        if(Data!= null)
            offsetMap_x = Data;
        else {
            offsetMap_x = "0";
            ConfigNotComplete =  true;
        }
        Data = loadAdminData("offsetMap_y");
        if(Data!= null)
            offsetMap_y = Data;
        else {
            offsetMap_y = "0";
            ConfigNotComplete =  true;
        }

        Data = loadAdminData("positionXFixedBeaconOne");
        if(Data!= null)
            position_x_fixed_beacon_one = Data;
        else {
            position_x_fixed_beacon_one = "0";
            ConfigNotComplete =  true;
        }
        Data = loadAdminData("positionYFixedBeaconOne");
        if(Data!= null)
            position_y_fixed_beacon_one = Data;
        else {
            position_y_fixed_beacon_one = "0";
            ConfigNotComplete =  true;
        }

        Data = loadAdminData("positionXFixedBeaconTwo");
        if(Data!= null)
            position_x_fixed_beacon_two = Data;
        else {
            position_x_fixed_beacon_two = "0";
            ConfigNotComplete =  true;
        }
        Data = loadAdminData("positionYFixedBeaconTwo");
        if(Data!= null)
            position_y_fixed_beacon_two = Data;
        else {
            position_y_fixed_beacon_two = "0";
            ConfigNotComplete =  true;
        }

        Data = loadAdminData("positionXFixedBeaconThree");
        if(Data!= null)
            position_x_fixed_beacon_three = Data;
        else {
            position_x_fixed_beacon_three = "0";
            ConfigNotComplete =  true;
        }
        Data = loadAdminData("positionYFixedBeaconThree");
        if(Data!= null)
            position_y_fixed_beacon_three = Data;
        else {
            position_y_fixed_beacon_three = "0";
            ConfigNotComplete =  true;
        }

        Data = loadAdminData("positionXFixedBeaconFour");
        if(Data!= null)
            position_x_fixed_beacon_four = Data;
        else {
            position_x_fixed_beacon_four = "0";
            ConfigNotComplete =  true;
        }
        Data = loadAdminData("positionYFixedBeaconFour");
        if(Data!= null)
            position_y_fixed_beacon_four = Data;
        else {
            position_y_fixed_beacon_four = "0";
            ConfigNotComplete =  true;
        }

        fixedBeaconOne = findViewById(R.id.FixedBeaconOne);
        ContextThemeWrapper wrapper = new ContextThemeWrapper(this, R.style.DefaultScene);
        changeTheme(wrapper.getTheme(), fixedBeaconOne, R.drawable.ic_number_one_in_a_circle);

        fixedBeaconTwo = findViewById(R.id.FixedBeaconTwo);
        changeTheme(wrapper.getTheme(), fixedBeaconTwo, R.drawable.ic_number_two_in_a_circle);

        fixedBeaconThree = findViewById(R.id.FixedBeaconThree);
        changeTheme(wrapper.getTheme(), fixedBeaconThree, R.drawable.ic_number_three_in_a_circle);

        fixedBeaconFour = findViewById(R.id.FixedBeaconFour);
        changeTheme(wrapper.getTheme(), fixedBeaconFour, R.drawable.ic_number_four_in_a_circle);

        goal1 = this.findViewById(R.id.goal1);
        wrapper = new ContextThemeWrapper(this, R.style.Beacon_One);
        changeTheme(wrapper.getTheme(), goal1, R.drawable.ic_place_black_50dp);

        goal2 = this.findViewById(R.id.goal2);
        wrapper = new ContextThemeWrapper(this, R.style.Beacon_Two);
        changeTheme(wrapper.getTheme(), goal2, R.drawable.ic_place_black_50dp);

        goal3 = this.findViewById(R.id.goal3);
        wrapper = new ContextThemeWrapper(this, R.style.Beacon_Three);
        changeTheme(wrapper.getTheme(), goal3, R.drawable.ic_place_black_50dp);


        Data = loadAdminData("mapPath");
        if(Data!= null)
            filePath = Data;
        else {
            filePath = null;
            ConfigNotComplete =  true;
        }
        map = findViewById(R.id.map);

        if(filePath != null) {
            Bitmap SavedMap = loadImageFromStorage(filePath);
            map.setImageBitmap(SavedMap);
        }

        if(ConfigNotComplete) {
            Toast.makeText(MapActivity.this, "Configuration is not complete, contact an administrator", Toast.LENGTH_SHORT).show();
        }

        selectGoals = findViewById(R.id.selectGoals);
        selectGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new FireMissilesDialogFragment();
                fragment.setInput(beaconsToFind);
                fragment.show(getFragmentManager(),"SELECT");
            }
        });
        bleManager=new BleManager(this);
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
            fixedBeaconOne.setX(settingScale(position_x_fixed_beacon_one,fixedBeaconOne,"x"));
            fixedBeaconOne.setY(settingScale(position_y_fixed_beacon_one,fixedBeaconOne,"y"));

            fixedBeaconTwo.setX(settingScale(position_x_fixed_beacon_two,fixedBeaconTwo,"x"));
            fixedBeaconTwo.setY(settingScale(position_y_fixed_beacon_two,fixedBeaconTwo,"y"));

            fixedBeaconThree.setX(settingScale(position_x_fixed_beacon_three,fixedBeaconThree,"x"));
            fixedBeaconThree.setY(settingScale(position_y_fixed_beacon_three,fixedBeaconThree,"y"));

            fixedBeaconFour.setX(settingScale(position_x_fixed_beacon_four,fixedBeaconFour,"x"));
            fixedBeaconFour.setY(settingScale(position_y_fixed_beacon_four,fixedBeaconFour,"y"));
        }
    }

    //Mise à l'échelle en fonction de la position
    public Float settingScale(String positionXY, ImageView object, String type){
        if(type=="x"){
            return ((Float.parseFloat(positionXY) * deviceWidth / Float.parseFloat(widthRoom))
                    + Float.parseFloat(offsetMap_x)) - object.getWidth()/2;
        }
        else if(type=="y"){
            return ((Float.parseFloat(positionXY) * deviceHeight / Float.parseFloat(heightRoom))
                    + Float.parseFloat(offsetMap_y)) - object.getHeight()/2;
        }
        else{
            return -1f;
        }
    }

    public void setGoalsPosition(double[] calculatedPosition, int beacon){
        switch (beacon) {
            case 1:
                //Position du beacon 1
                goal1.setX(settingScale(Float.toString((float) calculatedPosition[0]),goal1,"x"));
                goal1.setY(settingScale(Float.toString((float) calculatedPosition[1]),goal1,"y"));
                //Log.i("MAP POC - BEACON1 (m)", "X = " + calculatedPosition[0] + " -- Y = " + calculatedPosition[1]);
                break;
            case 2:
                //Position du beacon 2
                goal2.setX(settingScale(Float.toString((float) calculatedPosition[0]),goal2,"x"));
                goal2.setY(settingScale(Float.toString((float) calculatedPosition[1]),goal2,"y"));
                //Log.i("MAP POC - BEACON2", "X = " + calculatedPosition[0] + " -- Y = " + calculatedPosition[1]);
                break;
            case 3:
                //Position du beacon 3
                goal3.setX(settingScale(Float.toString((float) calculatedPosition[0]),goal3,"x"));
                goal3.setY(settingScale(Float.toString((float) calculatedPosition[1]),goal3,"y"));
                //Log.i("MAP POC - BEACON3", "X = " + calculatedPosition[0] + " -- Y = " + calculatedPosition[1]);
                break;
        }

        if(fragment!=null) {
            if (fragment.getmSelectedItems().contains(0)) {
                goal1.setVisibility(View.VISIBLE);
            } else {
                goal1.setVisibility(View.INVISIBLE);
            }

            if (fragment.getmSelectedItems().contains(1)) {
                goal2.setVisibility(View.VISIBLE);
            } else {
                goal2.setVisibility(View.INVISIBLE);
            }

            if (fragment.getmSelectedItems().contains(2)) {
                goal3.setVisibility(View.VISIBLE);
            } else {
                goal3.setVisibility(View.INVISIBLE);
            }
        }
        //getWindow().getDecorView().findViewById(android.R.id.content).postInvalidate();
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

    public ImageView getGoal1() {
        return goal1;
    }

    public void setGoal1(ImageView goal1) {
        this.goal1 = goal1;
    }

    public ImageView getGoal2() {
        return goal2;
    }

    public void setGoal2(ImageView goal2) {
        this.goal2 = goal2;
    }

    public ImageView getGoal3() {
        return goal3;
    }

    public void setGoal3(ImageView goal3) {
        this.goal3 = goal3;
    }

    public FireMissilesDialogFragment getFragment() {
        return fragment;
    }

    public void setFragment(FireMissilesDialogFragment fragment) {
        this.fragment = fragment;
    }

    public String getHeightRoom() {
        return heightRoom;
    }

    public void setHeightRoom(String heightRoom) {
        this.heightRoom = heightRoom;
    }

    public String getWidthRoom() {
        return widthRoom;
    }

    public void setWidthRoom(String widthRoom) {
        this.widthRoom = widthRoom;
    }

    public String getPosition_x_fixed_beacon_one() {
        return position_x_fixed_beacon_one;
    }

    public void setPosition_x_fixed_beacon_one(String position_x_fixed_beacon_one) {
        this.position_x_fixed_beacon_one = position_x_fixed_beacon_one;
    }

    public String getPosition_y_fixed_beacon_one() {
        return position_y_fixed_beacon_one;
    }

    public void setPosition_y_fixed_beacon_one(String position_y_fixed_beacon_one) {
        this.position_y_fixed_beacon_one = position_y_fixed_beacon_one;
    }

    public String getPosition_x_fixed_beacon_two() {
        return position_x_fixed_beacon_two;
    }

    public void setPosition_x_fixed_beacon_two(String position_x_fixed_beacon_two) {
        this.position_x_fixed_beacon_two = position_x_fixed_beacon_two;
    }

    public String getPosition_y_fixed_beacon_two() {
        return position_y_fixed_beacon_two;
    }

    public void setPosition_y_fixed_beacon_two(String position_y_fixed_beacon_two) {
        this.position_y_fixed_beacon_two = position_y_fixed_beacon_two;
    }

    public String getPosition_x_fixed_beacon_three() {
        return position_x_fixed_beacon_three;
    }

    public void setPosition_x_fixed_beacon_three(String position_x_fixed_beacon_three) {
        this.position_x_fixed_beacon_three = position_x_fixed_beacon_three;
    }

    public String getPosition_y_fixed_beacon_three() {
        return position_y_fixed_beacon_three;
    }

    public void setPosition_y_fixed_beacon_three(String position_y_fixed_beacon_three) {
        this.position_y_fixed_beacon_three = position_y_fixed_beacon_three;
    }

    public String getPosition_x_fixed_beacon_four() {
        return position_x_fixed_beacon_four;
    }

    public void setPosition_x_fixed_beacon_four(String position_x_fixed_beacon_four) {
        this.position_x_fixed_beacon_four = position_x_fixed_beacon_four;
    }

    public String getPosition_y_fixed_beacon_four() {
        return position_y_fixed_beacon_four;
    }

    public void setPosition_y_fixed_beacon_four(String position_y_fixed_beacon_four) {
        this.position_y_fixed_beacon_four = position_y_fixed_beacon_four;
    }

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
        return offsetMap_x;
    }

    public void setOffsetMap_x(String offsetMap_x) {
        this.offsetMap_x = offsetMap_x;
    }

    public String getOffsetMap_y() {
        return offsetMap_y;
    }

    public void setOffsetMap_y(String offsetMap_y) {
        this.offsetMap_y = offsetMap_y;
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
}
