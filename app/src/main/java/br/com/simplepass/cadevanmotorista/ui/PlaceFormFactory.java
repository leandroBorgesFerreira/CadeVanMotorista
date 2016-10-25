package br.com.simplepass.cadevanmotorista.ui;

import android.app.Activity;
import android.view.ViewGroup;

import br.com.simplepass.cadevanmotorista.activity.ProgressShower;

/**
 * Created by leandro on 3/28/16.
 */
public class PlaceFormFactory {
    private PlaceFormFactory() {
    }

    public static PlaceForm createPlaceForm(Activity activity, ProgressShower progressShower,
                                            ViewGroup container, int type){


        switch (type){
            case (PlaceForm.SCHOOL):
                return new SchoolForm(activity, container, progressShower);
            case (PlaceForm.STUDENT):
                return new StudentForm(activity, container, progressShower);
            default:
                throw new IllegalArgumentException("tipo desconhecido");
        }
    }
}
