package br.com.simplepass.cadevanmotorista.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Serviço para acesso do Account Authenticator
 */
public class AccountService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }

}
