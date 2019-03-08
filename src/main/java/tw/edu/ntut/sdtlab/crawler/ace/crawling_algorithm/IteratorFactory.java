package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.exception.IteratorTypeErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;


public class IteratorFactory {
    public static final int NFS = 0, DFS = 1, RANDOM_WALK = 2, STATE_TRAVERSE = 3, EVENT_TRAVERSE = 4;

    public Iterator createIterator(int type, StateGraph stateGraph, AndroidCrawler crawlerController) throws IteratorTypeErrorException {
        switch (type) {
            case IteratorFactory.NFS:
                return new NFSIterator(stateGraph, crawlerController);
            case IteratorFactory.DFS:
                return new DFSIterator(stateGraph, crawlerController);
            case IteratorFactory.RANDOM_WALK:
                return new RandomWalkIterator(stateGraph, crawlerController);
            /*case IteratorFactory.STATE_TRAVERSE:
                return new StateTraversalIterator(stateGraph, crawlerController);
            case IteratorFactory.EVENT_TRAVERSE:
                return new EventTraversalIterator(stateGraph, crawlerController);*/
            default:
                throw new IteratorTypeErrorException();
        }
    }
}
