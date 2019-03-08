package tw.edu.ntut.sdtlab.crawler.ace.event;


public class SwipeEvent extends AndroidEvent {
    public enum Direction {RIGHT, UP, DOWN, LEFT}

    public enum Type {SCROLL, SWIPE}

    private final int POINT_OFFSET = 1;
    private String eventName = null;
    private String[] command = null;
    private String reportLabel = null;
    private Point startPoint = null, endPoint = null;
    private String bounds = "";
    private Type type;
    private EventData eventData;

    public SwipeEvent(EventData eventData, Type type) {
        this.eventData = eventData;
        this.bounds = this.eventData.getBounds();
        this.type = type;
        if (eventData.getStartPoint() != null && eventData.getEndPoint() != null) {
            this.startPoint = eventData.getStartPoint();
            this.endPoint = eventData.getEndPoint();
        } else
            this.initialStartEndPoint(eventData, eventData.getSwipeDirection());
        this.createSwipeEvent();
    }

    private void initialStartEndPoint(EventData eventData, Direction direction) {
        Point upperLeftPoint = eventData.getUpperLeftPoint();
        Point lowerRightPoint = eventData.getLowerRightPoint();
        switch (direction) {
            case RIGHT:
                this.startPoint = this.getLeftCenterPoint(upperLeftPoint, lowerRightPoint);
                this.endPoint = this.getRightCenterPoint(upperLeftPoint, lowerRightPoint);
                break;
            case UP:
                this.startPoint = this.getDownCenterPoint(upperLeftPoint, lowerRightPoint);
                this.endPoint = this.getUpCenterPoint(upperLeftPoint, lowerRightPoint);
                break;
            case DOWN:
                this.startPoint = this.getUpCenterPoint(upperLeftPoint, lowerRightPoint);
                this.endPoint = this.getDownCenterPoint(upperLeftPoint, lowerRightPoint);
                break;
            case LEFT:
                this.startPoint = this.getRightCenterPoint(upperLeftPoint, lowerRightPoint);
                this.endPoint = this.getLeftCenterPoint(upperLeftPoint, lowerRightPoint);
                break;
        }
    }

    private Point getLeftCenterPoint(Point upperLeftPoint, Point lowerRightPoint) {
        int leftX = upperLeftPoint.x() + POINT_OFFSET;
        int leftY = (lowerRightPoint.y() + upperLeftPoint.y()) / 2;
        return new Point(leftX, leftY);
    }

    private Point getRightCenterPoint(Point upperLeftPoint, Point lowerRightPoint) {
        int rightX = lowerRightPoint.x() - POINT_OFFSET;
        int rightY = (lowerRightPoint.y() + upperLeftPoint.y()) / 2;
        return new Point(rightX, rightY);
    }

    private Point getUpCenterPoint(Point upperLeftPoint, Point lowerRightPoint) {
        int upX = (lowerRightPoint.x() + upperLeftPoint.x()) / 2;
        int upY = upperLeftPoint.y() + POINT_OFFSET;
        return new Point(upX, upY);
    }

    private Point getDownCenterPoint(Point upperLeftPoint, Point lowerRightPoint) {
        int downX = (lowerRightPoint.x() + upperLeftPoint.x()) / 2;
        int downY = lowerRightPoint.y() - POINT_OFFSET;
        return new Point(downX, downY);
    }

    public void createSwipeEvent() {
        this.command = new String[]{"shell", "input", "swipe", Integer.toString(this.startPoint.x()),
                Integer.toString(this.startPoint.y()), Integer.toString(this.endPoint.x()),
                Integer.toString(this.endPoint.y())};

        this.createLabel(this.startPoint, this.endPoint, this.type);
        if (type == Type.SWIPE) {
            this.eventName = "Swipe Event";
        } else
            this.eventName = "Scroll Event";
    }

    @Override
    public String getReportLabel() {
        return this.reportLabel;
    }

    @Override
    public String[] getCommand() {
        return this.command;
    }

    private void createLabel(Point startPoint, Point endPoint, Type type) {
        String event = null;
        if (type == Type.SWIPE)
            event = "swipe";
        else if (type == Type.SCROLL)
            event = "scroll";
        if (startPoint.x() == endPoint.x()) {
            if (endPoint.y() > startPoint.y()) {
                reportLabel = event + "(\\\"Down\\\")";
            } else if (endPoint.y() < startPoint.y()) {
                reportLabel = event + "(\\\"Up\\\")";
            } else {
                reportLabel = event + "(\\\"as click\\\")";
            }
        } else if (startPoint.y() == endPoint.y()) {
            if (endPoint.x() > startPoint.x()) {
                reportLabel = event + "(\\\"Right\\\")";
            } else if (endPoint.x() < startPoint.x()) {
                reportLabel = event + "(\\\"Left\\\")";
            }
        } else {
            reportLabel = event + "\\\"(" + this.startPoint.x() + "," + this.startPoint.y() + ") to (" + this.endPoint.x() + "," + this.endPoint.y() + ")\\\"";
        }
    }

    @Override
    public EventData getEventData() {
        EventData newEventData = new EventData();
        newEventData.setStartPoint(startPoint);
        newEventData.setEndPoint(endPoint);
        newEventData.setBounds(bounds);
        newEventData.setSwipeDirection(this.eventData.getSwipeDirection());
        return newEventData;
    }

    @Override
    public AndroidEvent clone() {
        AndroidEventFactory androidEventFactory = new AndroidEventFactory();
        if (this.type == Type.SCROLL)
            return androidEventFactory.createAndroidEvent("Scroll", this.getEventData());
        else
            return androidEventFactory.createAndroidEvent("Swipe", this.getEventData());
    }

    @Override
    public String getName() {
        return this.eventName;
    }

}
