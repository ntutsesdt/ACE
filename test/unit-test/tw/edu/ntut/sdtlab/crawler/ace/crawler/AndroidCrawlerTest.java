package tw.edu.ntut.sdtlab.crawler.ace.crawler;

import tw.edu.ntut.sdtlab.crawler.ace.util.CommandHelper;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.equivalent.CComponentEquivalentStrategy;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.BackKeyEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.ClickEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.EventData;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AndroidCrawlerTest {
    private AndroidCrawler androidCrawler;
    private Device device;
    private StateGraph stateGraph;
    private String deviceSerialNum;

    @Before
    public void setup() throws InterruptedException, ExecuteCommandErrorException, ClickTypeErrorException, DocumentException, IOException, NullPackageNameException, MultipleListOrGridException {
        Config config = new Config();
        this.deviceSerialNum = config.getDeviceSerialNum();
        this.device = new Device(this.deviceSerialNum);
        this.stateGraph = new StateGraph();
        this.stateGraph.setEquivalentStragegy(new CComponentEquivalentStrategy());
        this.androidCrawler = new AndroidCrawler(this.device, this.stateGraph);
        new File(System.getProperty("user.dir") + "/" + Utility.getReportPath() + "/States").mkdirs();
    }

    @Test
    public void testFirstTimeRestartApp() throws InterruptedException, ExecuteCommandErrorException, IOException, NoSuchFieldException, IllegalAccessException, ClickTypeErrorException, DocumentException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        Field restartCountField = AndroidCrawler.class.getDeclaredField("restartCount");
        restartCountField.setAccessible(true);
        int restartCount = 0;
        restartCountField.set(this.androidCrawler, restartCount);
        this.androidCrawler.restartApp();

        // check package name correct after restart
        String[] getPackageNameCommand = {Config.ADB_PATH, "-s", this.deviceSerialNum, "shell", "dumpsys", "window", "windows", "|", "grep", "-E", "\'mFocusedApp\'"};
        List<String> packageNameResult = CommandHelper.executeCmd(getPackageNameCommand);
        assertTrue(packageNameResult.get(0).contains(Config.PACKAGE_NAME));

        // check activity name correct after restart when non instrumentation situation
        if (!Config.APP_INSTRUMENTED) {
            String[] getActivityNameCommand = {Config.ADB_PATH, "-s", this.deviceSerialNum, "shell", "dumpsys", "window", "windows", "|", "grep", "-E", "\'mCurrentFocus\'"};
            List<String> activityNameResult = CommandHelper.executeCmd(getActivityNameCommand);
            assertTrue(activityNameResult.get(0).contains(Config.LAUNCHER_ACTIVITY));
        }

        // check not increase restart count
        int expectRestartCount = restartCount;
        int actualRestartCount = (int) restartCountField.get(this.androidCrawler);
        assertEquals(expectRestartCount, actualRestartCount);
    }

    @Test
    public void testNotFirstTimeRestartApp() throws InterruptedException, ExecuteCommandErrorException, IOException, NoSuchFieldException, IllegalAccessException, ClickTypeErrorException, DocumentException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        Field restartCountField = AndroidCrawler.class.getDeclaredField("restartCount");
        restartCountField.setAccessible(true);
        int restartCount = 0;
        restartCountField.set(this.androidCrawler, restartCount);
        this.androidCrawler.restartApp();
        // check not increase restart count
        int expectRestartCount = restartCount;
        int actualRestartCount = (int) restartCountField.get(this.androidCrawler);
        assertEquals(expectRestartCount, actualRestartCount);
        this.androidCrawler.restartApp();
        // check increase restart count
        actualRestartCount = (int) restartCountField.get(this.androidCrawler);
        assertEquals(1, actualRestartCount);
    }

    @Test
    public void testExecuteEvent() throws NoSuchFieldException, IllegalAccessException, ExecuteCommandErrorException, EventFromStateErrorException, InterruptedException, IOException {
        int executeEventCount = 0;
        Field totalExecutedEventCountField = StateGraph.class.getDeclaredField("totalEventCount");
        totalExecutedEventCountField.setAccessible(true);
        totalExecutedEventCountField.set(this.stateGraph, executeEventCount);
        this.androidCrawler.executeEvent(new BackKeyEvent());

        // check increase total executed event count
        int expectExecuteEventCount = executeEventCount + 1;
        int actualExecuteEventCount = (int) totalExecutedEventCountField.get(this.stateGraph);
        assertEquals(expectExecuteEventCount, actualExecuteEventCount);
    }

    @Test
    public void testExecuteEvents() throws NoSuchFieldException, IllegalAccessException, EventFromStateErrorException, InterruptedException, IOException, CannotReachTargetStateException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        int executeEventCount = 0;
        Field totalExecutedEventCountField = StateGraph.class.getDeclaredField("totalEventCount");
        totalExecutedEventCountField.setAccessible(true);
        totalExecutedEventCountField.set(this.stateGraph, executeEventCount);
        List<AndroidEvent> events = new ArrayList<>();
        events.add(new BackKeyEvent());
        this.androidCrawler.executeEvents(events);

        // check increase total executed event count
        int expectExecuteEventCount = executeEventCount + 1;
        int actualExecuteEventCount = (int) totalExecutedEventCountField.get(this.stateGraph);
        assertEquals(expectExecuteEventCount, actualExecuteEventCount);
    }

    @Test
    public void testExecuteEventsWhenEventsIsEmpty() throws NoSuchFieldException, IllegalAccessException, EventFromStateErrorException, InterruptedException, IOException, CannotReachTargetStateException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        int executeEventCount = 0;
        Field totalExecutedEventCountField = StateGraph.class.getDeclaredField("totalEventCount");
        totalExecutedEventCountField.setAccessible(true);
        totalExecutedEventCountField.set(this.stateGraph, executeEventCount);
        List<AndroidEvent> events = new ArrayList<>();
        this.androidCrawler.executeEvents(events);

        // check increase total executed event count
        int expectExecuteEventCount = executeEventCount;
        int actualExecuteEventCount = (int) totalExecutedEventCountField.get(this.stateGraph);
        assertEquals(expectExecuteEventCount, actualExecuteEventCount);
    }

    @Test(expected = EventFromStateErrorException.class)
    public void testExecuteEventWhenFromStateError() throws NoSuchFieldException, DocumentException, IllegalAccessException, ExecuteCommandErrorException, EventFromStateErrorException, InterruptedException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " resource-id=\"\" scrollable=\"false\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        AndroidEvent event = new BackKeyEvent();
        Field field = AndroidEvent.class.getDeclaredField("fromState");
        field.setAccessible(true);
        Document document = DocumentHelper.parseText(xml);
        field.set(event, new GUIState(document, new ArrayList<AndroidEvent>()));
        this.androidCrawler.executeEvent(event);
    }

    @Test
    public void testGetCurrentState() throws DocumentException, NoSuchFieldException, IllegalAccessException {
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " resource-id=\"\" scrollable=\"false\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document document = DocumentHelper.parseText(xml);
        GUIState state = new GUIState(document, new ArrayList<AndroidEvent>());
        Field field = AndroidCrawler.class.getDeclaredField("currentState");
        field.setAccessible(true);
        field.set(this.androidCrawler, state);
        assertEquals(state, this.androidCrawler.getCurrentState());
    }

    @Test
    public void testGetStateGraph() throws NoSuchFieldException, IllegalAccessException {
        StateGraph stateGraph = new StateGraph();
        Field field = AndroidCrawler.class.getDeclaredField("stateGraph");
        field.setAccessible(true);
        field.set(this.androidCrawler, stateGraph);
        assertEquals(stateGraph, this.androidCrawler.getStateGraph());
    }

    @Test
    public void testIsDesktopState() throws DocumentException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " resource-id=\"\" scrollable=\"false\" package=\"testPackageName\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document document = DocumentHelper.parseText(xml);
        GUIState desktopState = new GUIState(document, new ArrayList<AndroidEvent>());
        final String ACTIVITY_NAME = "testActivityName";
        desktopState.setActivityName(ACTIVITY_NAME);
        Field field = AndroidCrawler.class.getDeclaredField("desktopState");
        field.setAccessible(true);
        field.set(this.androidCrawler, desktopState);
        GUIState testState = new GUIState(document, new ArrayList<AndroidEvent>());
        testState.setActivityName(ACTIVITY_NAME);
        Method method = AndroidCrawler.class.getDeclaredMethod("isDesktopState", GUIState.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.androidCrawler, testState);
        assertTrue(actual);
    }

    @Test
    public void testIncreaseGUIIndex() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = AndroidCrawler.class.getDeclaredField("guiIndex");
        field.setAccessible(true);
        int actual = (int) field.get(this.androidCrawler);
        assertEquals(0, actual);
        Method method = AndroidCrawler.class.getDeclaredMethod("increaseGUIIndex");
        method.setAccessible(true);
        method.invoke(this.androidCrawler);
        actual = (int) field.get(this.androidCrawler);
        assertEquals(1, actual);
    }

    @Test
    public void testUpdateStateGraphWhenDesktopState() throws NoSuchFieldException, IllegalAccessException, InterruptedException, DocumentException, ClickTypeErrorException, ExecuteCommandErrorException, IOException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException, NoSuchMethodException, InvocationTargetException {
        String xmlString = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" +
                "<hierarchy rotation=\"0\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.widget.FrameLayout\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\"" +
                " bounds=\"[0,0][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.widget.LinearLayout\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,1776]\"><node" +
                " index=\"0\" text=\"\" resource-id=\"android:id/content\" class=\"android.widget.FrameLayout\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,1776]\"><node" +
                " index=\"0\" text=\"\" resource-id=\"\" class=\"android.widget.FrameLayout\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"Xperia™主畫面\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\"" +
                " focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\"" +
                " selected=\"false\" bounds=\"[0,0][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.view.View\" package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\"" +
                " scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\"" +
                " bounds=\"[0,0][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"\" class=\"android.view.View\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\"" +
                " password=\"false\" selected=\"false\" bounds=\"[0,0][1080,1776]\" /><node index=\"1\" text=\"\" resource-id=\"\"" +
                " class=\"android.view.View\" package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1068,600]\"><node index=\"0\" text=\"\"" +
                " resource-id=\"\" class=\"android.appwidget.AppWidgetHostView\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"What's New\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\"" +
                " focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\"" +
                " selected=\"false\" bounds=\"[0,0][1068,600]\"><node index=\"0\" text=\"\"" +
                " resource-id=\"com.sonymobile.entrance:id/widget_root\" class=\"android.widget.RelativeLayout\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[6,6][1062,594]\"><node index=\"0\"" +
                " text=\"\" resource-id=\"com.sonymobile.entrance:id/out_of_box\" class=\"android.widget.RelativeLayout\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\"" +
                " password=\"false\" selected=\"false\" bounds=\"[6,6][1062,594]\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.widget.ImageView\" package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[6,6][1062,327]\" /><node index=\"1\" text=\"\"" +
                " resource-id=\"com.sonymobile.entrance:id/back_plate\" class=\"android.widget.LinearLayout\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\"" +
                " scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[6,327][1062,594]\"><node index=\"0\"" +
                " text=\"探索您最愛的新遊戲、應用程式與媒體內容。\" resource-id=\"com.sonymobile.entrance:id/welcome_text\" class=\"android.widget.TextView\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\"" +
                " focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[54,375][1014,432]\" /><node index=\"1\"" +
                " text=\"顯示\" resource-id=\"com.sonymobile.entrance:id/widget_show_me_button\" class=\"android.widget.Button\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[726,432][1026,570]\" /></node></node><node index=\"1\" text=\"\"" +
                " resource-id=\"com.sonymobile.entrance:id/empty_view\" class=\"android.widget.ViewFlipper\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\"" +
                " scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[6,6][1062,594]\"><node index=\"0\" text=\"\"" +
                " resource-id=\"\" class=\"android.widget.FrameLayout\" package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\"" +
                " bounds=\"[6,6][1062,594]\" /></node><node index=\"2\" text=\"\" resource-id=\"com.sonymobile.entrance:id/header\" class=\"android.widget.RelativeLayout\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\"" +
                " focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[6,6][1062,126]\"><node index=\"0\"" +
                " text=\"\" resource-id=\"com.sonymobile.entrance:id/header_clickable_area\" class=\"android.widget.LinearLayout\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[6,6][392,126]\"><node index=\"0\" text=\"\"" +
                " resource-id=\"\" class=\"android.widget.ImageView\" package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\"" +
                " password=\"false\" selected=\"false\" bounds=\"[42,42][126,126]\" /><node index=\"1\" text=\"What's New\" resource-id=\"\" class=\"android.widget.TextView\"" +
                " package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\"" +
                " focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[138,55][356,112]\"" +
                " /></node></node></node></node></node><node index=\"3\" text=\"\" resource-id=\"\" class=\"android.view.View\" package=\"com.sonyericsson.home\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][288,65]\" /></node><node NAF=\"true\" index=\"1\" text=\"\" resource-id=\"\"" +
                " class=\"android.view.View\" package=\"com.sonyericsson.home\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\"" +
                " focusable=\"false\" focused=\"true\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,1776]\"" +
                " /></node></node></node></node></hierarchy>";
        Document document = DocumentHelper.parseText(xmlString);
        GUIState desktopState = new GUIState(document, new ArrayList<AndroidEvent>());
        Field desktopStateField = AndroidCrawler.class.getDeclaredField("desktopState");
        desktopStateField.setAccessible(true);
        desktopStateField.set(this.androidCrawler, desktopState);
        assertNotNull(desktopState);
        Method method = AndroidCrawler.class.getDeclaredMethod("dumpCurrentState");
        method.setAccessible(true);
        GUIState newState = (GUIState) method.invoke(this.androidCrawler);
        this.androidCrawler.updateStateGraph(newState);
        Field currentStateField = AndroidCrawler.class.getDeclaredField("desktopState");
        currentStateField.setAccessible(true);
        GUIState currentState = (GUIState) currentStateField.get(this.androidCrawler);
        assertNotNull(currentState);
        assertEquals(desktopState, currentState);
    }

    @Test
    public void testStartExploreNotIncreaseRestartCount() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method method = AndroidCrawler.class.getDeclaredMethod("startExplore");
        method.setAccessible(true);
        method.invoke(this.androidCrawler);
        Field field = AndroidCrawler.class.getDeclaredField("restartCount");
        field.setAccessible(true);
        int actualRestartCount = (int) field.get(this.androidCrawler);
        assertEquals(0, actualRestartCount);
    }

    @Test
    public void testStartExploreUpdateDesktopState() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = AndroidCrawler.class.getDeclaredField("desktopState");
        field.setAccessible(true);
        GUIState desktopState = (GUIState) field.get(this.androidCrawler);
        assertNull(desktopState);
        Method method = AndroidCrawler.class.getDeclaredMethod("startExplore");
        method.setAccessible(true);
        method.invoke(this.androidCrawler);
        desktopState = (GUIState) field.get(this.androidCrawler);
        assertNotNull(desktopState);
        Field eventsField = GUIState.class.getDeclaredField("events");
        eventsField.setAccessible(true);
        List<AndroidEvent> actual = (List<AndroidEvent>) eventsField.get(desktopState);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testDumpCurrentState() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = AndroidCrawler.class.getDeclaredMethod("dumpCurrentState");
        method.setAccessible(true);
        method.invoke(this.androidCrawler);
    }

    @Test
    public void testGetNondeterministicEventIfExist() throws DocumentException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<AndroidEvent> fromStateEvents = new ArrayList<>();
        AndroidEvent clickEvent = new ClickEvent(new EventData("[480,230][600,310]", "temp"), ClickEvent.Type.Click);
        AndroidEvent backKeyEvent = new BackKeyEvent();
        backKeyEvent.setNondeterministic(true);
        fromStateEvents.add(clickEvent);
        fromStateEvents.add(backKeyEvent);
        GUIState fromState = new GUIState(DocumentHelper.parseText("<text></text>"), fromStateEvents);
        clickEvent.setFromState(fromState);
        backKeyEvent.setToState(fromState);
        Field currentStateField = AndroidCrawler.class.getDeclaredField("currentState");
        currentStateField.setAccessible(true);
        currentStateField.set(this.androidCrawler, fromState);
        Method method = AndroidCrawler.class.getDeclaredMethod("getNondeterministicEventIfExist", AndroidEvent.class);
        method.setAccessible(true);
        AndroidEvent nonDeterministicEvent = (AndroidEvent) method.invoke(this.androidCrawler, clickEvent);
        assertNotNull(nonDeterministicEvent);
    }

    @After
    public void teardown() throws NoSuchFieldException, IllegalAccessException {
        File directory = new File(Utility.getReportPath());
        deleteFilesInDirectory(directory);
        directory.delete();
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                this.deleteFilesInDirectory(file);
            file.delete();
        }
    }
}
