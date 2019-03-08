package tw.edu.ntut.sdtlab.crawler.ace.state_graph;

import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.BackKeyEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.LeaveActivityLastEventOrder;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ClickTypeErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.NullPackageNameException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;
import tw.edu.ntut.sdtlab.crawler.ace.util.NodeAttribute;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GUIStateTest {
    private GUIState state;
    private ArrayList<AndroidEvent> events;
    final String TEST_DATA_FOLDER_PATH = "test/unit-test/tw/edu/ntut/sdtlab/crawler/ace/test-data";

    @Before
    public void setup() throws DocumentException {
        this.events = new ArrayList<>();
        SAXReader reader = new SAXReader();
        final String TEST_XML_PATH = TEST_DATA_FOLDER_PATH + "/layoutForGUIStateTest.xml";
        Document document = reader.read(TEST_XML_PATH);
        this.state = new GUIState(document, this.events);
    }

    @Test
    public void testGetPackageName() throws NoSuchFieldException, IllegalAccessException, NullPackageNameException {
        assertEquals("com.google.android.wearable.app", this.state.getPackageName());
    }

    @Test(expected = NullPackageNameException.class)
    public void testGetPackageNameNull() throws DocumentException, NullPackageNameException {
        String str = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " password=\"false\" resource-id=\"\" scrollable=\"false\"" +
                " selected=\"false\" text=\"很抱歉已停止\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document document = DocumentHelper.parseText(str);
        GUIState state = new GUIState(document, new ArrayList<AndroidEvent>());
        state.getPackageName();
    }

    @Test
    public void testGetActivityName() throws NoSuchFieldException, IllegalAccessException {
        String activityName = "activityName";
        Field field = GUIState.class.getDeclaredField("activityName");
        field.setAccessible(true);
        field.set(this.state, activityName);
        assertEquals(activityName, this.state.getActivityName());
    }

    @Test
    public void testSetActivityName() throws NoSuchFieldException, IllegalAccessException {
        String activityName = "activityName";
        this.state.setActivityName(activityName);
        Field field = GUIState.class.getDeclaredField("activityName");
        field.setAccessible(true);
        String actual = (String) field.get(this.state);
        assertEquals(activityName, actual);
    }

    @Test
    public void testIsNotCrashState() {
        boolean isCrashState = this.state.isCrashState();
        assertFalse(isCrashState);
    }

    @Test
    public void testIsCrashState() throws DocumentException {
        String text = Config.CRASH_MESSAGE.replace(" ", "");
        String str = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " package=\"com.google.android.wearable.app\" password=\"false\" resource-id=\"\" scrollable=\"false\"" +
                " selected=\"false\" text=\"" + text + "\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document document = DocumentHelper.parseText(str);
        GUIState state = new GUIState(document, new ArrayList<AndroidEvent>());
        assertTrue(state.isCrashState());
    }

    @Test
    public void testIsExactlyEquivalent() {
        GUIState targetState = new GUIState(this.state.contentClone(), new ArrayList<AndroidEvent>());
        assertTrue(this.state.isExactlyEquivalentTo(targetState));
    }

    @Test
    public void testIsOverCrossAppEventThreshold() throws NoSuchFieldException, IllegalAccessException {
        final int CONFIG_CROSS_APP_EVENT_THRESHOLD = Config.CROSS_APP_EVENT_THRESHHOLD;
        final int CROSS_APP_EVENT_THRESHOLD = CONFIG_CROSS_APP_EVENT_THRESHOLD + 1;
        Field field = GUIState.class.getDeclaredField("crossAppDepth");
        field.setAccessible(true);
        field.set(this.state, CROSS_APP_EVENT_THRESHOLD);
        assertTrue(this.state.isOverCrossAppEventThreshold());
    }

    @Test
    public void testIsOverCrossAppEventThresholdWhenEqualsToConfig() throws NoSuchFieldException, IllegalAccessException {
        final int CONFIG_CROSS_APP_EVENT_THRESHOLD = Config.CROSS_APP_EVENT_THRESHHOLD;
        Field field = GUIState.class.getDeclaredField("crossAppDepth");
        field.setAccessible(true);
        field.set(this.state, CONFIG_CROSS_APP_EVENT_THRESHOLD);
        assertTrue(this.state.isOverCrossAppEventThreshold());
    }

    @Test
    public void testNotOverCrossAppEventThreshold() throws NoSuchFieldException, IllegalAccessException {
        final int CONFIG_CROSS_APP_EVENT_THRESHOLD = Config.CROSS_APP_EVENT_THRESHHOLD;
        final int CROSS_APP_EVENT_THRESHOLD = CONFIG_CROSS_APP_EVENT_THRESHOLD - 1;
        Field field = GUIState.class.getDeclaredField("crossAppDepth");
        field.setAccessible(true);
        field.set(this.state, CROSS_APP_EVENT_THRESHOLD);
        assertFalse(this.state.isOverCrossAppEventThreshold());
    }

    @Test
    public void testSetEventListEmpty() throws NoSuchFieldException, IllegalAccessException {
        List<AndroidEvent> events = new ArrayList<>();
        events.add(new BackKeyEvent());
        Field field = GUIState.class.getDeclaredField("events");
        field.setAccessible(true);
        field.set(this.state, events);
        List<AndroidEvent> actual = (List<AndroidEvent>) field.get(this.state);
        assertTrue(actual.size() > 0);
        assertEquals(events.size(), actual.size());
        this.state.clearEvents();
        assertEquals(0, actual.size());
    }

    @Test
    public void testConvertDeviceXMLToGUIState() throws IOException, DocumentException, NoSuchFieldException, IllegalAccessException, ClickTypeErrorException, NullPackageNameException {
//        final String FILE_PATH = "test_gui_pages/test.xml";
        final String FILE_PATH = TEST_DATA_FOLDER_PATH + "/test.xml";

        // create test.xml file, which is used for convert
        final String XML_SOURCE = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><hierarchy rotation=\"0\">" +
                "<node index=\"0\" text=\"\" resource-id=\"\" class=\"android.widget.FrameLayout\"" +
                " package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\"" +
                " scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\"" +
                " bounds=\"[0,0][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.widget.LinearLayout\" package=\"com.example.oil.myapplication2\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\"" +
                " focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\"" +
                " selected=\"false\" bounds=\"[0,0][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.widget.FrameLayout\" package=\"com.example.oil.myapplication2\" content-desc=\"\"" +
                " checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\"" +
                " focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\"" +
                " bounds=\"[0,75][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"com.example.oil.myapplication2:id/decor_content_parent\"" +
                " class=\"android.view.View\" package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,75][1080,1776]\"><node index=\"0\"" +
                " text=\"\" resource-id=\"com.example.oil.myapplication2:id/action_bar_container\" class=\"android.widget.FrameLayout\"" +
                " package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\"" +
                " selected=\"false\" bounds=\"[0,75][1080,243]\"><node index=\"0\" text=\"\" resource-id=\"com.example.oil.myapplication2:id/action_bar\"" +
                " class=\"android.view.View\" package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\"" +
                " password=\"false\" selected=\"false\" bounds=\"[0,75][1080,243]\"><node index=\"0\" text=\"TestCrash\" resource-id=\"\"" +
                " class=\"android.widget.TextView\" package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[48,118][319,199]\" /></node></node>" +
                "<node index=\"1\" text=\"\" resource-id=\"android:id/content\" class=\"android.widget.FrameLayout\"" +
                " package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\"" +
                " password=\"false\" selected=\"false\" bounds=\"[0,243][1080,1776]\"><node index=\"0\" text=\"\" resource-id=\"\"" +
                " class=\"android.widget.RelativeLayout\" package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\"" +
                " checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,243][1080,1776]\"><node index=\"0\"" +
                " text=\"Hello World!\" resource-id=\"\" class=\"android.widget.TextView\" package=\"com.example.oil.myapplication2\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\"" +
                " focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[48,291][273,348]\" />" +
                "<node index=\"1\" text=\"crash\" resource-id=\"com.example.oil.myapplication2:id/crashButton\" class=\"android.widget.Button\"" +
                " package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\"" +
                " focusable=\"true\" focused=\"true\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[408,1005][672,1149]\" />" +
                "<node index=\"2\" text=\"PrevPage\" resource-id=\"com.example.oil.myapplication2:id/prevButton\" class=\"android.widget.Button\"" +
                " package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\"" +
                " focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\"" +
                " bounds=\"[128,1374][408,1518]\" /><node index=\"3\" text=\"nextPage\" resource-id=\"com.example.oil.myapplication2:id/nextButton\"" +
                " class=\"android.widget.Button\" package=\"com.example.oil.myapplication2\" content-desc=\"\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\"" +
                " selected=\"false\" bounds=\"[672,1374][954,1518]\" /></node></node></node></node></node><node index=\"1\" text=\"\"" +
                " resource-id=\"android:id/statusBarBackground\" class=\"android.view.View\" package=\"com.example.oil.myapplication2\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\"" +
                " focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][1080,75]\" /><node index=\"2\"" +
                " text=\"\" resource-id=\"android:id/navigationBarBackground\" class=\"android.view.View\" package=\"com.example.oil.myapplication2\"" +
                " content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\"" +
                " long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1776][1080,1920]\" /></node></hierarchy>";
        FileWriter fw = new FileWriter(FILE_PATH);
        fw.write(XML_SOURCE);
        fw.close();

        String expectActivityName = "activityName";
        GUIState state = GUIState.convertDeviceXMLToGUIState(FILE_PATH, new LeaveActivityLastEventOrder(), expectActivityName);

        // assert activity name
        Field activityNameField = GUIState.class.getDeclaredField("activityName");
        activityNameField.setAccessible(true);
        assertEquals(expectActivityName, activityNameField.get(state));

        Field field = GUIState.class.getDeclaredField("events");
        field.setAccessible(true);
        List<AndroidEvent> actualEvents = (List<AndroidEvent>) field.get(state);
        assertEquals(7, actualEvents.size());   // 7 is according to the run result before refactor

        // delete test.xml
        File xmlFile = new File(FILE_PATH);
        xmlFile.delete();
        assertFalse(xmlFile.exists());
    }

    @Test
    public void testCalculateElementDistance() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String documentStr = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " package=\"com.google.android.wearable.app\" password=\"false\" resource-id=\"\" scrollable=\"false\"" +
                " selected=\"false\" text=\"很抱歉已停止\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        final String newDocumentStr = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " package=\"com.google.android.wearable.app\" password=\"false\" resource-id=\"\" scrollable=\"false\"" +
                " selected=\"false\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document document = DocumentHelper.parseText(documentStr);
        Document newDocument = DocumentHelper.parseText(newDocumentStr);
        Method method = GUIState.class.getDeclaredMethod("calculateElementDistance", Element.class, Element.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.state, document.getRootElement(), newDocument.getRootElement());
        assertEquals(1, actual);
    }

    @Test
    public void testCalculateElementDistanceWhenTheSame() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String documentStr = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " package=\"com.google.android.wearable.app\" password=\"false\" resource-id=\"\" scrollable=\"false\"" +
                " selected=\"false\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document newDocument = DocumentHelper.parseText(documentStr);
        Method method = GUIState.class.getDeclaredMethod("calculateElementDistance", Element.class, Element.class);
        method.setAccessible(true);
        int actual = (int) method.invoke(this.state, newDocument.getRootElement(), newDocument.getRootElement());
        assertEquals(0, actual);
    }

    @Test
    public void testAreAttributeEqualsWhenIgnoreNAFWhenBothNotNAF() throws NoSuchMethodException, DocumentException, InvocationTargetException, IllegalAccessException {
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        String elementStr = "<node bounds=\"[0,75][1080,243]\" checkable=\"false\" checked=\"false\" class=\"android.widget.FrameLayout\"" +
                " clickable=\"false\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\"" +
                " long-clickable=\"false\" package=\"com.example.oil.loginapplication\" password=\"false\"" +
                " resource-id=\"com.example.oil.loginapplication:id/action_bar_container\" scrollable=\"false\" selected=\"false\"" +
                " text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        Element newElement = DocumentHelper.parseText(elementStr).getRootElement();
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertTrue(actual);
    }

    @Test
    public void testAreAttributeEqualsWhenIgnoreNAFWhenFirstIsNAF() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertTrue(actual);
    }

    @Test
    public void testAreAttributeEqualsWhenIgnoreNAFWhenSecondIsNAF() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertTrue(actual);
    }

    @Test
    public void testAreAttributeEqualsWhenIgnoreNAFWhenBothIsNAF() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String str = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(str).getRootElement();
        Element newElement = DocumentHelper.parseText(str).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertTrue(actual);
    }

    @Test
    public void testAreAttributeEqualsReturnFalseWhenFirstIsNAF() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node NAF=\"true\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"false\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertFalse(actual);
    }

    @Test
    public void testAreAttributeEqualsReturnFalseWhenFirstIsNAFAndSecondIsNotEqual() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"false\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertFalse(actual);
    }

    @Test
    public void testAreAttributeEqualsReturnFalseWhenSecondIsNAF() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"false\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertFalse(actual);
    }

    @Test
    public void testAreAttributeEqualsReturnFalseWhenSecondIsNAFAndSecondIsNotEqual() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"false\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node NAF=\"true\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertFalse(actual);
    }

    @Test
    public void testAreAttributeEqualsReturnFalseWhenBothIsNAF() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node NAF=\"true\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertFalse(actual);
    }

    @Test
    public void testAreAttributeEqualsReturnFalseWhenBothIsNAFAndSecondIsNotEqual() throws DocumentException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String elementStr = "<node NAF=\"true\" bounds=\"[0,150][1080,342]\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element element = DocumentHelper.parseText(elementStr).getRootElement();
        String newElementStr = "<node NAF=\"true\" checkable=\"false\" checked=\"false\" class=\"android.widget.TextView\" clickable=\"true\" content-desc=\"\" enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\" package=\"com.example.anycut\" password=\"false\" resource-id=\"android:id/text1\" scrollable=\"false\" selected=\"false\" text=\"\"></node>";
        Element newElement = DocumentHelper.parseText(newElementStr).getRootElement();
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE)
            excludeAttribute.add(NodeAttribute.Bounds);
        Method method = GUIState.class.getDeclaredMethod("areAttributeEqualsWhenIgnoreNAF", Element.class, Element.class, List.class);
        method.setAccessible(true);
        boolean actual = (boolean) method.invoke(this.state, element, newElement, excludeAttribute);
        assertFalse(actual);
    }
}
