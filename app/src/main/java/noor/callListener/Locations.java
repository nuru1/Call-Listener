package noor.callListener;

/**
 * Created by asif on 25-Feb-18.
 */

public class Locations {

    int id;
    String time;
    String lattitude,longitude;

    public Locations() {
    }

    public Locations(int id, String time, String lattitude, String longitude) {
        this.id = id;
        this.time = time;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
