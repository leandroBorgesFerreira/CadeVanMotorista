package br.com.simplepass.cadevanmotorista.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by leandro on 5/21/16.
 */
public class FormUtilsTest {

    @Test()
    public void testMD5(){
        Assert.assertEquals("25a957b0616f6e7d4128bdfb7f18c972", FormUtils.md5("fraseDeTeste"));
    }
}
