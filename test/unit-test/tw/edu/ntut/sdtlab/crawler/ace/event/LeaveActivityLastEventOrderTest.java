package tw.edu.ntut.sdtlab.crawler.ace.event;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LeaveActivityLastEventOrderTest {
    private EventOrder eventOrder;

    @Before
    public void setup() {
        this.eventOrder = new LeaveActivityLastEventOrder();
    }

    @Test
    public void testOrder() {
        Point point = new Point(0, 0);
        String testStr = "test";
        EventData eventData = new EventData("[0,0][100,100]", testStr);
        AndroidEvent checkEvent = new ClickEvent(eventData, ClickEvent.Type.Check);
        eventData.setSwipeDirection(SwipeEvent.Direction.RIGHT);
        AndroidEvent scrollEvent = new SwipeEvent(eventData, SwipeEvent.Type.SCROLL);
        AndroidEvent swipeEvent = new SwipeEvent(eventData, SwipeEvent.Type.SWIPE);
        eventData.setValue("123");
        List<EventData> eventDatas = new ArrayList<>();
        eventDatas.add(eventData);
        AndroidEvent editTextEvent = new EditTextEvent(eventDatas);
        AndroidEvent clickEvent = initialClickEvent(point, testStr);
        AndroidEvent longClickEvent = new LongClickEvent(eventData);
        AndroidEvent backKeyEvent = new BackKeyEvent();
        AndroidEvent menuKeyEvent = new MenuKeyEvent();

        List<AndroidEvent> unOrderEvents = new ArrayList<>();
        unOrderEvents.add(menuKeyEvent);
        unOrderEvents.add(backKeyEvent);
        unOrderEvents.add(longClickEvent);
        unOrderEvents.add(clickEvent);
        unOrderEvents.add(editTextEvent);
        unOrderEvents.add(swipeEvent);
        unOrderEvents.add(scrollEvent);
        unOrderEvents.add(checkEvent);

        List<AndroidEvent> expectEvents = new ArrayList<>();
        expectEvents.add(checkEvent);
        expectEvents.add(scrollEvent);
        expectEvents.add(swipeEvent);
        expectEvents.add(editTextEvent);
        expectEvents.add(clickEvent);
        expectEvents.add(longClickEvent);
        expectEvents.add(backKeyEvent);
        expectEvents.add(menuKeyEvent);

        List<AndroidEvent> actualEvents = this.eventOrder.order(unOrderEvents);
        assertEquals(actualEvents.size(), expectEvents.size());
        for (int i = 0; i < actualEvents.size(); i++) {
            assertEquals(expectEvents.get(i), actualEvents.get(i));
        }
    }

    private AndroidEvent initialClickEvent(Point point, String testStr) {
        EventData eventData = new EventData();
        eventData.setCenterPoint(point);
        eventData.setTempLabel(testStr);
        return new ClickEvent(eventData, ClickEvent.Type.Click);
    }

    @Test
    public void testOrderEmptyList() {
        List<AndroidEvent> emptyEvents = new ArrayList<>();
        List<AndroidEvent> result = this.eventOrder.order(emptyEvents);
        assertEquals(result.size(), 0);
        assertFalse(emptyEvents == result);
    }
}
