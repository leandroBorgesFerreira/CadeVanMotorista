package br.com.simplepass.cadevanmotorista.location;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import br.com.simplepass.cadevanmotorista.BuildConfig;
import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import io.realm.RealmList;

/**
 * Created by leandro on 5/22/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class PathManagerTest {
    PathManager pathManager;
    Path mPath;
    DistanceToDestinationRequester mDistanceToDestinationRequester;
    RealmList<Place> placeList;


    @Before
    public void setup(){
        mPath = Mockito.mock(Path.class);

        Place place1 = Mockito.mock(Place.class);
        Place place2 = Mockito.mock(Place.class);
        Place place3 = Mockito.mock(Place.class);

        EntityInsidePlace entityInsidePlace = Mockito.mock(EntityInsidePlace.class);
        Mockito.doReturn("Leandro").when(entityInsidePlace).getName();

        RealmList<EntityInsidePlace> entityList = new RealmList<>();
        entityList.add(entityInsidePlace);

        Mockito.doReturn(entityList).when(place1).getEntitysInsidePlace();
        Mockito.doReturn(entityList).when(place2).getEntitysInsidePlace();
        Mockito.doReturn(entityList).when(place3).getEntitysInsidePlace();


        placeList = new RealmList<>();
        placeList.add(place1);
        placeList.add(place2);
        placeList.add(place3);

        Mockito.doReturn(placeList).when(mPath).getPlaces();

        mDistanceToDestinationRequester = Mockito.mock(DistanceToDestinationRequester.class);

        pathManager = new PathManager(
                RuntimeEnvironment.application.getApplicationContext(),
                mPath,
                mDistanceToDestinationRequester);
    }

    @Test()
    public void testNextPlace(){
        Assert.assertEquals(placeList.get(0), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
        Assert.assertTrue(pathManager.nextPlace());
        Assert.assertEquals(placeList.get(1), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
    }

    @Test
    public void testChangePlace(){
        Assert.assertEquals(placeList.get(0), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
        pathManager.changePlace(1);
        Assert.assertEquals(placeList.get(1), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
    }

    @Test
    public void testNextPlaceInTheLastPlace(){
        Assert.assertEquals(placeList.get(0), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
        pathManager.changePlace(2);
        Assert.assertEquals(placeList.get(2), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
        Assert.assertFalse(pathManager.nextPlace());
        Assert.assertEquals(placeList.get(2), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
    }

    @Test
    public void testNextPlaceWithRemovedPlace(){
        Assert.assertEquals(placeList.get(0), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
        pathManager.removeFromDelivering(placeList.get(1));
        Assert.assertTrue(pathManager.nextPlace());
        Assert.assertEquals(placeList.get(2), pathManager.getPlace(pathManager.getCurrentPlaceNumber()));
    }
}
