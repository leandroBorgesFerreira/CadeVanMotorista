package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse;

/**
 * Created by leandro on 4/12/16.
 */
public class Geometry {
    LocationResponse location;

    public LocationResponse getLocation() {
        return location;
    }

    public void setLocation(LocationResponse location) {
        this.location = location;
    }
}
