package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse;

/**
 * Created by leandro on 4/12/16.
 */
public class LocationResponse {
    double lat;
    double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
