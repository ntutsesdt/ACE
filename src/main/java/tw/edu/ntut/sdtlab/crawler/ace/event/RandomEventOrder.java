package tw.edu.ntut.sdtlab.crawler.ace.event;

import java.util.Collections;
import java.util.List;

public class RandomEventOrder implements EventOrder {
    @Override
    public List<AndroidEvent> order(List<AndroidEvent> events) {
        Collections.shuffle(events);
        return events;
    }
}
