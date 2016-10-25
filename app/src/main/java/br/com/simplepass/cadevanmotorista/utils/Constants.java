package br.com.simplepass.cadevanmotorista.utils;

/**
 * Created by leandro on 3/4/16.
 */
public class Constants {

    /* --------------------  Account Manager -------------------------------- */
    public static final String ACCOUNT_TYPE = "br.com.simplepass.cadevanmotorista";
    public static final String ACCOUNT_TOKEN_TYPE = "user";

    public static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    /* -------------------- Acesso ao servidor -------------------------------  */
    public static final String CLIENT_ID = "app";
    public static final String CLIENT_SECRET = "top_secret";

    /* -------------------- Constantes de Places ----------------------------- */
    public static final int ON_DELIVERING = 2;

    public static final String SHARED_PREFS_NAME = "br.com.simplepass.cadevan.PREFS";

    public class SharedPrefs{
        public static final String CHOSEN_DRIVER_ID = "br.com.simplepass.cadevan.CHOSEN_DRIVER_ID";
        public static final String KEY_PUSH_REGISTRATION_ID = "KEY_PUSH_REGISTRATION_ID";
    }
}
