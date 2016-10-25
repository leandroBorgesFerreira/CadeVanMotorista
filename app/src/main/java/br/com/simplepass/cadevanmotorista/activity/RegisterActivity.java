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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain.Driver;
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
 * Activity dedicated to make possible the user to register in the app.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class RegisterActivity extends AppCompatActivity {
    @Bind(R.id.sign_in_email) EditText mEmailView;
    @Bind(R.id.sign_in_first_name) EditText mFirstNameView;
    @Bind(R.id.sign_in_last_name) EditText mLastNameView;
    @Bind(R.id.sign_in_password) EditText mPasswordView;
    @Bind(R.id.sign_in_company) EditText mCompany;
    @Bind(R.id.sign_in_phone_number) EditText mPhoneNumberView;
    @Bind(R.id.sign_in_phone_country) EditText mPhoneCountryView;
    private ProgressDialog          mProgressDialog;

    /**
     * Show progress information. (Ex: progress dialog, progress button, etc...)
     *
     * @param show
     */
    private void showProgress(boolean show){
        if(show){
            mProgressDialog = ProgressDialog.show(
                    this, null, getString(R.string.dialog_wait_register), true);
        } else{
            mProgressDialog.dismiss();
        }
    }

    /**
     * Show information to the user. Ex: the login was not successful
     *
     * @param message The message that will the presented to the user.
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
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        mPhoneNumberView.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mPhoneNumberView.addTextChangedListener(FormUtils.areaCodeFixer());
        mPhoneNumberView.requestFocus();

        /*mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });*/
    }

    /**
     * Click listener to the confirmation button.
     */
    @OnClick(R.id.btn_sign_in)
    public void attemptRegister(){
        // Reseta os erros
        mCompany.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mPhoneNumberView.setError(null);
        mPhoneCountryView.setError(null);

        // Guarda os valores antes de tentar fazer o registro
        String email            = mEmailView.getText().toString(); //Esse getText precisa mesmo??
        String password         = mPasswordView.getText().toString();
        String firstName        = mFirstNameView.getText().toString();
        String lastName         = mLastNameView.getText().toString();
        String phoneNumber      = mPhoneNumberView.getText().toString();
        String phoneCountry     = mPhoneCountryView.getText().toString();
        String company          = mCompany.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Checa se o nome foi preenchido
        if(TextUtils.isEmpty(firstName)){
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if(TextUtils.isEmpty(lastName)){
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }

        if(TextUtils.isEmpty(phoneCountry)){
            mPhoneCountryView.setError(getString(R.string.error_field_required));
            focusView = mPhoneCountryView;
            cancel = true;
        }

        // Checa se o usuário digitou alguma coisa na senha
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

        // Checa se o email é valido e se o usuário digitou alguma coisa.
        if(TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!FormUtils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            /* Houve algum erro ao preencher o formulário. Vamos dar foco onde o erro aconteceu */
            focusView.requestFocus();
        } else {
            /* Mostra uma barrinha de progresso e manda a AsyncTask para fazer o registro do
             * usuario */
            String phone = (phoneCountry + phoneNumber).replaceAll("\\D", "");
            String name = firstName + " " + lastName;

            final Driver driver = new Driver(0L, phone, password, email, name, company);

            showProgress(true);
            RegisterTask registerTask = new RegisterTask(driver);
            registerTask.execute();
        }
    }

    /**
     * login is the second part of the login process. After the oauth is generated, the app asks the server
     * for the token. if the token is received, it stores the needed information in the app. DO NOT STORE THE
     * PASSWORD!!
     * @param driver
     */
    private void login(final Driver driver){
        CadeVanMotoristaClient clientService = ServiceGenerator.createService(CadeVanMotoristaClient.class,
                Constants.CLIENT_ID,
                Constants.CLIENT_SECRET);
        final Call<OAuthTokenResponse> callLogin = clientService.getAuthToken(driver.getPhoneNumber(),
                driver.getPassword(),
                "password");
        callLogin.enqueue(new Callback<OAuthTokenResponse>() {
            @Override
            public void onResponse(Call<OAuthTokenResponse> call, retrofit2.Response<OAuthTokenResponse> response) {
                if(response.isSuccessful()){
                    showProgress(false);

                    OAuthTokenResponse tokenResponse = response.body();
                    AccountManager accountManager = AccountManager.get(RegisterActivity.this);
                    Account account =
                            new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE);

                    Bundle bundle = new Bundle();
                    bundle.putString("driverId", String.valueOf(driver.getId()));
                    bundle.putString("email", driver.getEmail());
                    bundle.putString("phoneNumber", driver.getPhoneNumber());
                    bundle.putString("name", driver.getName());
                    bundle.putString("trackingCode", String.valueOf(driver.getTrackingCode()));

                    accountManager.addAccountExplicitly(account, null, bundle);
                    accountManager.setAuthToken(account,
                            tokenResponse.getTokenType(),
                            tokenResponse.getAccessToken());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                                RegisterActivity.this);

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent, activityOptions.toBundle());
                    } else {
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                } else{
                    showProgress(false);
                    showInfoDialog("falha ao registrar. Tente novamente");
                }
            }

            @Override
            public void onFailure(Call<OAuthTokenResponse> call, Throwable t) {
                showProgress(false);
                showInfoDialog("falha ao registrar. Tente novamente");
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, LoginActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, activityOptions.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /**
     * RegisterTask register in the push notification tool and register in the server for a oauth token.
     *
     * @see android.os.AsyncTask
     */
    private class RegisterTask extends AsyncTask<Void, Void, Boolean> {
        private Driver mDriver;

        public RegisterTask(Driver driver) {
            this.mDriver = driver;
        }

        /**
         * Method to do the tasks in the background
         * @param params no params are needed.
         * @return return the success of the task.
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            CadeVanMotoristaClient clientService = ServiceGenerator.createService(CadeVanMotoristaClient.class);
            Call<Driver> callRegister = clientService.register(mDriver);

            try {
                try {
                    String registrationId = Pushy.register(getApplicationContext());

                    mDriver.setGcmToken(registrationId);

                    Response<Driver> registerResponse = callRegister.execute();
                    if (registerResponse.isSuccessful()) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.SharedPrefs.KEY_PUSH_REGISTRATION_ID, registrationId);
                        editor.apply();

                        mDriver.setId(registerResponse.body().getId());
                        mDriver.setTrackingCode(registerResponse.body().getTrackingCode());

                        return true;
                    }
                } catch (PushyException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return false;
        }

        /**
         * If the registraing is ok, continue the login process. If not, warn the user.
         * @param registered get the success information of the backgounrd task.
         */
        @Override
        protected void onPostExecute(Boolean registered) {
            if(registered){
                login(mDriver);
            } else{
                showProgress(false);
                Toast.makeText(RegisterActivity.this, "Falha ao registrar. Tente novamente",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
