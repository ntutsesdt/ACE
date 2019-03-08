package tw.edu.ntut.sdtlab.crawler.ace.event;

public class LongClickEvent extends AndroidEvent {
    private String eventName = null;
    private String reportLabel = null, tempLabel = null;
    private Point centerPoint = null;
    private String[] command = null;
    private final String DELAY_TIME = String.valueOf(1500);

    public LongClickEvent(EventData eventData) {
        this.centerPoint = eventData.getCenterPoint();
        this.tempLabel = eventData.getTempLabel();
        createLongClickEvent();
    }

    public void createLongClickEvent() {
        this.eventName = "LongClick Event";
        this.combinePointAndlabel();
        this.reportLabel = "longClick(\\\"" + this.tempLabel + "\\\")";
        this.command = new String[]{"shell", "input", "swipe", Integer.toString(this.centerPoint.x()), Integer.toString(this.centerPoint.y()), Integer.toString(this.centerPoint.x()), Integer.toString(this.centerPoint.y()), DELAY_TIME};
    }

    @Override
    public String[] getCommand() {
        return this.command;
    }

    @Override
    public String getReportLabel() {
        return this.reportLabel;
    }

    protected void combinePointAndlabel() {
        tempLabel = tempLabel + "[" + centerPoint.x() + "," + centerPoint.y() + "]";
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
        return androidEventFactory.createAndroidEvent("LongClick", this.getEventData());
    }

    @Override
    public String getName() {
        return this.eventName;
    }

}
