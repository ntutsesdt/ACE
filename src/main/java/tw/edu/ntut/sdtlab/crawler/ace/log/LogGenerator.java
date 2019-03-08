package tw.edu.ntut.sdtlab.crawler.ace.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import tw.edu.ntut.sdtlab.crawler.ace.crawler.AndroidCrawler;
import tw.edu.ntut.sdtlab.crawler.ace.util.ConfigReader;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.exception.NullPackageNameException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import tw.edu.ntut.sdtlab.crawler.ace.util.StopWatch;

public class LogGenerator {
    List<String> errorMessageList = null;
    protected String xmlReaderTimestamp = null;
    List<GUIState> guiStateList = null;
    String filePackageName = "";
    long executeTime = 0;
    int restartCount = 0;
    protected int totalFireEventCount = 0;
	protected int totalDistanceEquivalentStateCount = 0;
    protected int totalListGridEquivalentStateCount = 0;
    protected int totalEachChoiceEquivalentStateCount = 0;
    private int nafCount, nafStateCount, totalNafEquivalentCounter, totalCompareStateNotNAFButSelfNAFCounter;
    private int finalRootStateId;
    private int totalEquivalentRootStateRestartCount;
    private StopWatch dumpXMLStopWatch, pullXMLStopWatch, deleteXMLStopWatch, convertXMLToGUIStateStopWatch, updateStateGraphStopWatch, restartAppStopWatch, checkNonDeterministicEventStopWatch, executeEventStopWatch, buildReportStopWatch, getScreenShotStopWatch;
    private StopWatch executeOnStopWatch, turnOffSoftKeyboardStopWatch, executeCmdStopWatch;
    private String deviceModel, androidVersion;
    private int rootStateCount;
    private  GUIState desktopState;
    public LogGenerator(AndroidCrawler androidCrawler, StateGraph stateGraph) {
        this.xmlReaderTimestamp = androidCrawler.getStartTime();
        this.guiStateList = stateGraph.getAllStates();
        this.executeTime = System.currentTimeMillis() - Long.parseLong(this.xmlReaderTimestamp);
        this.errorMessageList = stateGraph.getLoggerStore().getErrorList();
        this.restartCount = androidCrawler.getRestartCount();
        this.totalDistanceEquivalentStateCount = stateGraph.getTotalDistanceEquivalentStateCount();
        this.totalFireEventCount = stateGraph.getTotalExecutedEventCount();
        this.totalListGridEquivalentStateCount = stateGraph.getTotalListGridEquivalentStateCount();
        this.totalEachChoiceEquivalentStateCount = stateGraph.getTotalEachChoiceEquivalentStateCount();
        this.filePackageName = getCreateFilePackageName();
        this.nafCount = androidCrawler.getNafCount();
        this.nafStateCount = androidCrawler.getNafStateCount();
        this.totalNafEquivalentCounter = this.getTotalNafEquivalentCounter(stateGraph);
        this.totalCompareStateNotNAFButSelfNAFCounter = this.getTotalCompareStateNotNAFButSelfNAFCounter(stateGraph);
        this.dumpXMLStopWatch = androidCrawler.getDumpXMLTimer();
        this.pullXMLStopWatch = androidCrawler.getPullXMLTimer();
        this.deleteXMLStopWatch = androidCrawler.getDeleteXMLTimer();
        this.convertXMLToGUIStateStopWatch = androidCrawler.getConvertXMLToGUIStateStopWatch();
        this.updateStateGraphStopWatch = androidCrawler.getUpdateStateGraphStopWatch();
        this.restartAppStopWatch = androidCrawler.getRestartAppStopWatch();
        this.checkNonDeterministicEventStopWatch = androidCrawler.getCheckNonDeterministicEventStopWatch();
        this.executeEventStopWatch = androidCrawler.getExecuteEventStopWatch();
        this.buildReportStopWatch = stateGraph.getBuildReportStopWatch();
        this.getScreenShotStopWatch = androidCrawler.getGetScreenShotTimer();
        this.executeOnStopWatch = androidCrawler.getExecuteOnStopWatch();
        this.turnOffSoftKeyboardStopWatch = androidCrawler.getTurnOffSoftKeyboardStopWatch();
        this.executeCmdStopWatch = androidCrawler.getExecuteCmdTimer();
        this.finalRootStateId = androidCrawler.getRootState().getId();
        this.totalEquivalentRootStateRestartCount = androidCrawler.getTotalEquivalentRootStateRestartCount();
        this.deviceModel = androidCrawler.getDevice().getDeviceModel();
        this.androidVersion = androidCrawler.getDevice().getAndroidVersion();
        this.rootStateCount = androidCrawler.getRootStateCount();
        this.desktopState=androidCrawler.getDesktopState();
    }


