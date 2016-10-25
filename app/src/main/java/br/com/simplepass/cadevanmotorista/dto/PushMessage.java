package br.com.simplepass.cadevanmotorista.dto;

import java.util.List;

/**
 * Class that has the information about a push notification
 */
public interface PushMessage {
    String TYPE_ARRIVE_TIME = "arriveTime";
    String TYPE_SHARE_PATH = "sharePath";

    List<String> getPhoneNumberList();
    Object getMessage();
    String getType();
}
