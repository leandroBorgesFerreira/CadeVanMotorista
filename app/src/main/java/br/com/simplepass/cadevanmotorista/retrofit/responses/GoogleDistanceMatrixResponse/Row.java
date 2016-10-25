package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse;

import java.util.List;

/**
 * Created by leandro on 3/22/16.
 */
public class Row {
    private List<Element> elements;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
