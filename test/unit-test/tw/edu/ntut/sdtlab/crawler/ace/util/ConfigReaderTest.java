package tw.edu.ntut.sdtlab.crawler.ace.util;

import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.ConfigReader;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigReaderTest {
    private ConfigReader configReader;
    private Device device;
    private final String CONFIG_FILE_PATH = "test_gui_pages/0.xml";
    private final String SLASH = "/";
    private final String REPORT_PATH = System.getProperty("user.dir") + SLASH + Utility.getReportPath();

    @Before
    public void setup() throws FileNotFoundException {
        Config config = new Config();
        this.device = new Device(config.getDeviceSerialNum());
        this.configReader = new ConfigReader(this.device);
    }

    @Test
    public void testCreatePath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String path = "test_create_path";
        Method method = ConfigReader.class.getDeclaredMethod("createPath", String.class);
        method.setAccessible(true);
        method.invoke(this.configReader, path);
        File file = new File(path);
        assertTrue(file.exists());
        file.delete();
        assertFalse(file.exists());
    }

    @Test
    public void testCreateReportPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = ConfigReader.class.getDeclaredMethod("createReportPath");
        method.setAccessible(true);
        method.invoke(this.configReader);
        File file = new File(REPORT_PATH);
        assertTrue(file.exists());
    }

    @Test
    public void testCreateDotPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String DOT_PATH = REPORT_PATH + SLASH + "Dot";
        Method method = ConfigReader.class.getDeclaredMethod("createDotPath");
        method.setAccessible(true);
        method.invoke(this.configReader);
        File file = new File(DOT_PATH);
        assertTrue(file.exists());
    }

    @Test
    public void testCreateASDPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String ASD_PATH = REPORT_PATH + SLASH + "ActivitySubstateDiagram";
        Method method = ConfigReader.class.getDeclaredMethod("createASDPath");
        method.setAccessible(true);
        method.invoke(this.configReader);
        File file = new File(ASD_PATH);
        assertTrue(file.exists());
    }

    @Test
    public void testCreateStateFolder() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String STATE_FOLDER = REPORT_PATH + SLASH + "States";
        Method method = ConfigReader.class.getDeclaredMethod("createStateFolder");
        method.setAccessible(true);
        method.invoke(this.configReader);
        File file = new File(STATE_FOLDER);
        assertTrue(file.exists());
    }

    @After
    public void teardown() {
        File reportPath = new File(REPORT_PATH);
        this.deleteFilesInDirectory(reportPath);
        reportPath.delete();
        assertFalse(reportPath.exists());
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                this.deleteFilesInDirectory(file);
            file.delete();
        }
    }
}
