package tw.edu.ntut.sdtlab.crawler.ace.event;


public class ClickEvent extends AndroidEvent {
    public enum Type {Check, Click}

    private String eventName = null;
    private String[] command = null;
    private String reportLabel = null, tempLabel = null;
    private Point centerPoint = null;
    private Type type;

    public ClickEvent(EventData eventData, Type type) {
        this.centerPoint = eventData.getCenterPoint();
        this.tempLabel = eventData.getTempLabel();
        this.type = type;
        createClickEvent();
    }

    public void createClickEvent() {
        this.eventName = type.equals(Type.Click) ? "Click Event" : "Check Event";
        this.reportLabel = type.equals(Type.Click) ? "click(\\\"" + this.tempLabel + "\\\")" : "check(\\\"" + this.tempLabel + "\\\")";
        this.command = new String[]{"shell", "input", "tap", Integer.toString(this.centerPoint.x()), Integer.toString(this.centerPoint.y())};
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
        EventData eventData = new EventData();
        eventData.setCenterPoint(centerPoint);
        eventData.setTempLabel(tempLabel);
        return eventData;
    }

    @Override
    public AndroidEvent clone() {
        AndroidEventFactory androidEventFactory = new AndroidEventFactory();
        String tempEventType = type.equals(Type.Click) ? "Click" : "Check";
        return androidEventFactory.createAndroidEvent(tempEventType, this.getEventData());
    }

    @Override
    public String getName() {
        return this.eventName;
    }

}
