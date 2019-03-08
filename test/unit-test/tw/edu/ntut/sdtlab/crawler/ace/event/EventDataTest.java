package tw.edu.ntut.sdtlab.crawler.ace.event;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EventDataTest {
    private Element element;
    private EventData eventData;

    @Before
    public void setup() throws DocumentException {
        final String XML_SOURCE = "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\"" +
                " long-clickable=\"false\" package=\"com.google.android.wearable.app\"" +
                " password=\"false\" resource-id=\"\" scrollable=\"false\" selected=\"false\" text=\"\">\n" +
                "\t\t</node>\n";
        InputStream inputStream = new ByteArrayInputStream(XML_SOURCE.getBytes());
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        this.element = document.getRootElement();
        this.eventData = new EventData(this.element);
    }

    @Test
    public void testParseBounds() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = EventData.class.getDeclaredMethod("parseCoordinate", String.class);
        method.setAccessible(true);
        String[] actual = (String[]) method.invoke(this.eventData, "[0,0][320,320]");
        String[] expect = {"0", "0", "320", "320"};
        assertArrayEquals(expect, actual);
        assertEquals(0, Integer.parseInt(actual[0]));
        assertEquals(0, Integer.parseInt(actual[1]));
        assertEquals(320, Integer.parseInt(actual[2]));
        assertEquals(320, Integer.parseInt(actual[3]));
    }

    @Test
    public void testGetUpperLeftPoint() {
        Point expect = new Point(0, 0);
        Point actual = this.eventData.getUpperLeftPoint();
        assertEquals(expect.x(), actual.x());
        assertEquals(expect.y(), actual.y());
    }

    @Test
    public void testGetLowerRightPoint() {
        Point expect = new Point(320, 320);
        Point actual = this.eventData.getLowerRightPoint();
        assertEquals(expect.x(), actual.x());
        assertEquals(expect.y(), actual.y());
    }

    @Test
    public void testSetSwipeDirectionRight() throws NoSuchFieldException, IllegalAccessException {
        SwipeEvent.Direction expect = SwipeEvent.Direction.RIGHT;
        this.eventData.setSwipeDirection(expect);
        Field field = EventData.class.getDeclaredField("swipeDirection");
        field.setAccessible(true);
        SwipeEvent.Direction actual = (SwipeEvent.Direction) field.get(this.eventData);
        assertEquals(expect, actual);
    }

    @Test
    public void testGetSwipeDirectionRight() throws NoSuchFieldException, IllegalAccessException {
        SwipeEvent.Direction expect = SwipeEvent.Direction.RIGHT;
        Field field = EventData.class.getDeclaredField("swipeDirection");
        field.setAccessible(true);
        field.set(this.eventData, expect);
        assertEquals(expect, this.eventData.getSwipeDirection());
    }
}
