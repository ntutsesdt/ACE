package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DistanceEquivalentStrategyTest {
    private DistanceEquivalentStrategy handler;
    private String strCompare;

    @Before
    public void setUp() throws Exception {
        this.handler = new DistanceEquivalentStrategy();
        this.strCompare = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" class=\"android.widget.FrameLayout\" checkable=\"false\" checked=\"false\"" +
                " content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
    }

    @Ignore
    public void testIsEquivalent_True() throws DocumentException {
    }

    @Ignore
    public void testIsEquivalent_False() throws DocumentException {
    }

    @Test
    public void testGetEquivalentState() throws Exception {
        Document document = DocumentHelper.parseText(this.strCompare);
        GUIState state = new GUIState(document, new ArrayList<AndroidEvent>());
        Field field = EquivalentStateStrategy.class.getDeclaredField("eqGUIState");
        field.setAccessible(true);
        field.set(this.handler, state);
        assertEquals(state, this.handler.getEquivalentState());
    }

    @Ignore
    public void testCheckTwoStateIsSimilarState_True() throws DocumentException, NoSuchFieldException,
                                             IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = DistanceEquivalentStrategy.class.getDeclaredMethod("checkTwoStateIsSimilarState",
                Element.class, Element.class);
        method.setAccessible(true);

        Document documentCompare = DocumentHelper.parseText(this.strCompare);
        Element compareElement = (Element) documentCompare.getRootElement().elementIterator().next();
        System.out.println("[DBG] compareElement size : " + compareElement.attributes().size());
        boolean actual = (boolean) method.invoke(this.handler, compareElement, compareElement);
        assertTrue(actual);
    }

    @Ignore
    public void testCheckTwoStateIsSimilarState_False() throws DocumentException, NoSuchFieldException,
                                              IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String strStateA = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " clickable=\"false\" content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";

        String strStateB = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"true\" checked=\"false\"" +
                " clickable=\"false\" content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Method method = DistanceEquivalentStrategy.class.getDeclaredMethod("checkTwoStateIsSimilarState",
                Element.class, Element.class);
        method.setAccessible(true);

        Document documentCompare = DocumentHelper.parseText(strStateA);
        Element compareElement = (Element) documentCompare.getRootElement().elementIterator().next();

        Document documentNew = DocumentHelper.parseText(strStateB);
        Element newElement = (Element) documentNew.getRootElement().elementIterator().next();
        boolean actual = (boolean) method.invoke(this.handler, newElement, compareElement);
        assertFalse(actual);
    }

}