package tw.edu.ntut.sdtlab.crawler.ace.event;

public class BackKeyEvent extends AndroidEvent {
    private String eventName = null;
    private String[] command = null;
    private String reportLabel = null;


    public BackKeyEvent() {
        this.eventName = "BackKey Event";
        this.command = new String[]{"shell", "input", "keyevent", "KEYCODE_BACK"};
        this.reportLabel = "press(\\\"BackKey\\\")";
    }

    @Override
    public String getReportLabel() {
        return this.reportLabel;
    }

    @Override
    public String[] getCommand() {
        return this.command;
    }

    @Override
    public EventData getEventData() {
        return new EventData();
    }

    @Override
    public AndroidEvent clone() {
        AndroidEventFactory androidEventFactory = new AndroidEventFactory();
        return androidEventFactory.createAndroidEvent("BackKey", this.getEventData());
    }

    @Override
    public String getName(){
        return this.eventName;
    }

}
