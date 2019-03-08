package tw.edu.ntut.sdtlab.crawler.ace.event;

import java.util.ArrayList;
import java.util.List;

public class LeaveActivityLastEventOrder implements EventOrder {
    @Override
    public List<AndroidEvent> order(List<AndroidEvent> events) {
        List<AndroidEvent> orderedEvents = new ArrayList<>();
        String[] orderSequence = {"Check Event", "Scroll Event", "Swipe Event", "EditText Event", "Click Event", "LongClick Event", "BackKey Event", "MenuKey Event"};
        int count = 0;  // index the orderSequence
        while(orderedEvents.size() < events.size()) {
            if(count > orderSequence.length - 1)
                throw new IndexOutOfBoundsException("some error : order not complete, but orderSequence does");
            for(AndroidEvent event : events) {
                if(event.getName() == orderSequence[count]) {
                    orderedEvents.add(event);
                }
            }
            count += 1;
        }
        return orderedEvents;
    }
}
