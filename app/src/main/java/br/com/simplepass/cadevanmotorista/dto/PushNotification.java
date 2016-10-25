package br.com.simplepass.cadevanmotorista.dto;

import java.util.List;


/**
 * Class the implements PushMessage. It has the phone number list, the message and the type of message.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class PushNotification implements PushMessage{
    List<String> phoneNumberList;
    Object message;
    String type;

    public PushNotification(List<String> phoneNumberList, String message, String type) {
        this.phoneNumberList = phoneNumberList;
        this.message = message;
        this.type = type;
    }

    public List<String> getPhoneNumberList() {
        return phoneNumberList;
    }

    public void setPhoneNumberList(List<String> phoneNumberList) {
        this.phoneNumberList = phoneNumberList;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
