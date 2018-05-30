package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import com.lemmingapex.trilateration.LinearLeastSquaresSolver;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

/**
 * Class used for calculate distances thanks to 3 -> N value.
 */
public class Trilateration {
    private MapActivity currentActivity;

    public Trilateration(MapActivity currentActivity){
        this.currentActivity=currentActivity;
    }

    /**
     * Launch the trilateration function
     * @param positions
     * @param distances
     * @param bcn
     */
    public void launchTrilateration(double [][] positions, double[] distances, final Beacon bcn) {
        TrilaterationFunction triFunc = new TrilaterationFunction(positions,distances);
        LinearLeastSquaresSolver lSolver = new LinearLeastSquaresSolver(triFunc);
        NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(triFunc, new LevenbergMarquardtOptimizer());

        RealVector linearCalculatePosition = lSolver.solve();
        LeastSquaresOptimizer.Optimum nonLinearCalculatePosition = nlSolver.solve();

        final double[] calculatedPosition = nonLinearCalculatePosition.getPoint().toArray();
        /**Placement des beacons cibles (exécution par le Thread UI)*/
        new Thread(new Runnable() {
            public void run() {
                currentActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        currentActivity.setGoalsPosition(calculatedPosition,bcn);
                    }
                });
            }
        }).start();
    }
}
