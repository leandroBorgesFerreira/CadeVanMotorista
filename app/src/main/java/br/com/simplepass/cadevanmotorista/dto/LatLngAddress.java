package br.com.simplepass.cadevanmotorista.dto;

/**
 * Class that has the information about an address and the lat and lng of this address
 */
public class LatLngAddress {
    private String address;
    private double lat;
    private double lng;

    public LatLngAddress(String address, double lat, double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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
