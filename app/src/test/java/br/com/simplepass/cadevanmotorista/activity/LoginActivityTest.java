package br.com.simplepass.cadevanmotorista.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import br.com.simplepass.cadevanmotorista.BuildConfig;
import br.com.simplepass.cadevanmotorista.R;

import static org.assertj.android.api.Assertions.assertThat;

/**
 * Created by leandro on 5/24/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = "src/main/AndroidManifest.xml", packageName = "br.com.simplepass.cadevanmotorista")
public class LoginActivityTest {

    LoginActivity activity;
    ShadowActivity shadowActivity;
    EditText phoneET;
    EditText passwordET;
    Button btnLogin;
    Button btnRecoverPassword;
    Button btnRegister;


    @Before
    public void setup(){
        activity = Robolectric.setupActivity(LoginActivity.class);
        shadowActivity = Shadows.shadowOf(activity);
        phoneET =  (EditText) activity.findViewById(R.id.phone_number_login);
        passwordET =  (EditText) activity.findViewById(R.id.password);
        btnLogin = (Button) activity.findViewById(R.id.btn_login);
        btnRecoverPassword = (Button) activity.findViewById(R.id.btn_recover_password);
        btnRegister = (Button) activity.findViewById(R.id.btn_register);
    }

    @Test
    public void testNotNull(){
        assertThat(phoneET).isNotNull();
        assertThat(passwordET).isNotNull();
        assertThat(btnLogin).isNotNull();
        assertThat(btnRegister).isNotNull();
    }

    @Test
    public void insertPhoneRemoveInitialZeroTest(){
        assertThat(phoneET).isNotNull();

        phoneET.setText("03191889992");
        Shadows.shadowOf(phoneET).getWatchers().get(1).afterTextChanged(phoneET.getText());
        Assert.assertEquals("3191889992", phoneET.getText().toString());

        phoneET.setText("553191889992");
        Shadows.shadowOf(phoneET).getWatchers().get(1).afterTextChanged(phoneET.getText());
        Assert.assertEquals("553191889992", phoneET.getText().toString());
    }

    @Test
    public void shortPasswordTest(){
        passwordET =  (EditText) activity.findViewById(R.id.password);
        phoneET.setText("03191889992");
        passwordET.setText("1234");

        btnLogin.performClick();
        assertThat(passwordET).hasError();
    }

    @Test
    public void testStartRecoverPasswordActivity(){
        btnRecoverPassword.performClick();

        Intent intent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals(intent.toString(), (new Intent(activity, RecoverPasswordActivity.class)).toString());
    }

    @Test
    public void testStartRegisterActivity(){
        btnRegister.performClick();

        Intent intent = shadowActivity.getNextStartedActivity();
        Assert.assertEquals(intent.toString(), (new Intent(activity, RegisterActivity.class)).toString());
    }
}
