package tw.edu.ntut.sdtlab.crawler.ace.event;

import java.util.ArrayList;
import java.util.List;

public class LeaveActivityFirstEventOrder implements EventOrder {
    @Override
    public List<AndroidEvent> order(List<AndroidEvent> events) {
        List<AndroidEvent> orderedEvents = new ArrayList<>();
        //String[] orderSequence = {"BackKey Event", "Click Event", "LongClick Event", "EditText Event", "Check Event", "Scroll Event", "Swipe Event", "MenuKey Event"};
        String[] orderSequence = {"Click Event", "EditText Event", "Check Event", "LongClick Event", "Scroll Event", "Swipe Event","MenuKey Event", "BackKey Event"};
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
