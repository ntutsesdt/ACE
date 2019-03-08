package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Printer;
import org.dom4j.DocumentException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWalkIterator extends CrawlingIterator {
    private StateGraph stateGraph;
    private List<AndroidEvent> eventSequence = null;
    private AndroidCrawler crawler;
    private GUIState lastState;
    private long recordAtTheSameStateTime, recordCrossAppTime;
    private Random random = new Random(); // 要取多次平均時，不要seed

    public RandomWalkIterator(StateGraph stateGraph, AndroidCrawler crawler) {
        this.stateGraph = stateGraph;
        this.crawler = crawler;
        this.eventSequence = new ArrayList<AndroidEvent>();
    }

    @Override
    public void first() throws IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        this.crawler.startExplore();
        this.eventSequence = this.getRandomEventFromCurrentState();
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void next() throws IndexOutOfBoundsException, IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, EventFromStateErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        this.eventSequence = getRandomEventFromCurrentState();
    }

    @Override
    public List<AndroidEvent> getEventSequence() {
        return this.eventSequence;
    }

    private List<AndroidEvent> getRandomEventFromCurrentState() throws InterruptedException, EquivalentStateException, IOException, MultipleListOrGridException, NullPackageNameException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, ProgressBarTimeoutException {
        GUIState currentState = this.crawler.getCurrentState();
        final long STATE_TIMEOUT = 1000 * Config.AT_THE_SAME_STATE_TIMEOUT;
        final long CROSS_APP_TIMEOUT = 1000 * Config.CROSS_APP_STATE_TIMEOUT;
        boolean atTheSameStateTimeout = System.currentTimeMillis() - this.recordAtTheSameStateTime > STATE_TIMEOUT;
        boolean crossAppTimeout = this.lastState != null && this.lastState.isOverCrossAppEventThreshold() && System.currentTimeMillis() - this.recordCrossAppTime > CROSS_APP_TIMEOUT;
        if (this.lastState == null) {
            this.lastState = currentState;
            this.recordAtTheSameStateTime = System.currentTimeMillis();
            if (this.lastState.isOverCrossAppEventThreshold())
                this.recordCrossAppTime = System.currentTimeMillis();
        } else if (atTheSameStateTimeout || crossAppTimeout || currentState.isCrashState() || this.crawler.isDesktopState(currentState)) {
            Printer printer = new Printer();
            if (atTheSameStateTimeout)
                printer.atTheSameStateTimeout();
            else if (crossAppTimeout)
                printer.crossAppTimeout();
            this.crawler.restartApp();
            currentState = this.crawler.getCurrentState();
            this.recordAtTheSameStateTime = System.currentTimeMillis();
        }
        if (!this.lastState.isExactlyEquivalentTo(currentState)) {  // state change
            if (!this.lastState.isOverCrossAppEventThreshold() && currentState.isOverCrossAppEventThreshold())
                this.recordCrossAppTime = System.currentTimeMillis();
            this.lastState = currentState;
            this.recordAtTheSameStateTime = System.currentTimeMillis();
        }
        List<AndroidEvent> events = new ArrayList<>();
        int eventSize = currentState.getEvents().size();
        if (eventSize > 0)
            events.add(currentState.getEvents().get(random.nextInt(currentState.getEvents().size())));
        return events;
    }
}
