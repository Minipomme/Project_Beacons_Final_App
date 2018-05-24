package beacon.projetco.dii.polytech.tours.univ.beaconsfinder;

import android.widget.ImageView;

public class Beacon {

    int name;
    double [] distances;
    int x;
    int y;
    ImageView image;

    public Beacon(int name,double [] distances){
        this.name=name;
        this.distances=distances;
        this.x=0;
        this.y=0;
    }

    public Beacon(){
        this.name=0;
        this.distances=null;
        this.x=0;
        this.y=0;
    }

    public Beacon(int name){
        this.name=name;
        this.distances=null;
        this.x=0;
        this.y=0;
    }

    public int getName(){
        return this.name;
    }

    public double [] getDistances(){
        return this.distances;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageView getImage() {
        return image;
    }

    public void setDistances(double [] distances){
        this.distances=distances;
    }

    public void setName(int name){
        this.name=name;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

}
