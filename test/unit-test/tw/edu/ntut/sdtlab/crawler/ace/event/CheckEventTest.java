package tw.edu.ntut.sdtlab.crawler.ace.event;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CheckEventTest {
    private AndroidEvent checkEvent;
    private String tempLabel;
    private Point point;

    @Before
    public void setUp() throws Exception {
        this.point = new Point(100, 100);
        EventData eventData = new EventData("[0,0][200,200]", this.tempLabel);
        this.checkEvent = new ClickEvent(eventData, ClickEvent.Type.Check);
        testGetName();
        testGetReportLabel();
    }

    @Test
    public void testGetCommand() {
        String[] expect = new String[]{"shell", "input", "tap", "100", "100"};
        String[] actual = this.checkEvent.getCommand();
        assertArrayEquals(expect, actual);
    }

    @Test
    public void testExecuteOn() throws InterruptedException, IOException, ExecuteCommandErrorException {
        Config config = new Config();
        Device device = new Device(config.getDeviceSerialNum());
        this.checkEvent.executeOn(device);
    }


    @Test
    public void testGetReportLabel() {
        String expect = "check(\\\"" + this.tempLabel + "\\\")";
        String actual = this.checkEvent.getReportLabel();
        assertEquals(expect, actual);
    }

    @Test
    public void testGetName() {
        String expect = "Check Event";
        String actual = this.checkEvent.getName();
        assertEquals(expect, actual);
    }
}
