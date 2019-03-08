package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EquivalentStateHandlerTest {
    private EquivalentStateHandler handler;
    final String TEST_DATA_FOLDER_PATH = "test/unit-test/tw/edu/ntut/sdtlab/crawler/ace/test-data";
    final String path = TEST_DATA_FOLDER_PATH + "/test-attribute-equal/";

    @Before
    public void setup() {
        this.handler = new EquivalentStateHandler();
    }

    @Test
    public void testAreTheSame() throws DocumentException {
//        final String path = "test_gui_pages/test_attribute_equal/";
//        final String path = TEST_DATA_FOLDER_PATH + "/test_attribute_equal/";
        Element existingElement = (Element) this.readDocument(path + "existingLayoutForEquivalentStateHandlerTest.xml").getRootElement().elementIterator().next();
        Element newElement = (Element) this.readDocument(path + "newLayoutForEquivalentStateHandlerTest.xml").getRootElement().elementIterator().next();
        System.out.print(existingElement.toString());
        boolean actual = this.handler.areTheSame(existingElement, newElement);
        if(!Config.IGNORE_BOUNDS_ATTRIBUTE)
            assertFalse(actual);
        else
            assertTrue(actual);
    }

    @Test
    public void testIsAttributeEqual() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        final String path = "test_gui_pages/test_attribute_equal/";
//        final String path = TEST_DATA_FOLDER_PATH + "/test_attribute_equal/";
        Element existingElement = (Element) this.readDocument(path + "existingLayoutForEquivalentStateHandlerTest.xml").getRootElement().elementIterator().next();
        Element newElement = (Element) this.readDocument(path + "newLayoutForEquivalentStateHandlerTest.xml").getRootElement().elementIterator().next();
        Method method = EquivalentStateHandler.class.getDeclaredMethod("isAttributeEqual", Element.class, Element.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.handler, existingElement, newElement);
        assertTrue(actual);
    }

    @Test
    public void testMin() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EquivalentStateHandler.class.getDeclaredMethod("min", int.class, int.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.handler, 1,2);
        assertEquals(1, actual);
    }

    @Test
    public void testMinWhenEqual() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EquivalentStateHandler.class.getDeclaredMethod("min", int.class, int.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.handler, 1,1);
        assertEquals(1, actual);
    }

    @Test
    public void testMinWhenSecondParameterIsMin() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EquivalentStateHandler.class.getDeclaredMethod("min", int.class, int.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.handler, 2,1);
        assertEquals(1, actual);
    }

    @Test
    public void testMax() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EquivalentStateHandler.class.getDeclaredMethod("max", int.class, int.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.handler, 1,2);
        assertEquals(2, actual);
    }

    @Test
    public void testMaxWhenEqual() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EquivalentStateHandler.class.getDeclaredMethod("max", int.class, int.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.handler, 1,1);
        assertEquals(1, actual);
    }

    @Test
    public void testMaxWhenSecondParameterIsMax() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EquivalentStateHandler.class.getDeclaredMethod("max", int.class, int.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.handler, 2,1);
        assertEquals(2, actual);
    }

    private Document readDocument(String dotPath) throws DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(dotPath);
    }
}
