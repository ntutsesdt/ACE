package tw.edu.ntut.sdtlab.crawler.ace.util;

import java.io.Serializable;

/**
 * Created by Angel on 2017/8/10.
 */
public class StopWatch implements Serializable {
    int number;
    long startTime, stopTime, accumulatedTime;

    public StopWatch() {
        number = 0;
        startTime = 0;
        stopTime = 0;
        accumulatedTime = 0;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        stopTime = System.currentTimeMillis();
        number++;
        accumulatedTime += (stopTime - startTime);
    }

    public int getNumber() {
        return number;
    }

    public long getAccumulatedTime() {
        return accumulatedTime;
    }

    public long getAverageTime() {
        if (number == 0)
            return 0;
        return accumulatedTime / number;
    }
}
