package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleGeocodeResponse;

/**
 * Created by leandro on 4/12/16.
 */
public class Result {
    String formatted_address;
    Geometry geometry;
    /*Podem ser acrescentados outros campos, pois a resposta do google contém mais informações. */

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
