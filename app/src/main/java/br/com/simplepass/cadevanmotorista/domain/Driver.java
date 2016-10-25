package br.com.simplepass.cadevanmotorista.domain;

import br.com.simplepass.cadevanmotorista.utils.FormUtils;

/**
 * Classe que simboliza o motorista
 */
public class Driver {
    private Long id;
    private String phoneNumber;
    private String password;
    private String email;
    private String company;
    private String name;
    private String gcmToken;
    private String os;
    private Long trackingCode;

    public static final int TRACKING_CODE_BASE = 1135;

    public Driver(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = FormUtils.md5(password);
    }

    public Driver(Long id, String phoneNumber, String password, String email, String name, String company) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.password = FormUtils.md5(password);
        this.email = email;
        this.name = name;
        this.os = "android";
        this.trackingCode = id + TRACKING_CODE_BASE;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = FormUtils.md5(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Long getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(Long trackingCode) {
        this.trackingCode = trackingCode;
    }

    public static int getTrackingCodeBase() {
        return TRACKING_CODE_BASE;
    }
}
