package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import org.dom4j.DocumentException;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;

import java.io.IOException;
import java.util.List;

public interface Iterator {
    void first() throws IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException, EventFromStateErrorException, CannotReachTargetStateException;
    boolean isDone();
    void  next() throws IndexOutOfBoundsException, IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, EventFromStateErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException;
    List<AndroidEvent> getEventSequence();
}
