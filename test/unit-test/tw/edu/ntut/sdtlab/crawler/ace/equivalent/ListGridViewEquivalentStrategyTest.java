package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListGridViewEquivalentStrategyTest {
    private ListGridViewEquivalentStrategy listGridViewEquivalentStrategy;

    @Before
    public void setup() {
        this.listGridViewEquivalentStrategy = new ListGridViewEquivalentStrategy();
    }

    @Test
    public void testMarkEquivalentState() throws NoSuchMethodException, NoSuchFieldException, DocumentException, InvocationTargetException, IllegalAccessException {
        String str = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n" +
                "<hierarchy rotation=\"0\">\n" +
                "\t<node bounds=\"[0,0][320,320]\" checkable=\"false\" checked=\"false\"" +
                " class=\"android.widget.FrameLayout\" clickable=\"false\" content-desc=\"\"" +
                " enabled=\"true\" focusable=\"false\" focused=\"false\" index=\"0\" long-clickable=\"false\"" +
                " password=\"false\" resource-id=\"\" scrollable=\"false\"" +
                " selected=\"false\" text=\"很抱歉已停止\">\n" +
                "\t</node>\n" +
                "</hierarchy>";

        // mock compare state
        GUIState compareState = new GUIState(DocumentHelper.parseText(str), new ArrayList<AndroidEvent>());

        // mock new state
        GUIState newState = new GUIState(DocumentHelper.parseText(str), new ArrayList<AndroidEvent>());
        List<String> imageList = new ArrayList<>();
        imageList.add("img1");
        imageList.add("img2");
        Field imageListField = GUIState.class.getDeclaredField("imageList");
        imageListField.setAccessible(true);
        imageListField.set(newState, imageList);

        // test
        Method method = EquivalentStateStrategy.class.getDeclaredMethod("markEquivalentState", GUIState.class, GUIState.class);
        method.setAccessible(true);
        method.invoke(this.listGridViewEquivalentStrategy, compareState, newState);
        Field eqGUIStateField = EquivalentStateStrategy.class.getDeclaredField("eqGUIState");
        eqGUIStateField.setAccessible(true);
        GUIState actual = (GUIState) eqGUIStateField.get(this.listGridViewEquivalentStrategy);
        assertEquals(compareState, actual);
    }
}
