package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.util.NodeAttribute;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import org.dom4j.Document;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


import static org.junit.Assert.*;

public class EquivalentStateStrategyTest {

     public class MockEQStateStrategy extends EquivalentStateStrategy {
         private GUIState eqGUIState;

         public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState){
             return true;
         }

         @Override
         public GUIState getEquivalentState(){
                 return this.eqGUIState;
             }

         @Override
         public boolean getIsMarkEquivalent() {
             return isMarkEquivalent;
         }

         @Override
         public void setIsMarkEquivalent(boolean isMarkEquivalent) {
             this.isMarkEquivalent = isMarkEquivalent;
         }
     }
     private MockEQStateStrategy handler;
     private String strCompare;

    @Before
    public void setUp() throws Exception {
        this.strCompare = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" class=\"android.widget.FrameLayout\" checkable=\"false\" checked=\"false\"" +
                " content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
          this.handler = new MockEQStateStrategy();
    }

    @Test
    public void testAreAttributesEqual_True() throws Exception {
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

        Element compareStateElement = stateA.contentClone().getRootElement();
        Element newStateElement = stateB.contentClone().getRootElement();

        Method method = EquivalentStateStrategy.class.getDeclaredMethod("areAttributesEqual", Element.class, Element.class);
        method.setAccessible(true);
        Boolean actual = (Boolean) method.invoke(this.handler, compareStateElement, newStateElement);
        assertTrue(actual);
    }

    @Test
    public void testAreAttributesEqualFalse() throws Exception {
        String strStateA = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";
        String strStateB = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " >\n" +
                "\t</node>\n" +
                "</hierarchy>";
        Document documentA = DocumentHelper.parseText(strStateA);
        Document documentB = DocumentHelper.parseText(strStateB);

        Method method = EquivalentStateStrategy.class.getDeclaredMethod("areAttributesEqual", Element.class, Element.class);
        method.setAccessible(true);
        Boolean actual = (Boolean) method.invoke(this.handler, documentA.getRootElement(), documentB.getRootElement());
        assertTrue(actual);
    }

    @Test
    public void testAreAttributesEqualWhenElementHasElements() throws Exception {
        String strStateA = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" class=\"android.widget.FrameLayout\" checkable=\"false\"" +
                " content-desc=\"\">\n" +
                "\t</node>\n" +
                "</hierarchy>";

        Document documentA = DocumentHelper.parseText(this.strCompare);
        GUIState stateA = new GUIState(documentA, new ArrayList<AndroidEvent>());
        Document documentB = DocumentHelper.parseText(strStateA);
        GUIState stateB = new GUIState(documentB, new ArrayList<AndroidEvent>());

        Element compareStateElement = stateA.contentClone().getRootElement();
        Element newStateElement = stateB.contentClone().getRootElement();

        Method method = EquivalentStateStrategy.class.getDeclaredMethod("areAttributesEqual", Element.class, Element.class);
        method.setAccessible(true);
        Boolean actual = (Boolean) method.invoke(this.handler, compareStateElement, newStateElement);
        assertTrue(actual);
    }

    @Test
    public void testAreEquivalent_True() throws Exception {
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

        Element compareStateElement = stateA.contentClone().getRootElement();
        Element newStateElement = stateB.contentClone().getRootElement();
        HashMap<String,Integer> testLengthMap = new HashMap<>();

        Method method = EquivalentStateStrategy.class.getDeclaredMethod("areEquivalent",
                Element.class, Element.class, HashMap.class, GUIState.class, GUIState.class,
                String.class );
        method.setAccessible(true);
        Boolean actual = (Boolean) method.invoke(this.handler, compareStateElement, newStateElement,
                testLengthMap, stateA, stateB, NodeAttribute.ListView);
        assertTrue(actual);
    }
}