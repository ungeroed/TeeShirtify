package dagger.Test;

import org.junit.Test;
import org.mockito.Mockito;
import javax.inject.Inject;
import ungeroed.com.teeshirtify.ApiHandler;
import ungeroed.com.teeshirtify.ApiHandlerConsumer;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Morten on 03/09/2017.
 */

public class ApiHandlerDaggerTest {

    @Inject ApiHandler handler;

    @Inject
    ApiHandlerConsumer consumer;

    @org.junit.Before
    public void setUp() {
        TestComponent component = DaggerTestComponent.builder().apiModule(new TestApiHandlerModule()).build();
        component.inject(this);
    }

    @Test
    public void testDivideNumberOfElements_isDouble() {
        Mockito.when(handler.getProductCount(new String[]{"All","All"})).thenReturn(2);
        Double res = consumer.DivideNumberOfElementsWith(4);
        assert (res instanceof Double);
    }

    @Test
    public void testDivideNumberOfElements_isCorrect() {
        Mockito.when(handler.getProductCount(new String[]{"All","All"})).thenReturn(2);
        Double res = consumer.DivideNumberOfElementsWith(4);
        assertEquals(res.doubleValue(), 0.5);
    }

    @Test
    public void testDivideNumberOfElements_zero() {
        Mockito.when(handler.getProductCount(new String[]{"All","All"})).thenReturn(2);
        Double res = consumer.DivideNumberOfElementsWith(0);
        assertNull(res);
    }


    @Test
    public void testDivideNumberOfElements_maxInt() {
        Mockito.when(handler.getProductCount(new String[]{"All","All"})).thenReturn(2);
        Double res = consumer.DivideNumberOfElementsWith(Integer.MAX_VALUE);
        assertNotNull(res);
    }

    @Test
    public void testDivideNumberOfElements_minInt() {
        Mockito.when(handler.getProductCount(new String[]{"All","All"})).thenReturn(2);
        Double res = consumer.DivideNumberOfElementsWith(Integer.MIN_VALUE);
        assertNotNull(res);
    }

    @Test(expected = NullPointerException.class)
    public void testDivideNumberOfElements_null() {
        Mockito.when(handler.getProductCount(new String[]{"All","All"})).thenReturn(2);
        consumer.DivideNumberOfElementsWith(null);
    }


}
