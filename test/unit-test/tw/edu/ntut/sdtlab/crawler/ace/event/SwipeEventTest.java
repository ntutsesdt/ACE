package tw.edu.ntut.sdtlab.crawler.ace.event;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SwipeEventTest {
    private AndroidEvent swipeEvent;
    private Config config;
    private Device device;
    private Point startPoint;
    private Point endPoint;
    private String bounds;
    private Point upperLeftPoint, lowerRightPoint;
    private final int OFFSET = 1;

    @Before
    public void setUp() throws Exception {
        this.startPoint = new Point(200, 200);
        this.endPoint = new Point(700, 200);
        this.bounds = "[20,20][200,200]";
        this.config = new Config();
        this.device = new Device(config.getDeviceSerialNum());
        this.upperLeftPoint = new Point(0, 0);
        this.lowerRightPoint = new Point(320, 320);
        EventData eventData = new EventData(this.bounds, "swipe right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        this.swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
    }

    @Test
    public void testExecuteOn_SwipeEvent() throws InterruptedException, IOException, ExecuteCommandErrorException {
        EventData eventData = new EventData(this.bounds, "swipe right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        this.swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
        this.swipeEvent.executeOn(device);
    }

    @Test
    public void testExecuteOn_ScrollEvent() throws InterruptedException, IOException, ExecuteCommandErrorException {
        EventData eventData = new EventData(this.bounds, "scroll right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        this.swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SCROLL);
        this.swipeEvent.executeOn(device);
    }

    @Test
    public void testGetReportLabel_SwipeEvent() throws ExecuteCommandErrorException, InterruptedException, IOException {
        EventData eventData = new EventData(this.bounds, "swipe right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        this.swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
        String expect = "swipe" + "(\\\"Right\\\")";
        String actual = this.swipeEvent.getReportLabel();
        assertEquals(expect, actual);
    }

    @Test
    public void testGetReportLabel_ScrollEvent() throws ExecuteCommandErrorException, InterruptedException, IOException {
        EventData eventData = new EventData(this.bounds, "scroll right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        this.swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SCROLL);
        String expect = "scroll" + "(\\\"Right\\\")";
        String actual = this.swipeEvent.getReportLabel();
        assertEquals(expect, actual);
    }

    @Test
    public void testGetName_ScrollEvent() {
        EventData eventData = new EventData(this.bounds, "scroll right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        this.swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SCROLL);
        String expect = "Scroll Event";
        String actual = this.swipeEvent.getName();
        assertEquals(expect, actual);
    }

    @Test
    public void testCreateLabel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SwipeEvent.class.getDeclaredMethod("createLabel", Point.class, Point.class, SwipeEvent.Type.class);
        method.setAccessible(true);
        EventData eventData = new EventData(this.bounds, "swipe right");
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        SwipeEvent _swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
        method.invoke(_swipeEvent, this.startPoint, this.endPoint, SwipeEvent.Type.SWIPE);
    }

    @Test
    public void testGetLeftCenterPoint() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SwipeEvent.class.getDeclaredMethod("getLeftCenterPoint", Point.class, Point.class);
        method.setAccessible(true);
        Point actual = (Point) method.invoke(this.swipeEvent, this.upperLeftPoint, this.lowerRightPoint);
        assertEquals(0 + OFFSET, actual.x());
        assertEquals(160, actual.y());
    }

    @Test
    public void testGetRightCenterPoint() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SwipeEvent.class.getDeclaredMethod("getRightCenterPoint", Point.class, Point.class);
        method.setAccessible(true);
        Point actual = (Point) method.invoke(this.swipeEvent, this.upperLeftPoint, this.lowerRightPoint);
        assertEquals(320 - OFFSET, actual.x());
        assertEquals(160, actual.y());
    }

    @Test
    public void testGetUpCenterPoint() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SwipeEvent.class.getDeclaredMethod("getUpCenterPoint", Point.class, Point.class);
        method.setAccessible(true);
        Point actual = (Point) method.invoke(this.swipeEvent, this.upperLeftPoint, this.lowerRightPoint);
        assertEquals(160, actual.x());
        assertEquals(0 + OFFSET, actual.y());
    }

    @Test
    public void testGetDownCenterPoint() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = SwipeEvent.class.getDeclaredMethod("getDownCenterPoint", Point.class, Point.class);
        method.setAccessible(true);
        Point actual = (Point) method.invoke(this.swipeEvent, this.upperLeftPoint, this.lowerRightPoint);
        assertEquals(160, actual.x());
        assertEquals(320 - OFFSET, actual.y());
    }
}