    public void generateLog() throws NullPackageNameException {
        File file = new File(Utility.getReportPath() + "/Log.txt");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        int stateCount = this.guiStateList.size();
        int distinctStateCount = stateCount;
        for (GUIState state: this.guiStateList) {
            distinctStateCount += state.getEquivalentStateCount();
        }
        int edgeCount = calculateTotalEdgeCount();
        int nonDeterministicEdge = calculateNondeterministicEvent();
        int totalEquivalentStateCount = calculateTotalEquivalentStateCount();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits( 1 );

            String packageName = Config.PACKAGE_NAME;
            String launchableActivityName = Config.LAUNCHER_ACTIVITY;

            bw.write("Package Name: " + packageName);
            bw.newLine();
            bw.write("Launch Activity: " + launchableActivityName);
            bw.newLine();
            bw.write("Device model: " + deviceModel);
            bw.newLine();
            bw.write("Android version: " + androidVersion);
            bw.newLine();
            bw.newLine();

            bw.write("Execution time: " + String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(executeTime),
                    TimeUnit.MILLISECONDS.toSeconds(executeTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(executeTime))
            ));
            bw.newLine();
            bw.write("State graph state count: " + stateCount);
            bw.newLine();
            bw.write("State graph event count: " + edgeCount);
            bw.newLine();
            bw.write("Restart count: " + restartCount);
            bw.newLine();
            bw.write("Root state count: " + this.rootStateCount);
            bw.newLine();
            bw.write("Final root state: state" + this.finalRootStateId);
            bw.newLine();
            bw.newLine();

            //State
            bw.write("Distinct state count: " + distinctStateCount);
            bw.newLine();
            bw.write("Equivalent state count: " + totalEquivalentStateCount);
            bw.newLine();
            bw.write("Distance equivalent state count: " + totalDistanceEquivalentStateCount);
            bw.newLine();
            bw.write("List&Grid equivalent state count: " + totalListGridEquivalentStateCount);
            bw.newLine();
            bw.write("Each choice equivalent state count: " + totalEachChoiceEquivalentStateCount);
            bw.newLine();
            bw.newLine();

            //Event
            bw.write("Fired event count: " + totalFireEventCount);
            bw.newLine();
            bw.write("Discarded event count: " + calculateTotalDiscardedEvent());
            bw.newLine();
            bw.write("Nondeterministic edge count: " + nonDeterministicEdge);
            bw.newLine();
            bw.newLine();

            bw.write("Equivalent-root-state restart count: " + this.totalEquivalentRootStateRestartCount);
            bw.newLine();
            bw.write("Crash count: " + calculateTotalCrashCount());
            bw.newLine();
            bw.write("Cross app state count: " + calculateTotalCrossApp());
            bw.newLine();
            bw.newLine();

            bw.write("NAF state count: " + this.nafStateCount);
            bw.newLine();
            bw.write("NAF count: " + this.nafCount);
            bw.newLine();
            bw.write("NAF equivalent node count: " + this.totalNafEquivalentCounter);
            bw.newLine();
            bw.write("NAF-element to non-NAF-element change count: " + this.totalCompareStateNotNAFButSelfNAFCounter);
            bw.newLine();
            bw.newLine();

