package br.com.simplepass.cadevanmotorista.location;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse.Data;

/**
 * Created by leandro on 4/6/16.
 */
public interface TimeToArriveSender {

    int ARRIVED = 0;

    void sendArrived(Path path, Place place, String direction);
    void sendTimeToArrive(Path path, Place place, String direction, Data time);
    boolean evaluateIfSent(Place place, int time);
}
