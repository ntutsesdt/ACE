package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;


import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ClickTypeErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.IteratorTypeErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;
import tw.edu.ntut.sdtlab.crawler.ace.exception.NullPackageNameException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;

import static org.junit.Assert.*;

import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class iteratorFactoryTest {
    private  StateGraph stateGraph;
    private Device device;
    private AndroidCrawler androidCrawler;
    private String deviceSerialNum;

    @Before
    public void setup() throws InterruptedException, ExecuteCommandErrorException, ClickTypeErrorException, DocumentException, IOException, NullPackageNameException, MultipleListOrGridException {
        Config config = new Config();
        this.stateGraph = new StateGraph();
        this.deviceSerialNum  = config.getDeviceSerialNum();
        this.device = new Device(this.deviceSerialNum);
        this.androidCrawler = new AndroidCrawler(device, stateGraph);
    }

    @Test
    public void testCreateIterator() throws IteratorTypeErrorException {
        Iterator expected = new NFSIterator(stateGraph,androidCrawler);
        IteratorFactory iteratorFactory = new IteratorFactory();
        Iterator iterator = iteratorFactory.createIterator(IteratorFactory.NFS, stateGraph, androidCrawler);
        assertNotNull(iterator);
        assertTrue(iterator instanceof NFSIterator);
    }

    @After
    public void teardown() throws NoSuchFieldException, IllegalAccessException {
        File directory = new File(Utility.getReportPath());
        if(directory.isDirectory())
            deleteFilesInDirectory(directory);
        directory.delete();
    }

    private void deleteFilesInDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                this.deleteFilesInDirectory(file);
            file.delete();
        }
    }
}
