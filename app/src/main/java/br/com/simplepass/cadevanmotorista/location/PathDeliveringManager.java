package br.com.simplepass.cadevanmotorista.location;

import android.location.Location;

import java.util.List;

import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;

/**
 * Created by leandro on 4/22/16.
 */
public interface PathDeliveringManager {
    String ACTION_CHANGE_PLACE = "br.com.simplepass.cadevanmotorista.action.ACTION_CHANGE_PLACE";
    String ACTION_PATH_FINISH = "br.com.simplepass.cadevanmotorista.action.ACTION_PATH_FINISH";

    Path getPath(); //Esse método não deveria ter... porém foi necessário
    boolean nextPlace();
    void changePlace(int position);
    void removeFromDelivering(Place place);
    boolean isPlaceRemoved(Place place);
    Place getCurrentPlace();
    int getCurrentPlaceNumber();
    void setInsidePlace(Place place);
    Place getInsidePlace();
    Place getPlace(int position);
    List<Place> getAllPlaces();
}
