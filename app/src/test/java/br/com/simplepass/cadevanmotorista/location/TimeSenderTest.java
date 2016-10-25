package br.com.simplepass.cadevanmotorista.location;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

import br.com.simplepass.cadevanmotorista.BuildConfig;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import br.com.simplepass.cadevanmotorista.retrofit.CadeVanMotoristaClient;
import io.realm.RealmList;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


/**
 * Created by leandro on 5/22/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class TimeSenderTest {

    private TimeToArriveSender mTimeToArriveSender;
    private Place mPlace;
    private MockWebServer server;

    @Before()
    public void setup() throws IOException {
        server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK));
        server.start();

        mTimeToArriveSender = new TimeSender(RuntimeEnvironment.application.getApplicationContext(),
                MockServiceGenerator.createService(CadeVanMotoristaClient.class, server.url("/").toString()));

        mPlace = Mockito.mock(Place.class);

        EntityInsidePlace entityInsidePlace = Mockito.mock(EntityInsidePlace.class);
        Mockito.doReturn("Leandro").when(entityInsidePlace).getName();
        Mockito.doReturn("55").when(entityInsidePlace).getCountryCode();
        Mockito.doReturn("3199009900").when(entityInsidePlace).getPhone();
        Mockito.doReturn(EntityInsidePlace.TYPE_STUDENT).when(entityInsidePlace).getType();

        RealmList<EntityInsidePlace> entityList = new RealmList<>();
        entityList.add(entityInsidePlace);

        Mockito.doReturn(entityList).when(mPlace).getEntitysInsidePlace();
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test()
    public void testEvaluateIfSent() throws Exception{
        /*Data data = Mockito.mock(Data.class);
        Mockito.doReturn("4 minutos").when(data).getText();
        Mockito.doReturn(4).when(data).getValue();

        Assert.assertFalse(mTimeToArriveSender.evaluateIfSent(mPlace, 10));

        Path path = new Path();
        path.setDirection(Path.DIRECTION_SCHOOL);


        mTimeToArriveSender.sendTimeToArrive(path, mPlace, path.getDirection(), data);
        Thread.sleep(15000);
        RecordedRequest request1 = server.takeRequest();
        String ha = request1.getPath();
        Assert.assertFalse(mTimeToArriveSender.evaluateIfSent(mPlace, 10));
        Assert.assertTrue(mTimeToArriveSender.evaluateIfSent(mPlace, 4));*/
    }


}
