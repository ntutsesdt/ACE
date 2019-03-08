package tw.edu.ntut.sdtlab.crawler.ace.event;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class AndroidEventFactoryTest {
    private AndroidEventFactory androidEventFactory;

    @Before
    public void setUp() throws Exception {
        this.androidEventFactory = new AndroidEventFactory();
    }

    @Test
    public void testCreateAndroidEvent_withBackKeyEventType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EventData eventData = new EventData();
        Method method = AndroidEventFactory.class.getDeclaredMethod("createAndroidEvent", String.class, EventData.class);
        method.setAccessible(true);
        AndroidEvent actual = (AndroidEvent) method.invoke(this.androidEventFactory, "BackKey", eventData);
        assertNotNull(actual);
    }

    @Test(expected = NullPointerException.class)
    public void testCreateAndroidEvent_withNonExistEventType() throws Exception {
        EventData eventData = new EventData();
        this.androidEventFactory.createAndroidEvent("nonExistEvent", eventData);
    }

}