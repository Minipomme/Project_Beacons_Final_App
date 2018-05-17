package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.view.View;
import android.widget.ImageView;

import com.lemmingapex.trilateration.LinearLeastSquaresSolver;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

public class Trilateration {
    private MapActivity currentActivity;

    public Trilateration(MapActivity currentActivity){
        this.currentActivity=currentActivity;
    }

    public void launchTrilateration(double [][] positions, double[] distances, int beacon) {
        TrilaterationFunction triFunc = new TrilaterationFunction(positions,distances);
        LinearLeastSquaresSolver lSolver = new LinearLeastSquaresSolver(triFunc);
        NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(triFunc, new LevenbergMarquardtOptimizer());

        RealVector linearCalculatePosition = lSolver.solve();
        LeastSquaresOptimizer.Optimum nonLinearCalculatePosition = nlSolver.solve();

        double[] calculatedPosition = nonLinearCalculatePosition.getPoint().toArray();

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        setGoalsPosition(calculatedPosition,1);
        setGoalsPosition(calculatedPosition,2);
        setGoalsPosition(calculatedPosition,3);
        //currentActivity.setGoalsPosition(calculatedPosition,beacon);
        //Log.i("MAP POC - BEACON", "X = " + String.valueOf((float)calculatedPosition[0]) + " -- Y = " + String.valueOf((float)calculatedPosition[1]));
    }

    public void setGoalsPosition(double[] calculatedPosition, int beacon){
        switch (beacon) {
            case 1:
                //Position du beacon 1
                currentActivity.getGoal1().setX(settingScale(Float.toString((float) calculatedPosition[0]),currentActivity.getGoal1(),"x"));
                currentActivity.getGoal1().setY(settingScale(Float.toString((float) calculatedPosition[1]),currentActivity.getGoal1(),"y"));
                //Log.i("MAP POC - BEACON1 (m)", "X = " + calculatedPosition[0] + " -- Y = " + calculatedPosition[1]);
                break;
            case 2:
                //Position du beacon 2
                currentActivity.getGoal2().setX(settingScale(Float.toString((float) calculatedPosition[0]),currentActivity.getGoal2(),"x"));
                currentActivity.getGoal2().setY(settingScale(Float.toString((float) calculatedPosition[1]),currentActivity.getGoal2(),"y"));
                //Log.i("MAP POC - BEACON2", "X = " + calculatedPosition[0] + " -- Y = " + calculatedPosition[1]);
                break;
            case 3:
                //Position du beacon 3
                currentActivity.getGoal3().setX(settingScale(Float.toString((float) calculatedPosition[0]),currentActivity.getGoal3(),"x"));
                currentActivity.getGoal3().setY(settingScale(Float.toString((float) calculatedPosition[1]),currentActivity.getGoal3(),"y"));
                //Log.i("MAP POC - BEACON3", "X = " + calculatedPosition[0] + " -- Y = " + calculatedPosition[1]);
                break;
        }

        if(currentActivity.getFragment()!=null) {
            if (currentActivity.getFragment().getmSelectedItems().contains(0)) {
                currentActivity.getGoal1().setVisibility(View.VISIBLE);
            } else {
                currentActivity.getGoal1().setVisibility(View.INVISIBLE);
            }

            if (currentActivity.getFragment().getmSelectedItems().contains(1)) {
                currentActivity.getGoal2().setVisibility(View.VISIBLE);
            } else {
                currentActivity.getGoal2().setVisibility(View.INVISIBLE);
            }

            if (currentActivity.getFragment().getmSelectedItems().contains(2)) {
                currentActivity.getGoal3().setVisibility(View.VISIBLE);
            } else {
                currentActivity.getGoal3().setVisibility(View.INVISIBLE);
            }
        }
        //getWindow().getDecorView().findViewById(android.R.id.content).postInvalidate();
    }

    //Mise à l'échelle en fonction de la position
    public Float settingScale(String positionXY, ImageView object, String type){
        if(type=="x"){
            return ((Float.parseFloat(positionXY) * currentActivity.getDeviceWidth() / Float.parseFloat(currentActivity.getWidthRoom()))
                    + Float.parseFloat(currentActivity.getOffsetMap_x())) - object.getWidth()/2;
        }
        else if(type=="y"){
            return ((Float.parseFloat(positionXY) * currentActivity.getDeviceHeight() / Float.parseFloat(currentActivity.getHeightRoom()))
                    + Float.parseFloat(currentActivity.getOffsetMap_y())) - object.getHeight()/2;
        }
        else{
            return -1f;
        }
    }
}
