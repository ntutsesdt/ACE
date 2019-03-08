package tw.edu.ntut.sdtlab.crawler.ace.output_builder;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class STDBuilderTest {
    private ReportBuilder builder;
    private File testFolder;

    @Before
    public void setup() throws FileNotFoundException, UnsupportedEncodingException {
        List<GUIState> states = new ArrayList<>();
        List<String> mockList = new ArrayList<>();
        this.testFolder = new File(Utility.getReportPath() + "/Dot");
        this.testFolder.mkdirs();
        this.builder = new STDBuilder(new StateGraph());
    }

    @Test
    public void testBuildDot() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method addHeader = STDBuilder.class.getDeclaredMethod("addHeader");
        addHeader.setAccessible(true);
        Method addFooter = STDBuilder.class.getDeclaredMethod("addFooter");
        addFooter.setAccessible(true);
        addHeader.invoke(this.builder);
        addFooter.invoke(this.builder);
        File file = new File(Utility.getReportPath() + "/Dot" + "/StateTransitionDiagram.dot");
        assertTrue(file.exists());
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
