package tw.edu.ntut.sdtlab.crawler.ace.exception;

import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;

public class CannotReachTargetStateException extends Exception {
    private AndroidEvent nondeterministicEvent;

    public CannotReachTargetStateException(AndroidEvent event) {
        this.nondeterministicEvent = event;
    }

    public AndroidEvent getNondeterministicEvent() {
        return this.nondeterministicEvent;
    }
}
