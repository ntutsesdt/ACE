package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.equivalent.CComponentEquivalentStrategy;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.event.*;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.Point;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NFSIteratorTest {
    private StateGraph stateGraph;
    private Device device;
    private AndroidCrawler androidCrawler;
    private String deviceSerialNum;
    private AndroidEvent checkEvent, scrollEvent, swipeEvent, editTextEvent, clickEvent, longClickEvent, backKeyEvent, menuKeyEvent;
    private Point point;
    private String testStr, twoXMLSource, threeXMLSource, fourXMLSource;
    private Document document;
    private GUIState firstState;

    @Before
    public void setup() throws DocumentException, InterruptedException, ExecuteCommandErrorException, ClickTypeErrorException, IOException, NullPackageNameException, MultipleListOrGridException {
        Config config = new Config();
        this.stateGraph = new StateGraph();
        this.stateGraph.setEquivalentStragegy(new CComponentEquivalentStrategy());
        this.deviceSerialNum = config.getDeviceSerialNum();
        this.device = new Device(this.deviceSerialNum);
        this.androidCrawler = new AndroidCrawler(device, stateGraph);
        twoXMLSource = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +
                "<hierarchy rotation=\"0\"><node index=\"0\" text=\"\" resource-id=\"\" " +
                "class=\"android.widget.FrameLayout\" package=\"com.example.oil.myapplication2\" " +
                "content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" " +
                "focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" " +
                "selected=\"false\" bounds=\"[0,0][1080,1920]\" /></hierarchy>";
        point = new Point(0, 0);
        testStr = "test";
        EventData eventData = new EventData("[0,0][100,100]", this.testStr);
        checkEvent = new ClickEvent(eventData, ClickEvent.Type.Check);
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        scrollEvent = new SwipeEvent(eventData, SwipeEvent.Type.SCROLL);
        swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
        this.initialEditTextEvent();
        this.initialClickEvent(point, testStr);
        longClickEvent = new LongClickEvent(eventData);
        backKeyEvent = new BackKeyEvent();
        menuKeyEvent = new MenuKeyEvent();
        document = DocumentHelper.parseText(twoXMLSource);
        firstState = new GUIState(document, new ArrayList<AndroidEvent>());
    }

    private void initialClickEvent(Point point, String testStr) {
        EventData eventData = new EventData();
        eventData.setCenterPoint(point);
        eventData.setTempLabel(testStr);
        clickEvent = new ClickEvent(eventData, ClickEvent.Type.Click);
    }

    private void initialEditTextEvent() {
        EventData eventData = new EventData("[0,0][100,100]", "editText");
        eventData.setBackspaceCount(0);
        eventData.setValue("create editText");
        List<EventData> eventDatas = new ArrayList<>();
        eventDatas.add(eventData);
        this.editTextEvent = new EditTextEvent(eventDatas);
    }

    @Test
    public void testFirst() throws NoSuchFieldException, IllegalAccessException, InterruptedException, ExecuteCommandErrorException, CrawlerControllerInitialErrorException, DocumentException, IOException, NoSuchMethodException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        NFSIterator nfsIterator = new NFSIterator(this.stateGraph, this.androidCrawler);
        nfsIterator.first();
        Field currentStateField = AndroidCrawler.class.getDeclaredField("currentState");
        currentStateField.setAccessible(true);
        GUIState currentState = (GUIState) currentStateField.get(this.androidCrawler);
        assertEquals(currentState.getPackageName(), Config.PACKAGE_NAME);
        Field eventSequenceField = NFSIterator.class.getDeclaredField("eventSequence");
        eventSequenceField.setAccessible(true);
        List<AndroidEvent> eventSequence = (List<AndroidEvent>) eventSequenceField.get(nfsIterator);
        assertTrue(!eventSequence.isEmpty());
    }

    @Test
    public void testGetEventSequence() throws NoSuchFieldException, IllegalAccessException, InterruptedException, ExecuteCommandErrorException, CrawlerControllerInitialErrorException, DocumentException, IOException, NoSuchMethodException {
        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);
        eventSequence.add(clickEvent);
        eventSequence.add(longClickEvent);
        eventSequence.add(scrollEvent);
        eventSequence.add(swipeEvent);
        eventSequence.add(backKeyEvent);
        eventSequence.add(checkEvent);
        eventSequence.add(editTextEvent);

        NFSIterator iterator = new NFSIterator(stateGraph, androidCrawler);
        Field eventSequenceField = NFSIterator.class.getDeclaredField("eventSequence");
        eventSequenceField.setAccessible(true);
        eventSequenceField.set(iterator, eventSequence);
        assertEquals(eventSequence, iterator.getEventSequence());
    }

    @Test
    public void testNextWhenEventSequenceIsNotEmpty() throws NoSuchFieldException, IllegalAccessException, InterruptedException, ExecuteCommandErrorException, CrawlerControllerInitialErrorException, DocumentException, IOException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        ArrayList<AndroidEvent> events = new ArrayList<>();
        events.add(clickEvent);
        events.add(longClickEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, events);
        Field currentState = AndroidCrawler.class.getDeclaredField("currentState");
        currentState.setAccessible(true);
        currentState.set(androidCrawler, firstState);

        NFSIterator NFSIterator = new NFSIterator(stateGraph, androidCrawler);
        Field field = NFSIterator.class.getDeclaredField("eventSequence");
        field.setAccessible(true);
        NFSIterator.next();
        List<AndroidEvent> eventSequence = (List<AndroidEvent>) field.get(NFSIterator);

        assertEquals(clickEvent, eventSequence.get(0));
    }

    @Test
    public void testNextWhenEventSequenceIsEmpty() throws NoSuchFieldException, IllegalAccessException, InterruptedException, ExecuteCommandErrorException, CrawlerControllerInitialErrorException, DocumentException, IOException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        Field currentState = AndroidCrawler.class.getDeclaredField("currentState");
        currentState.setAccessible(true);
        currentState.set(androidCrawler, firstState);

        NFSIterator NFSIterator = new NFSIterator(stateGraph, androidCrawler);
        Field field = NFSIterator.class.getDeclaredField("eventSequence");
        field.setAccessible(true);
        new File(Utility.getReportPath() + "/States").mkdirs();
        NFSIterator.next();
        List<AndroidEvent> eventSequence = (List<AndroidEvent>) field.get(NFSIterator);
        assertFalse(eventSequence.isEmpty());

        // delete directory
        File directory = new File(Utility.getReportPath());
        deleteFilesInDirectory(directory);
        directory.delete();
    }

    @Test
    public void testGetUnfiredEventSequence() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayList<AndroidEvent> events = new ArrayList<>();
        events.add(longClickEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, events);
        Field currentState = AndroidCrawler.class.getDeclaredField("currentState");
        currentState.setAccessible(true);
        currentState.set(androidCrawler, firstState);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field field = StateGraph.class.getDeclaredField("states");
        field.setAccessible(true);
        field.set(stateGraph, states);

        NFSIterator NFSIterator = new NFSIterator(stateGraph, androidCrawler);
        Method unfiredEventSequence = NFSIterator.class.getDeclaredMethod("getUnfiredEventSequence", GUIState.class);
        unfiredEventSequence.setAccessible(true);
        List<AndroidEvent> actual = (List<AndroidEvent>) unfiredEventSequence.invoke(NFSIterator, firstState);
        assertEquals(longClickEvent, actual.get(0));
    }

    @Test
    public void testGetUnfiredEventSequenceWhenOverCrossAppEventThreshold() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ArrayList<AndroidEvent> events = new ArrayList<>();
        events.add(longClickEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, events);
        Field currentState = AndroidCrawler.class.getDeclaredField("currentState");
        currentState.setAccessible(true);
        currentState.set(androidCrawler, firstState);

        Field firstStateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        firstStateCrossApp.setAccessible(true);
        firstStateCrossApp.set(firstState, Config.CROSS_APP_EVENT_THRESHHOLD);

        NFSIterator NFSIterator = new NFSIterator(stateGraph, androidCrawler);
        Method unfiredEventSequence = NFSIterator.class.getDeclaredMethod("getUnfiredEventSequence", GUIState.class);
        unfiredEventSequence.setAccessible(true);
        List<AndroidEvent> actual = (List<AndroidEvent>) unfiredEventSequence.invoke(NFSIterator, firstState);
        assertTrue(actual.isEmpty());
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                this.deleteFilesInDirectory(file);
            file.delete();
        }
    }
}
