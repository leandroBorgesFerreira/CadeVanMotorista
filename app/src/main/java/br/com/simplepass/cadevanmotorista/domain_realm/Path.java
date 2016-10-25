package br.com.simplepass.cadevanmotorista.domain_realm;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leandro on 3/11/16.
 */
public class Path extends RealmObject {
    @PrimaryKey
    private Long id;

    private String name;
    private String direction;
    private RealmList<Place> places;

    public static final String DIRECTION_HOME = "home";
    public static final String DIRECTION_SCHOOL = "school";

    public Path() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public RealmList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(RealmList<Place> places) {
        this.places = places;
    }
}
