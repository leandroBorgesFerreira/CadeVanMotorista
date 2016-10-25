package br.com.simplepass.cadevanmotorista;

import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;

import br.com.simplepass.cadevanmotorista.application.CustomApplication;

/**
 * Created by leandro on 5/24/16.
 */
public class TestApplication extends CustomApplication implements TestLifecycleApplication {
    @Override
    public void onCreate() {
        super.onCreate();


    }


    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {

    }

    @Override
    public void afterTest(Method method) {

    }
}
