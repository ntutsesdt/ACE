package tw.edu.ntut.sdtlab.crawler.ace.output_builder;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;

import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ASDBuilderTest {
    private ReportBuilder builder;

    @Before
    public void setup() {
        List<GUIState> states = new ArrayList<>();
        List<String> mockList = new ArrayList<>();
        //Result mockResult = new Result(states, mockList, mockList);
        StateGraph stateGraph = new StateGraph();
        this.builder = new ASDBuilder(stateGraph);
    }

    @Test
    public void testNonDeterministicColorTrue() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ASDBuilder.class.getDeclaredMethod("nonDeterministicColor", boolean.class);
        method.setAccessible(true);
        String actual = (String) method.invoke(this.builder, true);
        String expect = "\" ,fontcolor = DodgerBlue,color = DodgerBlue];";
        assertEquals(expect, actual);
    }

    @Test
    public void testNonDeterministicColorFalse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ASDBuilder.class.getDeclaredMethod("nonDeterministicColor", boolean.class);
        method.setAccessible(true);
        String actual = (String) method.invoke(this.builder, false);
        String expect = "\"];";
        assertEquals(expect, actual);
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
