package br.com.simplepass.cadevanmotorista.ui;

import br.com.simplepass.cadevanmotorista.dto.PlaceFormResult;

/**
 * Created by leandro on 3/28/16.
 */
public interface PlaceForm {
    int STUDENT = 1;
    int SCHOOL = 2;

    void insertForm();
    void removeForm();
    PlaceFormResult getPlace();
    boolean isCorrect();
}
