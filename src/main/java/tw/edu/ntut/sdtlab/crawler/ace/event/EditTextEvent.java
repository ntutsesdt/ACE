package tw.edu.ntut.sdtlab.crawler.ace.event;

import java.io.IOException;
import java.util.List;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;

public class EditTextEvent extends AndroidEvent {
    private String eventName = null;
    private String reportLabel = "",
            tempLabel = null,
            text = null;
    private Point clickPoint = null;
    private int backSpaceCount = 0;
    private List<EventData> eventDatas;

    public EditTextEvent(List<EventData> eventDatas) {
        this.eventDatas = eventDatas;
        createEditTextEvent();
    }

    public void createEditTextEvent() {
        this.eventName = "EditText Event";
        for (EventData eventData : eventDatas) {
            this.reportLabel += "SetEditText(\\\"" + eventData.getTempLabel() + "\\\"\\," + " \\\"" + eventData.getValue() + "\\\")";
        }
    }

    protected void execute(Device device) throws IOException, ExecuteCommandErrorException, InterruptedException {
        for (EventData eventData : this.eventDatas) {
            this.clickPoint = new Point(eventData.getLowerRightPoint().x() - 1, eventData.getLowerRightPoint().y() - 1);
            this.text = eventData.getValue();
            this.tempLabel = eventData.getTempLabel();
            this.backSpaceCount = eventData.getBackspaceCount();
            this.clearText(device);
            this.enterValue(device);
            this.turnOffSoftKeyboard(device);
        }
    }

    @Override
    public String[] getCommand() {
        throw new NullPointerException();
    }

    @Override
    public String getReportLabel() {
        return this.reportLabel;
    }

    protected void clearText(Device device) throws IOException, ExecuteCommandErrorException, InterruptedException {
        String[] clickCommand = new String[]{"shell", "input", "tap", Integer.toString(clickPoint.x()), Integer.toString(clickPoint.y())};
        device.executeADBCommand(clickCommand);
        String[] backSpaceCommand = new String[]{"shell", "input", "keyevent", "KEYCODE_DEL"};
        int count = this.backSpaceCount;
        while (count > 0) {
            device.executeADBCommand(backSpaceCommand);
            count--;
        }

    }

    protected void enterValue(Device device) throws IOException, ExecuteCommandErrorException, InterruptedException {
        String tempValue;
        for (int i = 0; i < text.length(); i++) {
            tempValue = text.substring(i, i + 1);
            if (tempValue.compareTo(" ") == 0) {
                tempValue = "%s";
            }
            String[] command = new String[]{"shell", "input", "text", "\"" + tempValue + "\""};
            device.executeADBCommand(command);
        }
    }

    protected void turnOffSoftKeyboard(Device device) throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] command = {"shell", "dumpsys",
                "input_method", "|", "grep", "\"mInputShown=true\""};
        String feedBack = null;
        device.executeADBCommand(command);
        if (feedBack != null) {
            (new BackKeyEvent()).executeOn(device);
        }
    }

    @Override
    public EventData getEventData() {
        return null;
    }

    @Override
    public AndroidEvent clone() {
        AndroidEventFactory androidEventFactory = new AndroidEventFactory();
        return androidEventFactory.createAndroidEvent("EditText", this.eventDatas);
    }

    @Override
    public String getName() {
        return this.eventName;
    }
}
