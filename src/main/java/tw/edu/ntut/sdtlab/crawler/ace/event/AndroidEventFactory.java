package tw.edu.ntut.sdtlab.crawler.ace.event;


import java.util.ArrayList;
import java.util.List;

public class AndroidEventFactory {

    public AndroidEvent createAndroidEvent(String eventType, EventData eventData) {
        AndroidEvent androidEvent;
        switch (eventType) {
            case "BackKey":
                androidEvent = new BackKeyEvent();
                break;
            case "MenuKey":
                androidEvent = new MenuKeyEvent();
                break;
            case "Check":
                androidEvent = new ClickEvent(eventData, ClickEvent.Type.Check);
                break;
            case "Click":
                androidEvent = new ClickEvent(eventData, ClickEvent.Type.Click);
                break;
            case "EditText":
                List<EventData> eventDatas = new ArrayList<>();
                eventDatas.add(eventData);
                androidEvent = new EditTextEvent(eventDatas);
                break;
            case "LongClick":
                androidEvent = new LongClickEvent(eventData);
                break;
            case "Swipe":   // create both swipe or scroll
                androidEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
                break;
            case "Scroll":
                androidEvent = new SwipeEvent(eventData, SwipeEvent.Type.SCROLL);
                break;
            default:
                throw new NullPointerException();
        }
        return androidEvent;
    }

    public AndroidEvent createAndroidEvent(String eventType, List<EventData> eventDatas) {
        AndroidEvent androidEvent;
        switch (eventType) {
            case "EditText":
                androidEvent = new EditTextEvent(eventDatas);
                break;
            default:
                throw new NullPointerException();
        }
        return androidEvent;
    }
}
