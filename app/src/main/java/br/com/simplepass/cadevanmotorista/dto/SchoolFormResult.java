package br.com.simplepass.cadevanmotorista.dto;

import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;

/**
 * PlaceFormResult of the type school
 */
public class SchoolFormResult extends PlaceFormResult {
    @Override
    public String getType() {
        return EntityInsidePlace.TYPE_SCHOOL;
    }
}
