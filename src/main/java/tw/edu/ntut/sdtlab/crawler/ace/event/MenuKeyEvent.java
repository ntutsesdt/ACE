package tw.edu.ntut.sdtlab.crawler.ace.event;


public class MenuKeyEvent extends AndroidEvent {
    private String eventName = null;
    private String[] command = null;
    private String reportLabel = null;

    public MenuKeyEvent() {
        this.eventName = "MenuKey Event";
        this.command = new String[]{"shell", "input", "keyevent", "KEYCODE_MENU"};
        this.reportLabel = "press(\\\"MenuKey\\\")";
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
        return androidEventFactory.createAndroidEvent("MenuKey", this.getEventData());
    }

    @Override
    public String getName() {
        return this.eventName;
    }

}
