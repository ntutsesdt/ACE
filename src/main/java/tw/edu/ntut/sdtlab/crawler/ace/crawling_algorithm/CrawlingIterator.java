package tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;
public abstract class CrawlingIterator implements Iterator {
    @Override
    public abstract void first() throws IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException, EventFromStateErrorException, CannotReachTargetStateException;

    @Override
    public abstract boolean isDone();

    @Override
    public abstract void next() throws IndexOutOfBoundsException, IOException, InterruptedException, CrawlerControllerInitialErrorException, DocumentException, ExecuteCommandErrorException, ClickTypeErrorException, EventFromStateErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException;

    @Override
    public abstract List<AndroidEvent> getEventSequence();
}
