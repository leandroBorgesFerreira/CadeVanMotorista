package br.com.simplepass.cadevanmotorista.ui;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by leandro on 5/21/16.
 */
public class PlaceFormFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test()
    public void testPlaceFormFactoryWithNull(){
        expectedException.expect(IllegalArgumentException.class);

        PlaceFormFactory.createPlaceForm(null, null, null, -1);
    }
}
