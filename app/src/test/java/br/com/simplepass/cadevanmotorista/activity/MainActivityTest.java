package br.com.simplepass.cadevanmotorista.activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import br.com.simplepass.cadevanmotorista.BuildConfig;

/**
 * Created by leandro on 5/24/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = "src/main/AndroidManifest.xml", packageName = "br.com.simplepass.cadevanmotorista")
public class MainActivityTest {
    MainActivity mActivity;

    @Before
    public void setup(){
        mActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testBind(){

    }
}
