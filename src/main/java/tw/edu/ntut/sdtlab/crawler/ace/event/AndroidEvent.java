package tw.edu.ntut.sdtlab.crawler.ace.event;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.util.TimeHelper;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;

import java.io.IOException;
import java.io.Serializable;

public abstract class AndroidEvent implements Serializable {
    private GUIState fromState = null;
    private GUIState toState = null;
    private boolean isVisited;
    private AndroidEvent tempPreviousEvent = null;
    private boolean isNonDeterministicEvent = false;
    private int nondeterministicEventAttemptCount = 0;
    private String tag;
    private String text;
    protected int count; // total execute times
    protected String order = ""; // order of execute
    protected boolean orderFlag = false;
    protected int toStatePictureId;


    public abstract String getName();

    //public abstract AndroidEvent clone();
    public abstract String[] getCommand();

    public abstract String getReportLabel();

    public abstract EventData getEventData();

    public abstract AndroidEvent clone();

    protected void execute(Device device) throws InterruptedException, ExecuteCommandErrorException, IOException {
        device.executeADBCommand(this.getCommand());
    }

    public void executeOn(Device device) throws ExecuteCommandErrorException, InterruptedException, IOException {
        this.execute(device);
        TimeHelper.sleep((long) (Config.EVENT_SLEEP_TIMESECOND * 1000));
    }

    public boolean isVisited() {
        return this.isVisited;
    }

    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public GUIState getToState() {
        return this.toState;
    }

    public void setToState(GUIState toState) {
        this.toState = toState;
    }

    public GUIState getFromState() {
        return this.fromState;
    }

    public void setFromState(GUIState fromState) {
        this.fromState = fromState;
    }

    public void setNondeterministic(boolean isNondeterministic) {
        this.isNonDeterministicEvent = isNondeterministic;
    }

    public boolean isNonDeterministic() {
        return this.isNonDeterministicEvent;
    }

    public int getNondeterministicEventAttemptCount() {
        return this.nondeterministicEventAttemptCount;
    }

    public void increaseNondeterministicEventAttemptCount() {
        this.nondeterministicEventAttemptCount += 1;
    }

    public void setTempPreviousEvent(AndroidEvent preEvent) {
        this.tempPreviousEvent = preEvent;
    }

    public AndroidEvent getTempPreviousEvent() {
        return this.tempPreviousEvent;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setOrder(String order) {
        if (this.order.isEmpty())
            this.order = order;
        else
            this.order += ", " + order;
    }

    public String getOrder() {
        return this.order;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public void setToStatePictureId(int toStatePictureId) {
        this.toStatePictureId = toStatePictureId;
    }

    public int getToStatePictureId() {
        return toStatePictureId;
    }

    public boolean isToOriginalState() {
        return !this.getToState().isEquivalentState() || this.toStatePictureId == this.toState.getId();
    }

    public boolean isOverNondeterministicEventAttemptCountThreshold() {
        return nondeterministicEventAttemptCount >= Config.NONDETERMINISTIC_EVENT_ATTEMPT_COUNT_THRESHOLD;
    }
}
