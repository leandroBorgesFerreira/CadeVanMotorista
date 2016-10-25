package br.com.simplepass.cadevanmotorista.retrofit.responses.GoogleDistanceMatrixResponse;

/**
 * Created by leandro on 3/22/16.
 */
public class Data {
    private int value;
    private String text;

    public Data(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
