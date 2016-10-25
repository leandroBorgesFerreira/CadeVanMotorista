package br.com.simplepass.cadevanmotorista.dto;

/**
 * Result of a form
 */
public abstract class PlaceFormResult {
    protected String name;
    protected String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public abstract String getType();

}
