package tw.edu.ntut.sdtlab.crawler.ace.event;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.util.TimeHelper;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class BackKeyEventTest {
    private AndroidEvent backKeyEvent;

    @Before
    public void setUp() throws Exception {
        this.backKeyEvent = new BackKeyEvent();
    }

    @Ignore
    public void testExecuteOn() throws InterruptedException, IOException, ExecuteCommandErrorException {
        Config config = new Config();
        Device device = new Device(config.getDeviceSerialNum());
        String[] homeCommand = {"shell", "input", "keyevent", "KEYCODE_HOME"};
        device.executeADBCommand(homeCommand);
        TimeHelper.sleep(1000);
        String[] recentAppCommand = new String[]{"shell", "input", "keyevent", "KEYCODE_APP_SWITCH"};
        device.executeADBCommand(recentAppCommand);
        this.backKeyEvent.executeOn(device);
        TimeHelper.sleep(1000);
        String[] packageNameCommand = new String[]{"shell", "dumpsys", "window", "|", "grep", "-E", "mFocusedApp"};
        List<String> packageName = device.executeADBCommand(packageNameCommand);
        assertTrue(packageName.get(0).contains("HomeActivity"));
    }

    @Test
    public void testGetCommand() {
        String[] expect = new String[]{"shell", "input", "keyevent", "KEYCODE_BACK"};
        String[] actual = this.backKeyEvent.getCommand();
        assertArrayEquals(expect, actual);
    }

    @Test
    public void testGetReportLabel() {
        String expect = "press(\\\"BackKey\\\")";
        String actual = this.backKeyEvent.getReportLabel();
        assertEquals(expect, actual);
    }

    @Test
    public void testGetName() {
        String expect = "BackKey Event";
        String actual = this.backKeyEvent.getName();
        assertEquals(expect, actual);
    }

    @Test
    public void testGetTag() throws NoSuchFieldException, IllegalAccessException {
        String tag = "testTag";
        Field field = AndroidEvent.class.getDeclaredField("tag");
        field.setAccessible(true);
        field.set(this.backKeyEvent, tag);
        assertTrue(this.backKeyEvent.getTag().compareTo(tag) == 0);
    }

    @Test
    public void testSetTag() throws NoSuchFieldException, IllegalAccessException {
        String tag = "testTag";
        this.backKeyEvent.setTag(tag);
        Field field = AndroidEvent.class.getDeclaredField("tag");
        field.setAccessible(true);
        assertEquals(tag, (String) field.get(this.backKeyEvent));
    }

    @Test
    public void testSetOrder() throws NoSuchFieldException, IllegalAccessException {
        String expect = "1";
        this.backKeyEvent.setOrder(expect);
        Field field = AndroidEvent.class.getDeclaredField("order");
        field.setAccessible(true);
        String actual = (String) field.get(this.backKeyEvent);
        assertEquals(expect, actual);
        this.backKeyEvent.setOrder("2");
        actual = (String) field.get(this.backKeyEvent);
        expect = "1, 2";
        assertEquals(expect, actual);
    }
}
