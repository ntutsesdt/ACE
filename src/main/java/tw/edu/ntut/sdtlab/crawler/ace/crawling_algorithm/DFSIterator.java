package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;

import java.util.*;

import tw.edu.ntut.sdtlab.crawler.ace.util.Printer;
import org.dom4j.DocumentException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;

import java.io.IOException;


public class DFSIterator extends CrawlingIterator {
    private StateGraph stateGraph;
    private List<AndroidEvent> eventSequence = null;
    private AndroidCrawler crawler;
    private Map<GUIState, Stack<GUIState>> visitedStacks;
    private GUIState tmpRootState = null;

    public DFSIterator(StateGraph stateGraph, AndroidCrawler crawler) {
        this.stateGraph = stateGraph;
        this.crawler = crawler;
        this.visitedStacks = new HashMap<>();
    }

    @Override
    public void first() throws IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException, EventFromStateErrorException, CannotReachTargetStateException {
        this.crawler.startExplore();
        this.eventSequence = new ArrayList<AndroidEvent>();
        GUIState state = this.crawler.getCurrentState();
        Stack<GUIState> stack = new Stack<GUIState>();
        stack.push(state);
        this.visitedStacks.put(state, stack);
        this.tmpRootState = state;
        this.eventSequence.add(this.getUnfiredEvent(state));
    }

    @Override
    public boolean isDone() {
        return this.crawler.getCurrentState().isExactlyEquivalentTo(this.crawler.getRootState()) && allEventFired(this.crawler.getCurrentState());
    }

    @Override
    public void next() throws IndexOutOfBoundsException, IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, EventFromStateErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        GUIState state = this.crawler.getCurrentState();
        if (allEventFired(state) || (this.visitedStacks.get(this.tmpRootState).contains(state) && state != this.visitedStacks.get(this.tmpRootState).peek()))
            this.backtrackToPreviousState();
        else if (!this.visitedStacks.get(this.tmpRootState).contains(state))
            this.visitedStacks.get(this.tmpRootState).push(state);
        this.eventSequence = new ArrayList<AndroidEvent>();
        AndroidEvent unfiredEvent = this.getUnfiredEvent(this.crawler.getCurrentState());
        // unfired event will be null if all event are fire and current state is root state, which also means is done = true
        if (unfiredEvent != null)
            this.eventSequence.add(unfiredEvent);
    }

    @Override
    public List<AndroidEvent> getEventSequence() {
        return this.eventSequence;
    }

    private void backtrackToPreviousState() throws IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, EventFromStateErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        // restart
        this.crawler.restartApp();
        this.tmpRootState = this.crawler.getCurrentState();
        if (!this.visitedStacks.containsKey(this.tmpRootState)) {
            Stack<GUIState> stack = new Stack<GUIState>();
            this.visitedStacks.put(this.tmpRootState, stack);
        }
        // find previous node which has unfired event
        this.popVisitedStateUntilLastUnvisitedEventExist();
        if (!this.visitedStacks.get(this.tmpRootState).empty()) {
            List<AndroidEvent> toTargetStateEventSequence = this.getEventSequenceFromRootStateToTargetState(this.visitedStacks.get(this.tmpRootState).peek());
            if (toTargetStateEventSequence.isEmpty() && this.crawler.getCurrentState() != this.visitedStacks.get(this.tmpRootState).peek()) { // not route to target state (route contains overAttempCountThreshold event)
                this.visitedStacks.get(this.tmpRootState).pop();
                this.backtrackToPreviousState();
            }
            try {
                this.crawler.executeEvents(toTargetStateEventSequence);
            } catch (CannotReachTargetStateException e) {
                e.getNondeterministicEvent().increaseNondeterministicEventAttemptCount();
                new Printer().dfsBacktrackNondeterministicRetry();
                this.backtrackToPreviousState();
            }
        }
        else
            this.visitedStacks.get(this.tmpRootState).push(this.tmpRootState);
    }

    private List<AndroidEvent> getEventSequenceFromRootStateToTargetState(GUIState targetState) {
        Queue<AndroidEvent> eventQueue = new LinkedList();
        ArrayList<GUIState> visitedStateList = new ArrayList<GUIState>();
        List<AndroidEvent> eventSequence = new ArrayList<AndroidEvent>();
        visitedStateList.add(this.crawler.getCurrentState());
        for (AndroidEvent e : this.crawler.getCurrentState().getEvents()) {
            if (e.isVisited() && e.getFromState() != e.getToState()) { // not nondeterministic event
                eventQueue.add(e);
                e.setTempPreviousEvent(null); // should both two situation need initial e.prePreEvent? or only when e is nonDeterministic
            }
        }
        while (!eventQueue.isEmpty()) {
            AndroidEvent e = (AndroidEvent) eventQueue.poll();
            if (!e.isOverNondeterministicEventAttemptCountThreshold()) {
                if (e.getToState() == targetState) {
                    eventSequence.add(e);
                    //System.out.println("e.getToState() == targetState");
                    while (e.getTempPreviousEvent() != null) {
                        eventSequence.add(e.getTempPreviousEvent());
                        e = e.getTempPreviousEvent();
                        //System.out.println("e reportlabel : " + e.getReportLabel());
                    }
                    eventSequence = stateGraph.reverseElementsInEventSequence(eventSequence);
                    break;
                } else if (e.getToState() != null) {
                    for (AndroidEvent ev : e.getToState().getEvents()) {
                        //System.out.println("e.getToState() != null");
                        if (ev.isVisited() &&
                                !visitedStateList.contains(ev.getToState()) &&
                                !isToStateExistInPreviousEvent(e, ev.getToState())) {
                            visitedStateList.add(ev.getToState());
                            eventQueue.add(ev);
                            ev.setTempPreviousEvent(e);// set previousEvent
                        }
                    }
                }
            }
        }
        return eventSequence;
    }

    private void popVisitedStateUntilLastUnvisitedEventExist() {
        while (!this.visitedStacks.get(this.tmpRootState).isEmpty()) {
            GUIState topState = this.visitedStacks.get(this.tmpRootState).peek();
            if (allEventFired(topState) || topState.isOverCrossAppEventThreshold()) {
                this.visitedStacks.get(this.tmpRootState).pop();
                continue;
            }
            break;
        }
    }

    private boolean isToStateExistInPreviousEvent(AndroidEvent e, GUIState s) {
        while (e != null) {
            if (e.getFromState() == s)
                return true;
            e = e.getTempPreviousEvent();
        }
        return false;
    }

    private AndroidEvent getUnfiredEvent(GUIState currentState) throws IOException, MultipleListOrGridException, EventFromStateErrorException, NullPackageNameException, InterruptedException, ClickTypeErrorException, EquivalentStateException, ExecuteCommandErrorException, CrawlerControllerInitialErrorException, CannotReachTargetStateException, ProgressBarTimeoutException, DocumentException {
        AndroidEvent event = null;
        if (!currentState.isOverCrossAppEventThreshold()) {
            for (AndroidEvent e : currentState.getEvents()) {
                if (!e.isVisited() && !e.isOverNondeterministicEventAttemptCountThreshold()) {
                    return e;
                }
            }
        } else
            this.backtrackToPreviousState();
        return event;
    }

    private boolean allEventFired(GUIState state) {
        List<AndroidEvent> events = state.getEvents();
        for (AndroidEvent event : events) {
            if (!event.isVisited())
                return false;
        }
        return true;
    }
}