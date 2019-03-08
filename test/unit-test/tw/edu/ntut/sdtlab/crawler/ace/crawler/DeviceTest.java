package tw.edu.ntut.sdtlab.crawler.ace.crawler;

import tw.edu.ntut.sdtlab.crawler.ace.util.CommandHelper;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ProgressBarTimeoutException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.UnrecognizableStateException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.DocumentException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceTest {
    private Device device;
    private final String PACKAGE_NAME = Config.PACKAGE_NAME;
    private final String ACTIVITY_NAME = Config.LAUNCHER_ACTIVITY;
    final String TEST_DATA_FOLDER_PATH = "test/unit-test/tw/edu/ntut/sdtlab/crawler/ace/test-data";

    @Before
    public void setup() throws InterruptedException, ExecuteCommandErrorException, IOException {
        Config config = new Config();
        final String DEVICE_SERIAL_NUM = config.getDeviceSerialNum();
        this.device = new Device(DEVICE_SERIAL_NUM);

        // assert device exist
        List<String> result = CommandHelper.executeCmd(Config.ADB_PATH, "devices");
        assertTrue(this.isDeviceExist(DEVICE_SERIAL_NUM, result));
    }

    private boolean isDeviceExist(final String SERIAL_NUM, List<String> devices) {
        for (String str : devices) {
            if (str.contains(SERIAL_NUM))
                return true;
        }
        return false;
    }

    @Test
    public void testStartApp() throws IOException, InterruptedException, ExecuteCommandErrorException {
        this.device.startApp(PACKAGE_NAME, ACTIVITY_NAME);
    }

    @Test
    public void testStopApp() throws IOException, InterruptedException, ExecuteCommandErrorException {
        this.device.stopApp(PACKAGE_NAME);
    }

    @Test
    public void testPressHome() throws IOException, InterruptedException, ExecuteCommandErrorException {
        this.device.pressHome();
    }

    @Test
    public void testClearAppData() throws IOException, InterruptedException, ExecuteCommandErrorException {
        this.device.clearAppData(PACKAGE_NAME);
    }

    @Test
    public void testGetActivityName() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String activityName = this.device.getActivityName();
        assertFalse(activityName.isEmpty());
//        assertTrue("activityName = " + activityName, activityName.equals("Mileage"));
    }

    @Test
    public void testGetScreenShot() throws InterruptedException, ExecuteCommandErrorException, IOException {
//        final String IMG_FILE_PATH = "test_gui_pages/1.png";
        final String IMG_FILE_PATH = TEST_DATA_FOLDER_PATH + "/1.png";
        this.device.getScreenShot(IMG_FILE_PATH);
        File file = new File(IMG_FILE_PATH);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testTurnOffSoftKeyboard() throws InterruptedException, ExecuteCommandErrorException, IOException {
        this.device.turnOffSoftKeyboard();
        Config config = new Config();
        String[] command = {Config.ADB_PATH, "-s", config.getDeviceSerialNum(), "shell", "dumpsys", "input_method", "|", "grep", "\"mInputShown=false\""};
        List<String> result = CommandHelper.executeCmd(command);
        assertTrue(result.get(0).contains("mInputShown=false"));
    }

    @Test
    public void testDumpXML() throws InterruptedException, ExecuteCommandErrorException, IOException, UnrecognizableStateException, DocumentException, ProgressBarTimeoutException {
//        final String IMG_FILE_PATH = "test_gui_pages/1.xml";
        final String IMG_FILE_PATH = TEST_DATA_FOLDER_PATH + "/1.xml";
        this.device.dumpXML(IMG_FILE_PATH);
        File file = new File(IMG_FILE_PATH);
        assertTrue(file.exists());
        file.delete();
    }

//    @Test
//    public void testExecuteADBCommand() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Method method = Device.class.getDeclaredMethod("executeADBCommand", String[].class);
//        method.setAccessible(true);
//        String[] command = {"devices"};
//        List<String> result = (List<String>) method.invoke(this.device, command);
//        assertTrue(result.get(0).contains("List of devices attached"));
//    }
}
