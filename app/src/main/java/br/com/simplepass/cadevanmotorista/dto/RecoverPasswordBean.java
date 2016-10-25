package br.com.simplepass.cadevanmotorista.dto;

/**
 * Bean that is send to the server when the password will be remade
 */
public class RecoverPasswordBean {
    private String phoneNumber;

    public RecoverPasswordBean(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
