package tw.edu.ntut.sdtlab.crawler.ace.event;

import java.util.List;

// use to order the eventSequence in GUIState, usually be called in GUIState constructor
public interface EventOrder {
    List<AndroidEvent> order(List<AndroidEvent> events);
}
