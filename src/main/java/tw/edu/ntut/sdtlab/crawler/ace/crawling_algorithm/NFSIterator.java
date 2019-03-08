package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import org.dom4j.DocumentException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class NFSIterator extends CrawlingIterator {
    private StateGraph stateGraph;
    private List<AndroidEvent> eventSequence = null;
    private AndroidCrawler crawler;

    public NFSIterator(StateGraph stateGraph, AndroidCrawler crawler) {
        this.stateGraph = stateGraph;
        this.crawler = crawler;
        this.eventSequence = new ArrayList<AndroidEvent>();
    }

    @Override
    public void first() throws CrawlerControllerInitialErrorException, DocumentException, InterruptedException, ExecuteCommandErrorException, IOException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        this.crawler.startExplore();
        this.eventSequence = this.getUnfiredEventSequence(this.crawler.getCurrentState());
    }

    @Override
    public boolean isDone() {
        return this.crawler.getCurrentState().isExactlyEquivalentTo(this.crawler.getRootState()) && this.eventSequence.isEmpty();
    }

    @Override
    public List<AndroidEvent> getEventSequence() {
        return this.eventSequence;
    }

    @Override
    public void next() throws IndexOutOfBoundsException, CrawlerControllerInitialErrorException, DocumentException, InterruptedException, ExecuteCommandErrorException, IOException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        this.eventSequence = this.getUnfiredEventSequence(this.crawler.getCurrentState());
        if (this.eventSequence.isEmpty()) {
            this.crawler.restartApp();
            this.eventSequence = this.getUnfiredEventSequence(this.crawler.getCurrentState());
        }
    }

    // get nearest unfired event sequence
    private List<AndroidEvent> getUnfiredEventSequence(GUIState state) {
        if (state.isOverCrossAppEventThreshold()) {
            ArrayList<AndroidEvent> emptyEventSequence = new ArrayList<AndroidEvent>();
            return emptyEventSequence;
        }
        return stateGraph.getShortestUnfiredPath(state);
    }
}
