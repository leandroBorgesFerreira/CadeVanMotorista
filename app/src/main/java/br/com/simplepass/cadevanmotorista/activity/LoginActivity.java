package br.com.simplepass.cadevanmotorista.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import java.io.IOException;
import java.util.List;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain.Driver;
import br.com.simplepass.cadevanmotorista.retrofit.AccessToken;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.retrofit.responses.OAuthTokenResponse;
import br.com.simplepass.cadevanmotorista.utils.Constants;
import br.com.simplepass.cadevanmotorista.utils.FormUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.pushy.sdk.Pushy;
import me.pushy.sdk.exceptions.PushyException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login activity. This class is responsable for the registration of the user. He can insert the login
 * information or decide to register in the app
 *
 * @author Leandro Borges Ferreira
 * @see android.app.Activity
 */
public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.phone_number_login) EditText mPhoneNumberView;
    @Bind(R.id.phone_number_login_country) EditText mPhoneCountryView;
    @Bind(R.id.password) EditText mPasswordView;
    private ProgressDialog mProgressDialog;

    private void showProgress(boolean show){
        if(show){
            mProgressDialog = ProgressDialog.show(
                    this, null, getString(R.string.dialog_wait_register), true);
        } else{
            mProgressDialog.dismiss();
        }
    }

    /**
     * Info dialog. Needed to warn the show some information to user. Ex: the login was not successful
     * @param message Message that will be displayed to the user
     */
    private void showInfoDialog(String message){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("OK", dialogClickListener).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // Set up the login form.
        mPhoneNumberView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPhoneNumberView.addTextChangedListener(FormUtils.areaCodeFixer());

        /*mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });*/
    }

    /**
     * Recover password click listener
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_recover_password)
    public void recoverPassword(){
        Intent intent = new Intent(this, RecoverPasswordActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    LoginActivity.this);

            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * Register click listener
     * @param view
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_register)
    public void goToRegisterActivity(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                    LoginActivity.this);

            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    /**
     * Login click listener. It looks for mistakes in the form. If it find, warn the user. If the form
     * is OK, send the information to the server.
     */
    @OnClick(R.id.btn_login)
    public void attemptLogin() {
        // Reset errors.
        mPhoneCountryView.setError(null);
        mPhoneNumberView.setError(null);
        mPasswordView.setError(null);

        // Guarda os valores no momento que vai tentar logar
        String phoneCountry = mPhoneCountryView.getText().toString(); //Esse getText precisa mesmo??
        String phoneNumber = mPhoneNumberView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Checa se o usuário digitou alguma coisa
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
            //Checa se o usuá digitou um password valido.
        } else if (!FormUtils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password_too_short));
            focusView = mPasswordView;
            cancel = true;
        }

        // Checa se a pessoa digitou alguma coisa no numero de telefone.
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        if (TextUtils.isEmpty(phoneCountry)) {
            mPhoneCountryView.setError(getString(R.string.error_field_required));
            focusView = mPhoneCountryView;
            cancel = true;
        }

        if (cancel) {
            /* Houve um erro. Não tenta logar e foca no primeiro erro registrado. */
            focusView.requestFocus();
        } else {
            /* Mostra o desenho de progresso e lança uma async task para realizar o login no
             * servidor. */
            String phone = (phoneCountry + phoneNumber).replaceAll("\\D", "");

            final Driver driver = new Driver(phone, password);

            showProgress(true);
            LoginTask loginTask = new LoginTask();
            loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, driver);
        }
    }

    /**
     * AsyncTask dedicated to send the user information to the server.
     *
     *
     * @see android.os.AsyncTask
     */
    private class LoginTask extends AsyncTask<Driver, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Driver... params) {
            Driver driver = params[0];

            CadeVanMotoristaClient clientService = ServiceGenerator.createService(
                    CadeVanMotoristaClient.class,
                    Constants.CLIENT_ID,
                    Constants.CLIENT_SECRET);

            Call<OAuthTokenResponse> call = clientService.getAuthToken(driver.getPhoneNumber(),
                    driver.getPassword(),
                    "password");

            CadeVanMotoristaClient client = ServiceGenerator.createService(CadeVanMotoristaClient.class);
            Call<List<Driver>> callGetDriver = client.getDriversByPhoneNumber(driver.getPhoneNumber());
            try {
                try {
                    Response<OAuthTokenResponse> responseOAuth = call.execute();
                    Response<List<Driver>> responseGetDriver = callGetDriver.execute();

                    if (responseOAuth.isSuccessful() && responseGetDriver.isSuccessful()) {
                        OAuthTokenResponse oAuthTokenResponse = responseOAuth.body();
                        Driver driverWithId = responseGetDriver.body().get(0);

                        AccountManager accountManager = AccountManager.get(LoginActivity.this);
                        Account account =
                                new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE);

                        Bundle bundle = new Bundle();
                        bundle.putString("driverId", String.valueOf(driverWithId.getId()));
                        bundle.putString("email", driverWithId.getEmail());
                        bundle.putString("phoneNumber", driverWithId.getPhoneNumber());
                        bundle.putString("name", driverWithId.getName());
                        bundle.putString("trackingCode", String.valueOf(driverWithId.getTrackingCode()));

                        if (!accountManager.addAccountExplicitly(account, null, bundle)) {
                            return false;
                        }

                        accountManager.setAuthToken(account,
                                oAuthTokenResponse.getTokenType(),
                                oAuthTokenResponse.getAccessToken());

                        String registrationId = Pushy.register(getApplicationContext());

                        SharedPreferences sharedPreferences = getSharedPreferences(
                                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.SharedPrefs.KEY_PUSH_REGISTRATION_ID, registrationId);
                        editor.apply();

                        driverWithId.setGcmToken(registrationId);

                        Call<Void> callUpdateUser = client.updateUDriver(driverWithId, driverWithId.getId());
                        Response<Void> responseGcm = callUpdateUser.execute();

                        return responseGcm.isSuccessful();
                    } else {
                        return false;
                    }
                } catch (PushyException e){
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException e){
                Log.d("login", e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            showProgress(false);

            if(aBoolean) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            LoginActivity.this);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            } else{
                showInfoDialog("falha ao registrar. Tente novamente");
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}