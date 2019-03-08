package tw.edu.ntut.sdtlab.crawler.ace.util;

import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;

import java.io.Serializable;
import java.util.Calendar;

public class Printer implements Serializable{
    private String pre = "--  ";
    private String post = " --";

    public void start(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        System.out.println(this.pre + "start: " + String.valueOf(calendar.getTime()) + this.post);
        System.out.println(this.pre + "\t" + "packageName = " + Config.PACKAGE_NAME + this.post);
        System.out.println(this.pre + "\t" + "launcherActivity = " + Config.LAUNCHER_ACTIVITY + this.post);
    }

    public void restart(int restartCount) {
        System.out.println(this.pre + "restart : " + restartCount + this.post);
    }

    public void end() {
        System.out.println(this.pre + "crawling done" + this.post);
    }

    public void executeEvent(AndroidEvent event, int totalExecutedEventCount) {
        System.out.println(this.pre + "execute event : " + event.getReportLabel() + ", " + totalExecutedEventCount + this.post);
    }

    public void equivalentState(String className) {
        System.out.println(this.pre + "equivalent : " + className + this.post);
    }

    public void unrecognizableState() {
        System.out.println(this.pre + "unrecognizable state" + this.post);
    }

    public void progressBarTimeoutState() {
        System.out.println(this.pre + "progressBarTimeout state" + this.post);
    }

    public void atTheSameStateTimeout() {
        System.out.println(this.pre + "at the same state timeout -> restart" + this.post);
    }

    public void crossAppTimeout() {
        System.out.println(this.pre + "cross app state timeout -> restart" + this.post);
    }

    public void dfsBacktrackNondeterministicRetry() {
        System.out.println(this.pre + "DFS backtrack nondeterministic issue : retry" + this.post);
    }
}
