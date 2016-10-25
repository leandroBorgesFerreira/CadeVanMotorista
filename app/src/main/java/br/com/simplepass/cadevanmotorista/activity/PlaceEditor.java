package br.com.simplepass.cadevanmotorista.activity;

import android.widget.EditText;

import br.com.simplepass.cadevanmotorista.adapters.EntitiesInPlaceAdapter;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;

/**
 * Interface the a class that edit places need to implement in order to be able to adit places.
 */
public interface PlaceEditor {
    /**
     * The class must get what edit text is in focus. It is import to save the place change, in a second moment.
     *
     * @param entity entity of the edit text in focus
     * @param editText edit text in focus
     * @param type type of the entity
     */
    void getEditTextInFocus(EntityInsidePlace entity, EditText editText, int type);

    /**
     * Method to save de changes made in the entity.
     *
     * @param entity entity of the edit text in focus
     * @param editText edit text in focus
     * @param type type of the entity
     */
    void saveEditTextInFocus(EntityInsidePlace entity, EditText editText, int type);

    /**
     * The user may not like the places beeing toghther. With this method it is possible to split then.
     *
     * @param place The place which the entity is inside.
     * @param entityInsidePlace The entity to be separated.
     * @return
     */
    boolean splitEntity(Place place, EntityInsidePlace entityInsidePlace);
}
