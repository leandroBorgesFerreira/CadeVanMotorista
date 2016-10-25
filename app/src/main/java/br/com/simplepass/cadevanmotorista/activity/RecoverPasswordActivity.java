package br.com.simplepass.cadevanmotorista.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain.Driver;
import br.com.simplepass.cadevanmotorista.dto.RecoverPasswordBean;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import br.com.simplepass.cadevanmotorista.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity responsable to make possible to the user to recover his password.
 *
 * @author Leandro Borges Ferreira. lehen01@gmail.com
 */
public class RecoverPasswordActivity extends AppCompatActivity {
    @Bind(R.id.phone_recover_password) EditText phoneEditText;
    @Bind(R.id.country_code) EditText countryCode;
    private ProgressDialog mProgressDialog;

    private void showProgress(boolean show){
        if(show){
            mProgressDialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
                    getString(R.string.register_message), true);
        } else{
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }
    }

    /**
     * Click listener of the button to send the information.
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.btn_send_recover_password)
    public void sendPassword(){
        String phone = (countryCode.getText().toString() +
                phoneEditText.getText().toString()).replaceAll("\\D", "");

        CadeVanMotoristaClient client = ServiceGenerator.createService(CadeVanMotoristaClient.class);
        Call<Driver> callRecoverPassword = client.recoverPassword(new RecoverPasswordBean(phone));
        showProgress(true);
        callRecoverPassword.enqueue(new Callback<Driver>() {
            @Override
            public void onResponse(Call<Driver> call, Response<Driver> response) {
                showProgress(false);
                if(response.isSuccessful()) {
                    Utils.showInfoDialog(RecoverPasswordActivity.this,
                            String.format(getString(R.string.email_sent), response.body().getEmail()));
                } else{
                    Utils.showInfoDialog(RecoverPasswordActivity.this, "Usuário não encontrado");
                }
            }
            @Override
            public void onFailure(Call<Driver> call, Throwable t) {
                showProgress(false);
                Utils.showInfoDialog(RecoverPasswordActivity.this, getString(R.string.error_no_internet));
            }
        });
    }
}
