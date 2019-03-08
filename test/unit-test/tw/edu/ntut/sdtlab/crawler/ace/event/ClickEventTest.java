package tw.edu.ntut.sdtlab.crawler.ace.event;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ClickEventTest {
    private AndroidEvent clickEvent;
    private String tempLabel;
    private Point point;

    @Before
    public void setUp() throws Exception {
        this.initialClickEvent();
        testGetName();
        testGetReportLabel();
    }

    private void initialClickEvent() {
        this.point = new Point(100, 100);
        this.tempLabel = "testClickLabel";
        EventData eventData = new EventData();
        eventData.setCenterPoint(this.point);
        eventData.setTempLabel(tempLabel);
        this.clickEvent = new ClickEvent(eventData, ClickEvent.Type.Click);
    }

    @Test
    public void testGetCommand() {
        String[] expect = new String[]{"shell", "input", "tap", "100", "100"};
        String[] actual = this.clickEvent.getCommand();
        assertArrayEquals(expect, actual);
    }

    @Test
    public void testExecuteOn() throws InterruptedException, IOException, ExecuteCommandErrorException {
        Config config = new Config();
        Device device = new Device(config.getDeviceSerialNum());
        this.clickEvent.executeOn(device);
    }

    @Test
    public void testGetReportLabel() {
        String expect = "click(\\\"" + this.tempLabel + "\\\")";
        String actual = this.clickEvent.getReportLabel();
        assertEquals(expect, actual);
    }

    @Test
    public void testGetName() {
        String expect = "Click Event";
        String actual = this.clickEvent.getName();
        assertEquals(expect, actual);
    }
}
