package br.com.simplepass.cadevanmotorista.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;
import br.com.simplepass.cadevanmotorista.domain_realm.Place;
import io.realm.RealmList;

/**
 * Created by leandro on 5/21/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {

    EntityInsidePlace insidePlaceStudent1;
    EntityInsidePlace insidePlaceStudent2;
    EntityInsidePlace insidePlaceStudent3;
    EntityInsidePlace insidePlaceSchool;
    Place placeFull;
    Place place1Student;
    Place place1School;
    Place placeWithCoordinates;

    public static final String PHONE_insidePlaceStudent1 = "3191889992";
    public static final String PHONE_insidePlaceStudent2 = "3199009900";
    public static final String PHONE_insidePlaceStudent3 = "3199119911";
    public static final String BRASIL_CODE = "55";

    public static final double LATITUDE = -40.0;
    public static final double LONGITUDE = -38.9;

    @Before
    public void setup(){
        insidePlaceStudent1 = Mockito.mock(EntityInsidePlace.class);
        Mockito.doReturn("Leandro Borges").when(insidePlaceStudent1).getName();
        Mockito.doReturn(BRASIL_CODE).when(insidePlaceStudent1).getCountryCode();
        Mockito.doReturn(PHONE_insidePlaceStudent1).when(insidePlaceStudent1).getPhone();
        Mockito.doReturn(EntityInsidePlace.TYPE_STUDENT).when(insidePlaceStudent1).getType();

        insidePlaceStudent2 = Mockito.mock(EntityInsidePlace.class);
        Mockito.doReturn("Gabriela Lima").when(insidePlaceStudent2).getName();
        Mockito.doReturn(BRASIL_CODE).when(insidePlaceStudent2).getCountryCode();
        Mockito.doReturn(PHONE_insidePlaceStudent2).when(insidePlaceStudent2).getPhone();
        Mockito.doReturn(EntityInsidePlace.TYPE_STUDENT).when(insidePlaceStudent2).getType();

        insidePlaceStudent3 = Mockito.mock(EntityInsidePlace.class);
        Mockito.doReturn("Pedro Henrique").when(insidePlaceStudent3).getName();
        Mockito.doReturn(BRASIL_CODE).when(insidePlaceStudent3).getCountryCode();
        Mockito.doReturn(PHONE_insidePlaceStudent3).when(insidePlaceStudent3).getPhone();
        Mockito.doReturn(EntityInsidePlace.TYPE_STUDENT).when(insidePlaceStudent3).getType();

        insidePlaceSchool = Mockito.mock(EntityInsidePlace.class);
        Mockito.doReturn("Marista").when(insidePlaceSchool).getName();
        Mockito.doReturn(EntityInsidePlace.TYPE_SCHOOL).when(insidePlaceSchool).getType();

        List<EntityInsidePlace> entityListFull = new RealmList<>();
        entityListFull.add(insidePlaceStudent1);
        entityListFull.add(insidePlaceStudent2);
        entityListFull.add(insidePlaceStudent3);
        entityListFull.add(insidePlaceSchool);

        List<EntityInsidePlace> entityList1Student = new RealmList<>();
        entityList1Student.add(insidePlaceStudent1);

        List<EntityInsidePlace> entityList1School = new RealmList<>();
        entityList1School.add(insidePlaceSchool);

        placeWithCoordinates = Mockito.mock(Place.class);
        Mockito.doReturn(LATITUDE).when(placeWithCoordinates).getLatitude();
        Mockito.doReturn(LONGITUDE).when(placeWithCoordinates).getLongitude();

        placeFull = Mockito.mock(Place.class);
        Mockito.doReturn(entityListFull).when(placeFull).getEntitysInsidePlace();

        place1Student = Mockito.mock(Place.class);
        Mockito.doReturn(entityList1Student).when(place1Student).getEntitysInsidePlace();

        place1School = Mockito.mock(Place.class);
        Mockito.doReturn(entityList1School).when(place1School).getEntitysInsidePlace();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Test()
    public void testGetNameOfPlace(){
        Assert.assertEquals("Leandro Borges, Gabriela Lima, Pedro Henrique, Marista", Utils.getNameOfPlace(placeFull));
        Assert.assertEquals("Leandro Borges", Utils.getNameOfPlace(place1Student));
        Assert.assertEquals("Marista", Utils.getNameOfPlace(place1School));
    }

    @Test()
    public void testGetPhonesOfPlace(){
        List<String> phoneList = new ArrayList<>();
        phoneList.add(BRASIL_CODE+PHONE_insidePlaceStudent1);
        phoneList.add(BRASIL_CODE+PHONE_insidePlaceStudent2);
        phoneList.add(BRASIL_CODE+PHONE_insidePlaceStudent3);

        Assert.assertEquals(phoneList, Utils.getPhonesOfPlaceAsList(placeFull));

        phoneList = new ArrayList<>();
        phoneList.add(BRASIL_CODE+PHONE_insidePlaceStudent1);

        Assert.assertEquals(phoneList, Utils.getPhonesOfPlaceAsList(place1Student));

        phoneList = new ArrayList<>();

        Assert.assertEquals(phoneList, Utils.getPhonesOfPlaceAsList(place1School));
    }

    @Test()
    public void testGetNameOfPlaceWithNullPlace(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("place não pode ser null");

        Utils.getNameOfPlace(null);
    }

    @Test()
    public void testGetPhonesOfPlaceWithNullPlace(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("place não pode ser null");

        Utils.getPhonesOfPlaceAsList(null);
    }

    @Test()
    public void testGetLocationOfPlaceWithNullPlace(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("place não pode ser null");

        Utils.getLocationOfPlace(null);
    }

    @Test()
    public void testAddressToStringWithNullAddress(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("address não pode ser null");

        Utils.addressToString(null);
    }
}
