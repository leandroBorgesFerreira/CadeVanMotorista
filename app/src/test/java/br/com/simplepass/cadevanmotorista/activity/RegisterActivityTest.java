package br.com.simplepass.cadevanmotorista.activity;

import android.app.Activity;
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
import org.robolectric.internal.Shadow;
import org.robolectric.shadows.ShadowActivity;

import br.com.simplepass.cadevanmotorista.BuildConfig;
import br.com.simplepass.cadevanmotorista.R;
import butterknife.Bind;
import butterknife.ButterKnife;

import static org.assertj.android.api.Assertions.assertThat;

/**
 * Created by leandro on 5/24/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = "src/main/AndroidManifest.xml", packageName = "br.com.simplepass.cadevanmotorista")
public class RegisterActivityTest {
    RegisterActivity mActivity;

    @Before
    public void setup(){
        mActivity = Robolectric.setupActivity(RegisterActivity.class);
    }

    @Test
    public void testBind(){
        assertThat(mActivity.mEmailView).isNotNull();
        assertThat(mActivity.mFirstNameView).isNotNull();
        assertThat(mActivity.mLastNameView).isNotNull();
        assertThat(mActivity.mPasswordView).isNotNull();
        assertThat(mActivity.mCompany).isNotNull();
        assertThat(mActivity.mPhoneNumberView).isNotNull();
        assertThat(mActivity.mPhoneCountryView).isNotNull();
    }

    @Test
    public void formTestHappyPath(){
        //ToDo: Fazer quando aprender a simular o Retrofit.
    }

    @Test
    public void formTestWrongData(){
        mActivity.mEmailView.setText("emailerrado");
        mActivity.mFirstNameView.setText("");
        mActivity.mLastNameView.setText("");
        mActivity.mPasswordView.setText("12");
        mActivity.mCompany.setText("");
        mActivity.mPhoneNumberView.setText("");
        mActivity.mPhoneCountryView.setText("");

        Button btnRegister = (Button) mActivity.findViewById(R.id.btn_sign_in);
        assertThat(btnRegister).isNotNull();
        btnRegister.performClick();

        assertThat(mActivity.mEmailView).hasError();
        assertThat(mActivity.mFirstNameView).hasError();
        assertThat(mActivity.mLastNameView).hasError();
        assertThat(mActivity.mPasswordView).hasError();
        //!assertThat(mActivity.mCompany).hasError();
        assertThat(mActivity.mPhoneNumberView).hasError();
        assertThat(mActivity.mPhoneCountryView).hasError();

        /*Intent intent = Shadows.shadowOf(mActivity).getNextStartedActivity();
        Assert.assertEquals(intent.toString(), (new Intent(mActivity, MainActivity.class).toString()));*/
    }


}
