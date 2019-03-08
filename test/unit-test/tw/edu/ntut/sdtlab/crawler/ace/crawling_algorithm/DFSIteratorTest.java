package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.equivalent.EquivalentStateStrategy;
import tw.edu.ntut.sdtlab.crawler.ace.equivalent.EquivalentStateStrategyFactory;
import tw.edu.ntut.sdtlab.crawler.ace.event.*;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ClickTypeErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.NullPackageNameException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import org.dom4j.DocumentHelper;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.Point;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DFSIteratorTest {
    private StateGraph stateGraph;
    private Device device;
    private AndroidCrawler androidCrawler;
    private String deviceSerialNum;
    private AndroidEvent checkEvent, scrollEvent, swipeEvent, editTextEvent, clickEvent, longClickEvent, backKeyEvent, menuKeyEvent;
    private Point point;
    private String testStr, twoXMLSource, threeXMLSource, fourXMLSource;
    private Document document;
    private GUIState firstState, secondState, thirdState;
    private final String REPORT_PATH = Utility.getReportPath();

    @Before
    public void setup() throws DocumentException, InterruptedException, ExecuteCommandErrorException, ClickTypeErrorException, IOException, NullPackageNameException, MultipleListOrGridException {
        Config config = new Config();
        this.stateGraph = new StateGraph();
        this.deviceSerialNum = config.getDeviceSerialNum();
        this.device = new Device(this.deviceSerialNum);
        this.androidCrawler = new AndroidCrawler(device, stateGraph);
        twoXMLSource = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +
                "<hierarchy rotation=\"0\"><node index=\"0\" text=\"\" resource-id=\"\" " +
                "class=\"android.widget.FrameLayout\" package=\"com.example.oil.myapplication2\" " +
                "content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" " +
                "focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" " +
                "selected=\"false\" bounds=\"[0,0][1080,1920]\" /></hierarchy>";
        threeXMLSource = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><hierarchy rotation=\"0\"><node index=\"0\" " +
                "text=\"\" resource-id=\"\" class=\"android.widget.FrameLayout\" package=\"com.example.oil.myapplication4\" " +
                "content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" " +
                "focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" " +
                "bounds=\"[0,0][1080,1920]\" /></hierarchy>";
        fourXMLSource = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +
                "<hierarchy rotation=\"0\"><node index=\"0\" text=\"\" resource-id=\"\" " +
                "class=\"android.widget.FrameLayout\" package=\"com.google.android.wearable.app\" " +
                "content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" " +
                "enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" " +
                "long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][320,320]\" /></hierarchy>";
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
        firstState.setActivityName("firstState");
        document = DocumentHelper.parseText(threeXMLSource);
        secondState = new GUIState(document, new ArrayList<AndroidEvent>());
        document = DocumentHelper.parseText(fourXMLSource);
        thirdState = new GUIState(document, new ArrayList<AndroidEvent>());
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
    public void testGetEventSequence() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);
        eventSequence.add(clickEvent);
        eventSequence.add(longClickEvent);
        eventSequence.add(scrollEvent);
        eventSequence.add(swipeEvent);
        eventSequence.add(backKeyEvent);
        eventSequence.add(checkEvent);
        eventSequence.add(editTextEvent);

        DFSIterator iterator = new DFSIterator(stateGraph, androidCrawler);
        Field eventSequenceField = DFSIterator.class.getDeclaredField("eventSequence");
        eventSequenceField.setAccessible(true);
        eventSequenceField.set(iterator, eventSequence);
        assertEquals(eventSequence, iterator.getEventSequence());
    }

    @Test
    public void testGetUnfiredEvent() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method unfiredEvent = DFSIterator.class.getDeclaredMethod("getUnfiredEvent", GUIState.class);
        unfiredEvent.setAccessible(true);

        Field firstStateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        firstStateCrossApp.setAccessible(true);
        firstStateCrossApp.set(firstState, 0);

        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);
        eventSequence.add(clickEvent);
        eventSequence.get(0).setVisited(true);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, eventSequence);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field field = StateGraph.class.getDeclaredField("states");
        field.setAccessible(true);
        field.set(stateGraph, states);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        AndroidEvent actual = (AndroidEvent) unfiredEvent.invoke(DFSiterator, firstState);
        assertEquals(clickEvent, actual);
    }

    @Test
    public void testGetUnfiredEventWhenAllEventFired() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method unfiredEvent = DFSIterator.class.getDeclaredMethod("getUnfiredEvent", GUIState.class);
        unfiredEvent.setAccessible(true);

        Field firstStateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        firstStateCrossApp.setAccessible(true);
        firstStateCrossApp.set(firstState, 0);

        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);
        eventSequence.get(0).setVisited(true);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, eventSequence);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field field = StateGraph.class.getDeclaredField("states");
        field.setAccessible(true);
        field.set(stateGraph, states);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        AndroidEvent actual = (AndroidEvent) unfiredEvent.invoke(DFSiterator, firstState);
        assertNull(actual);
    }

    @Test
    public void testGetUnfiredEventWhenCrossApp() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        new File(REPORT_PATH + "/States").mkdirs();
        EquivalentStateStrategy equivalentStateStrategy = new EquivalentStateStrategyFactory().createStrategy();
        this.stateGraph.setEquivalentStragegy(equivalentStateStrategy);

        Method unfiredEvent = DFSIterator.class.getDeclaredMethod("getUnfiredEvent", GUIState.class);
        unfiredEvent.setAccessible(true);

        Field firstStateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        firstStateCrossApp.setAccessible(true);
        firstStateCrossApp.set(firstState, Config.CROSS_APP_EVENT_THRESHHOLD);

        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, eventSequence);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field field = StateGraph.class.getDeclaredField("states");
        field.setAccessible(true);
        field.set(stateGraph, states);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        AndroidEvent actual = (AndroidEvent) unfiredEvent.invoke(DFSiterator, firstState);
        assertNull(actual);
    }

    @Test
    public void testAllEventFiredWhenAllEventVisited() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method firedEvent = DFSIterator.class.getDeclaredMethod("allEventFired", GUIState.class);
        firedEvent.setAccessible(true);

        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);
        eventSequence.get(0).setVisited(true);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, eventSequence);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field field = StateGraph.class.getDeclaredField("states");
        field.setAccessible(true);
        field.set(stateGraph, states);
        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        assertTrue((boolean) firedEvent.invoke(DFSiterator, firstState));
    }

    @Test
    public void testAllEventFiredWhenEventUnvisited() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method firedEvent = DFSIterator.class.getDeclaredMethod("allEventFired", GUIState.class);
        firedEvent.setAccessible(true);

        List<AndroidEvent> eventSequence = new ArrayList<>();
        eventSequence.add(menuKeyEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, eventSequence);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field field = StateGraph.class.getDeclaredField("states");
        field.setAccessible(true);
        field.set(stateGraph, states);
        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        assertFalse((boolean) firedEvent.invoke(DFSiterator, firstState));
    }

    @Test
    public void testIsToStateExistInPreviousEvent() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method previousEvent = DFSIterator.class.getDeclaredMethod("isToStateExistInPreviousEvent", AndroidEvent.class, GUIState.class);
        previousEvent.setAccessible(true);

        menuKeyEvent.setFromState(firstState);
        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        assertTrue((boolean) previousEvent.invoke(DFSiterator, menuKeyEvent, firstState));
    }

    @Test
    public void testIsToStateExistInPreviousEventWhenPreviousEventNotToStateExist() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method previousEvent = DFSIterator.class.getDeclaredMethod("isToStateExistInPreviousEvent", AndroidEvent.class, GUIState.class);
        previousEvent.setAccessible(true);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        assertFalse((boolean) previousEvent.invoke(DFSiterator, menuKeyEvent, firstState));
    }

    @Test
    public void testPopVisitedStateUntilLastUnvisitedEventExist() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method popVisitedState = DFSIterator.class.getDeclaredMethod("popVisitedStateUntilLastUnvisitedEventExist");
        popVisitedState.setAccessible(true);

        Field stateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        stateCrossApp.setAccessible(true);
        stateCrossApp.set(firstState, 5);
        stateCrossApp.set(secondState, 0);

        List<AndroidEvent> firedEventSequence = new ArrayList<>();
        firedEventSequence.add(menuKeyEvent);
        firedEventSequence.get(0).setVisited(true);

        List<AndroidEvent> unfiredEventSequence = new ArrayList<>();
        unfiredEventSequence.add(clickEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, firedEventSequence);
        firstStateEvents.set(secondState, unfiredEventSequence);

        Stack<GUIState> stacks = new Stack<>();
        stacks.push(firstState);
        stacks.push(secondState);
        Map<GUIState, Stack<GUIState>> visitedStacks = new HashMap<>();
        visitedStacks.put(firstState, stacks);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        states.add(secondState);
        Field stateField = StateGraph.class.getDeclaredField("states");
        stateField.setAccessible(true);
        stateField.set(stateGraph, states);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        Field tmpRootStateField = DFSIterator.class.getDeclaredField("tmpRootState");
        tmpRootStateField.setAccessible(true);
        tmpRootStateField.set(DFSiterator, firstState);
        Field field = DFSIterator.class.getDeclaredField("visitedStacks");
        field.setAccessible(true);
        field.set(DFSiterator, visitedStacks);
        popVisitedState.invoke(DFSiterator);
        Map<GUIState, Stack<GUIState>> actualVisitedStacks = (Map<GUIState, Stack<GUIState>>) field.get(DFSiterator);
        Stack<GUIState> actualStacks = actualVisitedStacks.get(firstState);
        assertEquals(secondState, actualStacks.peek());
    }

    @Test
    public void testPopVisitedStateUntilLastUnvisitedEventExistWhenCrossApp() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method popVisitedState = DFSIterator.class.getDeclaredMethod("popVisitedStateUntilLastUnvisitedEventExist");
        popVisitedState.setAccessible(true);

        Field stateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        stateCrossApp.setAccessible(true);
        stateCrossApp.set(firstState, Config.CROSS_APP_EVENT_THRESHHOLD);

        List<AndroidEvent> firedEventSequence = new ArrayList<>();
        firedEventSequence.add(menuKeyEvent);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, firedEventSequence);

        Stack<GUIState> stacks = new Stack<>();
        stacks.push(firstState);
        Map<GUIState, Stack<GUIState>> visitedStacks = new HashMap<>();
        visitedStacks.put(firstState, stacks);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field stateField = StateGraph.class.getDeclaredField("states");
        stateField.setAccessible(true);
        stateField.set(stateGraph, states);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        Field tmpRootStateField = DFSIterator.class.getDeclaredField("tmpRootState");
        tmpRootStateField.setAccessible(true);
        tmpRootStateField.set(DFSiterator, firstState);
        Field field = DFSIterator.class.getDeclaredField("visitedStacks");
        field.setAccessible(true);
        field.set(DFSiterator, visitedStacks);
        popVisitedState.invoke(DFSiterator);
        Map<GUIState, Stack<GUIState>> actualVisitedStacks = (Map<GUIState, Stack<GUIState>>) field.get(DFSiterator);
        Stack<GUIState> actualStacks = actualVisitedStacks.get(firstState);
        assertTrue(actualStacks.empty());
    }

    @Test
    public void testPopVisitedStateUntilLastUnvisitedEventExistWhenEventFired() throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Method popVisitedState = DFSIterator.class.getDeclaredMethod("popVisitedStateUntilLastUnvisitedEventExist");
        popVisitedState.setAccessible(true);

        Field stateCrossApp = GUIState.class.getDeclaredField("crossAppDepth");
        stateCrossApp.setAccessible(true);
        stateCrossApp.set(firstState, 0);

        List<AndroidEvent> firedEventSequence = new ArrayList<>();
        firedEventSequence.add(menuKeyEvent);
        firedEventSequence.get(0).setVisited(true);

        Field firstStateEvents = GUIState.class.getDeclaredField("events");
        firstStateEvents.setAccessible(true);
        firstStateEvents.set(firstState, firedEventSequence);

        Stack<GUIState> stacks = new Stack<>();
        stacks.push(firstState);
        Map<GUIState, Stack<GUIState>> visitedStacks = new HashMap<>();
        visitedStacks.put(firstState, stacks);

        List<GUIState> states = new ArrayList<>();
        states.add(firstState);
        Field stateField = StateGraph.class.getDeclaredField("states");
        stateField.setAccessible(true);
        stateField.set(stateGraph, states);

        DFSIterator DFSiterator = new DFSIterator(stateGraph, androidCrawler);
        Field tmpRootStateField = DFSIterator.class.getDeclaredField("tmpRootState");
        tmpRootStateField.setAccessible(true);
        tmpRootStateField.set(DFSiterator, firstState);
        Field field = DFSIterator.class.getDeclaredField("visitedStacks");
        field.setAccessible(true);
        field.set(DFSiterator, visitedStacks);
        popVisitedState.invoke(DFSiterator);
        Map<GUIState, Stack<GUIState>> actualVisitedStacks = (Map<GUIState, Stack<GUIState>>) field.get(DFSiterator);
        Stack<GUIState> actualStacks = actualVisitedStacks.get(firstState);
        assertTrue(actualStacks.empty());
    }

    @After
    public void teardown() {
        File directory = new File(Utility.getReportPath());
        if (directory.exists()) {
            deleteFilesInDirectory(directory);
            directory.delete();
        }
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                this.deleteFilesInDirectory(file);
            file.delete();
        }
    }
}
