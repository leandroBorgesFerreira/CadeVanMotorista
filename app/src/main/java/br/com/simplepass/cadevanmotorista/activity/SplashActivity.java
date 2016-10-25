package br.com.simplepass.cadevanmotorista.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.utils.Constants;
import me.pushy.sdk.Pushy;


/**
 * Splash Activity. Wait a little and go to another activity.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Pushy.listen(this);

        waitAndMove();
    }

    /**
     * Method to keep the sistem wating some time and go to another activity. 
     */
    private void waitAndMove(){
        int SPLASH_TIME_OUT = 1500;

        new Handler().postDelayed(new Runnable() {

            /* Vamos mostrar nossa splash screen, depois passa para o app */
            @Override
            public void run() {
                AccountManager accountManager = AccountManager.get(SplashActivity.this);
                Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);

                if (accounts.length > 0) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                                SplashActivity.this);

                        startActivity(intent, activityOptions.toBundle());
                    } else {
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                                SplashActivity.this);

                        startActivity(intent, activityOptions.toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
