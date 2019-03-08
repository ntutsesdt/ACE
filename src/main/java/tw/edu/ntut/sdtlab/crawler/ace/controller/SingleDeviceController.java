package tw.edu.ntut.sdtlab.crawler.ace.controller;

import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.equivalent.EquivalentStateStrategy;
import tw.edu.ntut.sdtlab.crawler.ace.equivalent.EquivalentStateStrategyFactory;
import tw.edu.ntut.sdtlab.crawler.ace.log.LogGenerator;
import tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm.Iterator;
import tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm.IteratorFactory;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.DocumentException;
import tw.edu.ntut.sdtlab.crawler.ace.util.EnvironmentChecker;

import java.io.*;


public class SingleDeviceController {
    private AndroidCrawler mainCrawler;
    private StateGraph stateGraph;

    public void execute() throws InterruptedException, ExecuteCommandErrorException, ClickTypeErrorException, DocumentException, IOException, IteratorTypeErrorException, EventFromStateErrorException, CrawlerControllerInitialErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ClassNotFoundException, ProgressBarTimeoutException {
        new EnvironmentChecker().checkEnviroment();
        this.createStateGraph();
        this.createAndroidCrawler();
        IteratorFactory iteratorFactory = new IteratorFactory();
        Iterator iterator = null;
        final String algorithm = Config.CRAWLING_ALGORITHM.toLowerCase();
        if (algorithm.equals("nfs"))
            iterator = iteratorFactory.createIterator(IteratorFactory.NFS, stateGraph, mainCrawler);
        else if (algorithm.equals("dfs"))
            iterator = iteratorFactory.createIterator(IteratorFactory.DFS, stateGraph, mainCrawler);
        else if (algorithm.equals("random_walk"))
            iterator = iteratorFactory.createIterator(IteratorFactory.RANDOM_WALK, stateGraph, mainCrawler);
        this.mainCrawler.exploreAllStates(iterator);
        this.stateGraph.buildReport();
        LogGenerator logGenerator = new LogGenerator(this.mainCrawler, stateGraph);
        logGenerator.generateLog();
    }

    private void createAndroidCrawler() throws IOException, ClassNotFoundException, InterruptedException, ClickTypeErrorException, MultipleListOrGridException, NullPackageNameException, DocumentException, ExecuteCommandErrorException {
        Device device = new Device(Config.DEVICE_SERIAL_NUMBER);
        this.mainCrawler = new AndroidCrawler(device, this.stateGraph);
        EquivalentStateStrategy equivalentStateStrategy = new EquivalentStateStrategyFactory().createStrategy();
        this.stateGraph.setEquivalentStragegy(equivalentStateStrategy);
    }

    private void createStateGraph() throws IOException, ClassNotFoundException, DocumentException {
        this.stateGraph = new StateGraph();
        EquivalentStateStrategy equivalentStateStrategy = new EquivalentStateStrategyFactory().createStrategy();
        this.stateGraph.setEquivalentStragegy(equivalentStateStrategy);
    }
}
