package tw.edu.ntut.sdtlab.crawler.ace.equivalent;


import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExactlyEquivalentStrategyTest {
    private ExactlyEquivalentStrategy exactlyEquivalentStrategy;
    private String strCompare;

    @Before
    public void setUp() throws Exception {
        this.exactlyEquivalentStrategy = new ExactlyEquivalentStrategy();
        this.strCompare = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" class=\"android.widget.FrameLayout\" checkable=\"false\" checked=\"false\"" +
                " content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
    }

    @Test
    public void testIsEquivalent_True() throws DocumentException {
        String strStateA = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" class=\"android.widget.FrameLayout\" checkable=\"false\" checked=\"false\"" +
                " content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";

        Document documentA = DocumentHelper.parseText(this.strCompare);
        GUIState stateA = new GUIState(documentA, new ArrayList<AndroidEvent>());
        Document documentB = DocumentHelper.parseText(strStateA);
        GUIState stateB = new GUIState(documentB, new ArrayList<AndroidEvent>());
        StateGraph testState = new StateGraph();
        testState.addState(stateB);
        assertTrue(this.exactlyEquivalentStrategy.isEquivalent(stateA, testState, stateA));
    }

    @Test
    public void testIsEquivalent_False() throws Exception {
        String strStateA = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " resource-id=\"\" scrollable=\"false\">\n" +
                "\t</node>\n" +
                "</hierarchy>";

        Document documentA = DocumentHelper.parseText(strStateA);
        GUIState stateA = new GUIState(documentA, new ArrayList<AndroidEvent>());
        Document documentB = DocumentHelper.parseText(this.strCompare);
        GUIState stateB = new GUIState(documentB, new ArrayList<AndroidEvent>());
        StateGraph testState = new StateGraph();
        testState.addState(stateB);
        assertFalse(this.exactlyEquivalentStrategy.isEquivalent(stateA, testState, stateA));
    }

    @Test
    public void testGetEquivalentState() throws Exception {
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
        Field field = ExactlyEquivalentStrategy.class.getDeclaredField("eqGUIState");
        field.setAccessible(true);
        field.set(this.exactlyEquivalentStrategy, state);
        assertEquals(state, this.exactlyEquivalentStrategy.getEquivalentState());
    }
}