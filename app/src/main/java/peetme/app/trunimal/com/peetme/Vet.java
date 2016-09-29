package peetme.app.trunimal.com.peetme;

/**
 * Created by elias on 21/9/2016.
 */

public class Vet {

    String name;
    double latitude;
    double longitude;

    public Vet() {
    }

    public Vet(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
