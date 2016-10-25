package br.com.simplepass.cadevanmotorista.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import io.realm.RealmObject;

/**
 * Created by leandro on 4/22/16.
 */
public class PathShare extends RealmObject{
    private String driverName;
    private Path path;

    //Necessário para RealmObjects
    public PathShare() {
    }

    public PathShare(String driverName, Path path) {
        this.driverName = driverName;
        this.path = path;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