            bw.write("Dump XML: total=" + nf.format((double) dumpXMLStopWatch.getAccumulatedTime()/1000) + "s, times=" + dumpXMLStopWatch.getNumber() + ", average=" + nf.format((double) dumpXMLStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) dumpXMLStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Pull XML: total=" + nf.format((double) pullXMLStopWatch.getAccumulatedTime()/1000) + "s, times=" + pullXMLStopWatch.getNumber() + ", average=" + nf.format((double) pullXMLStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) pullXMLStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Delete XML: total=" + nf.format((double) deleteXMLStopWatch.getAccumulatedTime()/1000) + "s, times=" + deleteXMLStopWatch.getNumber() + ", average=" + nf.format((double) deleteXMLStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) deleteXMLStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Convert XML to GUI state: total=" + nf.format((double) convertXMLToGUIStateStopWatch.getAccumulatedTime()/1000) + "s, times=" + convertXMLToGUIStateStopWatch.getNumber() + ", average=" + nf.format((double) convertXMLToGUIStateStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) convertXMLToGUIStateStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Update state graph: total=" + nf.format((double) updateStateGraphStopWatch.getAccumulatedTime()/1000) + "s, times=" + updateStateGraphStopWatch.getNumber() + ", average=" + nf.format((double) updateStateGraphStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) updateStateGraphStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Update state graph::Get screen shot: total=" + nf.format((double) getScreenShotStopWatch.getAccumulatedTime()/1000) + "s, times=" + getScreenShotStopWatch.getNumber() + ", average=" + nf.format((double) getScreenShotStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) getScreenShotStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Restart app: total=" + nf.format((double) restartAppStopWatch.getAccumulatedTime()/1000) + "s, times=" + restartAppStopWatch.getNumber() + ", average=" + nf.format((double) restartAppStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) restartAppStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Check non deterministic event: total=" + nf.format((double) checkNonDeterministicEventStopWatch.getAccumulatedTime()/1000) + "s, times=" + checkNonDeterministicEventStopWatch.getNumber() + ", average=" + nf.format((double) checkNonDeterministicEventStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) checkNonDeterministicEventStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Execute event: total=" + nf.format((double) executeEventStopWatch.getAccumulatedTime()/1000) + "s, times=" + executeEventStopWatch.getNumber() + ", average=" + nf.format((double) executeEventStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) executeEventStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Execute event::Execute On: total=" + nf.format((double) executeOnStopWatch.getAccumulatedTime()/1000) + "s, times=" + executeOnStopWatch.getNumber() + ", average=" + nf.format((double) executeOnStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) executeOnStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Execute event::Turn off soft keyboard: total=" + nf.format((double) turnOffSoftKeyboardStopWatch.getAccumulatedTime()/1000) + "s, times=" + turnOffSoftKeyboardStopWatch.getNumber() + ", average=" + nf.format((double) turnOffSoftKeyboardStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) turnOffSoftKeyboardStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("Build report: total=" + nf.format((double) buildReportStopWatch.getAccumulatedTime()/1000) + "s, times=" + buildReportStopWatch.getNumber() + ", average=" + nf.format((double) buildReportStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) buildReportStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            bw.write("ExecuteCmd: total=" + nf.format((double) executeCmdStopWatch.getAccumulatedTime()/1000) + "s, times=" + executeCmdStopWatch.getNumber() + ", average=" + nf.format((double) executeCmdStopWatch.getAverageTime()/1000) + "s, percentage=" + nf.format((double) executeCmdStopWatch.getAccumulatedTime()*100/executeTime) + "%");
            bw.newLine();
            double totalExecuteTime = dumpXMLStopWatch.getAccumulatedTime() + pullXMLStopWatch.getAccumulatedTime() + deleteXMLStopWatch.getAccumulatedTime() + convertXMLToGUIStateStopWatch.getAccumulatedTime() + updateStateGraphStopWatch.getAccumulatedTime() + restartAppStopWatch.getAccumulatedTime() + checkNonDeterministicEventStopWatch.getAccumulatedTime() + executeEventStopWatch.getAccumulatedTime() + buildReportStopWatch.getAccumulatedTime(); //updateStateGraph time include getScreenShot time
            bw.write("Sum: total=" + nf.format(totalExecuteTime/1000) + "s, percentage=" + nf.format(totalExecuteTime*100/executeTime) + "%");
            bw.newLine();
            bw.newLine();

            this.logConfig(bw);

            bw.newLine();
            bw.write("*************************Exception****************************");
            bw.newLine();
            for (int i = 0; i < errorMessageList.size(); i++) {
                bw.write(errorMessageList.get(i));
                bw.newLine();
            }
            bw.write("**************************************************************");
            bw.newLine();

            bw.newLine();
            bw.write("State: ");
            bw.newLine();
            for (GUIState s : guiStateList) {
                bw.write("state" + s.getId() + ":");
                bw.newLine();
                for (AndroidEvent e : s.getEvents()) {
                    bw.write("event" + e.getReportLabel());
                    bw.write("-->");
                    if (e.getToState() != null)
                        bw.write("state" + e.getToState().getId());
                    else
                        bw.write("null");

                    bw.newLine();
                }
            }
            bw.flush();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void logConfig(BufferedWriter bw) throws IOException {
        List<String> ignoreConfig = new ArrayList<>();
        ignoreConfig.add("adb");
        ignoreConfig.add("graphvizLayout");
        ignoreConfig.add("monkeyRunner");
        ignoreConfig.add("crashMessage");
        ignoreConfig.add("packageName");
        ignoreConfig.add("launchableActivity");
        ignoreConfig.add("KillAppKeyCode_HTCOne4.4");
        ignoreConfig.add("KillAppKeyCode_HTCOne6.0");


        bw.newLine();
        bw.write("*************************Crawler Configuration****************************");
        bw.newLine();
        Map<String, String> configMap = ConfigReader.getConfigMap();
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            if (ignoreConfig.contains(entry.getKey()))
                continue;
            if (entry.getValue().equals("\n")) {// is entry a category element
                bw.newLine();
                bw.write("<" + entry.getKey() + ">");
            }else
                bw.write(entry.getKey() + ": " + entry.getValue());
            bw.newLine();
        }

        bw.write("**************************************************************************");
        bw.newLine();
    }

    protected int calculateTotalEdgeCount() {
        int edgeCount = 0;
        for (GUIState s : guiStateList) {
            for (AndroidEvent e : s.getEvents()) {
                if (e.getToState() != null)
                    if (!e.isNonDeterministic())
                        edgeCount++;
            }
        }
        return edgeCount;
    }

    protected int calculateNondeterministicEvent() {
        int edgeCount = 0;
        for (GUIState s : guiStateList) {
            for (AndroidEvent e : s.getEvents()) {
                if (e.getToState() != null)
                    if (e.isNonDeterministic())
                        edgeCount++;
            }
        }
        return edgeCount;
    }

    protected int calculateTotalEquivalentStateCount() {
        int totalCount = 0;
        for (GUIState s : guiStateList) {
            if (s.isEquivalentState())
                totalCount++;
        }
        return totalCount;
    }

    protected int calculateTotalDiscardedEvent() {
        int totalDiscarded = 0;
        int threshold = Config.NONDETERMINISTIC_EVENT_ATTEMPT_COUNT_THRESHOLD;
        for (GUIState s : guiStateList) {
            for (AndroidEvent e : s.getEvents()) {
                if (e.getNondeterministicEventAttemptCount() >= threshold)
                    totalDiscarded++;
            }
        }
        return totalDiscarded;
    }

    protected int calculateTotalCrashCount() {
        int totalCrash = 0;
        for (GUIState s : guiStateList) {
            for (AndroidEvent e : s.getEvents()) {
                if (e.getToState() != null && e.getToState().isCrashState())
                    totalCrash++;
            }
        }
        return totalCrash;
    }

    protected int calculateTotalCrossApp() throws NullPackageNameException {
        int totalCrossAppStateCount = 0;
        String packageName = Config.PACKAGE_NAME;
        for (GUIState s : guiStateList) {
            if (!s.getPackageName().equals(packageName) && !s.getPackageName().contains(desktopState.getPackageName())) {
                totalCrossAppStateCount++;
            }
        }
        return totalCrossAppStateCount;
    }

    protected String getCreateFilePackageName() {
        String[] name = Config.PACKAGE_NAME.split("\\.");
        return name[name.length - 1];
    }

    public int getTotalNafEquivalentCounter(StateGraph stateGraph) {
        int count = 0;
        for (GUIState state : stateGraph.getAllStates()) {
            count += state.getNafEquivalentCounter();
        }
        return count;
    }

    public int getTotalCompareStateNotNAFButSelfNAFCounter(StateGraph stateGraph) {
        int count = 0;
        for (GUIState state : stateGraph.getAllStates()) {
            count += state.getCompareStateNotNAFButSelfNAFCounter();
        }
        return count;
    }
}
