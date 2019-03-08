package tw.edu.ntut.sdtlab.crawler.ace.event;

import tw.edu.ntut.sdtlab.crawler.ace.util.NodeAttribute;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.Element;

import java.io.Serializable;

public class EventData implements Serializable {
    private Point centerPoint = null;
    private String tempLabel = null;
    private int backspaceCount = 0; // EditTextEvent
    private String value = null; // EditTextEvent
    private Point startPoint = null; // SwipeEvent
    private Point endPoint = null; // SwipeEvent
    private String bounds = "";
    private SwipeEvent.Direction swipeDirection = null; // only use for swipe or scroll event

    public EventData() {

    }

    public EventData(Element element) {
        this.bounds = element.attributeValue(NodeAttribute.Bounds);
        this.centerPoint = PointHelper.getCenterPoint(this.getUpperLeftPoint(), this.getLowerRightPoint());
        this.backspaceCount = element.attributeValue(NodeAttribute.Text).length();
        String textValue = element.attributeValue(NodeAttribute.Text);
        if (textValue.isEmpty()) {
            this.tempLabel = "index=" + element.attributeValue(NodeAttribute.Index);
        } else {
            int outputMaxEventLabelLength = Config.OUTPUT_MAX_EVENT_LABEL_LENGTH;
            if (textValue.length() > outputMaxEventLabelLength)
                textValue = textValue.substring(0,outputMaxEventLabelLength) + "...";
            this.tempLabel = textValue + " " + "index=" + element.attributeValue(NodeAttribute.Index);
        }
        if(element.attributeValue("direction") != null){
            String[] values = this.parseCoordinate(element.attributeValue("direction"));
            this.setStartPoint(new Point(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
            this.setEndPoint(new Point(Integer.parseInt(values[2]), Integer.parseInt(values[3])));
        }
    }

    // this constructor is only for test
    public EventData(String bounds, String tempLabel) {
        this.bounds = bounds;
        this.centerPoint = PointHelper.getCenterPoint(this.getUpperLeftPoint(), this.getLowerRightPoint());
        this.tempLabel = tempLabel;
    }

    public Point getUpperLeftPoint() {
        String[] parseBounds = this.parseCoordinate(this.bounds);
        return new Point(Integer.parseInt(parseBounds[0]), Integer.parseInt(parseBounds[1]));
    }

    public Point getLowerRightPoint() {
        String[] parseBounds = this.parseCoordinate(this.bounds);
        return new Point(Integer.parseInt(parseBounds[2]), Integer.parseInt(parseBounds[3]));
    }

    private String[] parseCoordinate(String str) {
        String parseBounds = str.replace("[", "");
        parseBounds = parseBounds.substring(0, parseBounds.length() - 1);
        parseBounds = parseBounds.replace("]", ",");
        return parseBounds.split(",");
    }

    public void setCenterPoint(Point centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Point getCenterPoint() {
        return this.centerPoint;
    }

    public void setTempLabel(String tempLabel) {
        this.tempLabel = tempLabel;
    }

    public String getTempLabel() {
        return this.tempLabel;
    }

    public void setBackspaceCount(int backspaceCount) {
        this.backspaceCount = backspaceCount;
    }

    public int getBackspaceCount() {
        return this.backspaceCount;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getStartPoint() {
        return this.startPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    public Point getEndPoint() {
        return this.endPoint;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public String getBounds() {
        return this.bounds;
    }

    public SwipeEvent.Direction getSwipeDirection() {
        if (this.swipeDirection == null)
            throw new NullPointerException();
        return this.swipeDirection;
    }

    public void setSwipeDirection(SwipeEvent.Direction swipeDirection) {
        this.swipeDirection = swipeDirection;
    }
}
