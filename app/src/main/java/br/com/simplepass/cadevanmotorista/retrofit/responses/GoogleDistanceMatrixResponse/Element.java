package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse;

/**
 * Created by leandro on 3/22/16.
 */
public class Element {
    private String status;
    private Data duration;
    private Data distance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getDuration() {
        return duration;
    }

    public void setDuration(Data duration) {
        this.duration = duration;
    }

    public Data getDistance() {
        return distance;
    }

    public void setDistance(Data distance) {
        this.distance = distance;
    }
}
