package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ClickTypeErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.NullPackageNameException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RandomWalkIteratorTest {
    private CrawlingIterator randomWalkIterator;
    private StateGraph stateGraph;
    private AndroidCrawler androidCrawler;


    @Before
    public void setup() throws InterruptedException, ClickTypeErrorException, MultipleListOrGridException, NullPackageNameException, DocumentException, ExecuteCommandErrorException, IOException, NoSuchFieldException, IllegalAccessException {
        this.stateGraph = new StateGraph();
        Config config = new Config();
        Device device = new Device(config.getDeviceSerialNum());
        this.androidCrawler = new AndroidCrawler(device, stateGraph);
        GUIState desktopState = new GUIState(DocumentHelper.parseText("<node bounds=\"[0,0][1080,1776]\" checkable=\"false\"" +
                " checked=\"false\" class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\" enabled=\"true\"" +
                " focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.sonyericsson.home\"" +
                " password=\"false\" resource-id=\"\" scrollable=\"false\" selected=\"false\" text=\"\"></node>"), new ArrayList<AndroidEvent>());
        desktopState.setActivityName("Launcher");
        Field desktopStateField = AndroidCrawler.class.getDeclaredField("desktopState");
        desktopStateField.setAccessible(true);
        desktopStateField.set(this.androidCrawler, desktopState);
        this.randomWalkIterator = new RandomWalkIterator(this.stateGraph, this.androidCrawler);
    }

    @Test
    public void testGetRandomEventFromCurrentStateWhenCurrentStateTheSame() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, DocumentException, NoSuchFieldException {
        GUIState currentState = new GUIState(DocumentHelper.parseText("<test></test>"), new ArrayList<AndroidEvent>());
        currentState.setUnrecognizableState(true);    // for activityName and packageName not null
        Field currentStateField = AndroidCrawler.class.getDeclaredField("currentState");
        currentStateField.setAccessible(true);
        currentStateField.set(this.androidCrawler, currentState);

        Field recordAtTheSameStateTimeField = RandomWalkIterator.class.getDeclaredField("recordAtTheSameStateTime");
        recordAtTheSameStateTimeField.setAccessible(true);
        long actualRecordAtTheSameStateTime = (long) recordAtTheSameStateTimeField.get(this.randomWalkIterator);
        assertEquals(0, actualRecordAtTheSameStateTime);
        Method method = RandomWalkIterator.class.getDeclaredMethod("getRandomEventFromCurrentState");
        method.setAccessible(true);
        List<AndroidEvent> events = (List<AndroidEvent>) method.invoke(this.randomWalkIterator); // call getRandomEventFromCurrentState for first time
        assertTrue(events.isEmpty());
        actualRecordAtTheSameStateTime = (long) recordAtTheSameStateTimeField.get(this.randomWalkIterator);
        assertNotEquals(0, actualRecordAtTheSameStateTime);
        long expectRecordAtTheSameStateTime = actualRecordAtTheSameStateTime;
        events = (List<AndroidEvent>) method.invoke(this.randomWalkIterator); // call getRandomEventFromCurrentState again
        assertTrue(events.isEmpty());
        actualRecordAtTheSameStateTime = (long) recordAtTheSameStateTimeField.get(this.randomWalkIterator);
        assertEquals(expectRecordAtTheSameStateTime, actualRecordAtTheSameStateTime);   // recordAtTheSameStateTime should the same
    }

    @Test
    public void testGetRandomEventFromCurrentStateWhenCurrentStateNotTheSame() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, DocumentException {
        GUIState currentState = new GUIState(DocumentHelper.parseText("<test></test>"), new ArrayList<AndroidEvent>());
        currentState.setUnrecognizableState(true);    // for activityName and packageName not null
        Field currentStateField = AndroidCrawler.class.getDeclaredField("currentState");
        currentStateField.setAccessible(true);
        currentStateField.set(this.androidCrawler, currentState);

        Field recordAtTheSameStateTimeField = RandomWalkIterator.class.getDeclaredField("recordAtTheSameStateTime");
        recordAtTheSameStateTimeField.setAccessible(true);
        long actualRecordAtTheSameStateTime = (long) recordAtTheSameStateTimeField.get(this.randomWalkIterator);
        assertEquals(0, actualRecordAtTheSameStateTime);
        Method method = RandomWalkIterator.class.getDeclaredMethod("getRandomEventFromCurrentState");
        method.setAccessible(true);
        List<AndroidEvent> events = (List<AndroidEvent>) method.invoke(this.randomWalkIterator); // call getRandomEventFromCurrentState for first time
        assertTrue(events.isEmpty());
        actualRecordAtTheSameStateTime = (long) recordAtTheSameStateTimeField.get(this.randomWalkIterator);
        assertNotEquals(0, actualRecordAtTheSameStateTime);
        long expectRecordAtTheSameStateTime = actualRecordAtTheSameStateTime;
        currentState = new GUIState(DocumentHelper.parseText("<node bounds=\"[0,0][1080,1920]\" class=\"android.widget.FrameLayout\"></node>"), new ArrayList<AndroidEvent>());  // change a new currentState
        currentState.setUnrecognizableState(true);    // for activityName and packageName not null
        currentStateField.set(this.androidCrawler, currentState);
        events = (List<AndroidEvent>) method.invoke(this.randomWalkIterator); // call getRandomEventFromCurrentState again
        assertTrue(events.isEmpty());
        actualRecordAtTheSameStateTime = (long) recordAtTheSameStateTimeField.get(this.randomWalkIterator);
        assertNotEquals(expectRecordAtTheSameStateTime, actualRecordAtTheSameStateTime);    // recordAtTheSameStateTime should change
    }
}
