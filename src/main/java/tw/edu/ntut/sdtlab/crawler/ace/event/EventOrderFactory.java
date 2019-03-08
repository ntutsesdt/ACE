package tw.edu.ntut.sdtlab.crawler.ace.event;

import tw.edu.ntut.sdtlab.crawler.ace.util.Config;

public class EventOrderFactory {
    public EventOrder createEventOrder() {
        String eventOrder = Config.EVENT_ORDER.toLowerCase();
        if (eventOrder.equals("leaveactivitylast"))
            return new LeaveActivityLastEventOrder();
        else if (eventOrder.equals("leaveactivityfirst"))
            return new LeaveActivityFirstEventOrder();
        else if (eventOrder.equals("random"))
            return new RandomEventOrder();
        return null;
    }
}
