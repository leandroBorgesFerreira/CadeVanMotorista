package br.com.simplepass.cadevanmotorista.domain_realm;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leandro on 3/14/16.
 */
public class Place extends RealmObject{
    @PrimaryKey
    private Long id;

    private double latitude;
    private double longitude;

    private RealmList<EntityInsidePlace> entitysInsidePlace;

    public Place() {
    }

    public Place(Long id, double latitude, double longitude, RealmList<EntityInsidePlace> entitysInsidePlace) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.entitysInsidePlace = entitysInsidePlace;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public RealmList<EntityInsidePlace> getEntitysInsidePlace() {
        return entitysInsidePlace;
    }

    public void setEntitysInsidePlace(RealmList<EntityInsidePlace> entitysInsidePlace) {
        this.entitysInsidePlace = entitysInsidePlace;
    }
}
