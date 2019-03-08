package tw.edu.ntut.sdtlab.crawler.ace.crawler;


import tw.edu.ntut.sdtlab.crawler.ace.exception.*;
import tw.edu.ntut.sdtlab.crawler.ace.util.CommandHelper;
import tw.edu.ntut.sdtlab.crawler.ace.util.TimeHelper;
import tw.edu.ntut.sdtlab.crawler.ace.event.BackKeyEvent;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import tw.edu.ntut.sdtlab.crawler.ace.util.XMLEventParser;
import tw.edu.ntut.sdtlab.crawler.ace.util.StopWatch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class Device implements Serializable{
    private final String ADB = Config.ADB_PATH;
    private String serialNum = null;
    private String deviceModel = null;
    private String androidVersion = null;
    private int coverageCounter = 0;
    private long progressBarRecordTime = 0;
    private StopWatch dumpXMLStopWatch, pullXMLStopWatch, deleteXMLStopWatch, getScreenShotStopWatch, executeCmdStopWatch;

    public Device(String serialNum) {
        this.serialNum = serialNum;
        this.dumpXMLStopWatch = new StopWatch();
        this.pullXMLStopWatch = new StopWatch();
        this.deleteXMLStopWatch = new StopWatch();
        this.getScreenShotStopWatch = new StopWatch();
        this.executeCmdStopWatch = new StopWatch();
    }

    // execute ADB command with serialNum
    public List<String> executeADBCommand(String[] cmd) throws InterruptedException, ExecuteCommandErrorException, IOException {
        final int DEFAULT_COMMAND_LENGTH = 3;
        String[] command = new String[cmd.length + DEFAULT_COMMAND_LENGTH];
        command[0] = ADB;
        command[1] = "-s";
        command[2] = this.serialNum;
        int i = DEFAULT_COMMAND_LENGTH;
        for (String str : cmd) {
            command[i] = str;
            i++;
        }
        executeCmdStopWatch.start();
        List<String> cmdOutput = CommandHelper.executeCmd(command);
        executeCmdStopWatch.stop();
        return cmdOutput;
    }

    public void resetDevice() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] searchCommand = {"shell", "input", "keyevent", "KEYCODE_SEARCH"};
        String[] homeCommand = {"shell", "input", "keyevent", "KEYCODE_HOME"};
        this.executeADBCommand(searchCommand);
        this.executeADBCommand(homeCommand);
    }

    public void pressHome() throws IOException, InterruptedException, ExecuteCommandErrorException {
        String[] command = {"shell", "input", "keyevent", "KEYCODE_HOME"};
        // presshome on watch may may theater mode, so check is home before press home
        if (!this.getActivityName().contains("HomeActivity")) {
            List<String> result = this.executeADBCommand(command);
            assertTrue(result.isEmpty());
        }
    }

    public void startApp(String packageName, String activityName) throws IOException, InterruptedException, ExecuteCommandErrorException {
        final long START_APP_SLEEP_TIME = TimeHelper.getWaitingTime("startAppSleepTimeSecond");
        //Due to return null when launch instrument activity,
        //the asserts executed only in normal Activity(non-instrumented App).
        if (!Config.APP_INSTRUMENTED) {
            String[] startCmd = {"shell", "am", "start", "-n",
                    packageName + "/" + activityName};
            List<String> result = this.executeADBCommand(startCmd);
            assertTrue(result.get(0).contains("Starting: Intent "));
            assertTrue(result.get(0).contains(packageName));
        } else {
            String[] initInstrActivityCmd = {"adb", "-s", this.serialNum, "shell", "am", "instrument", "-w", "-e", "coverage", "true", packageName + ".test/android.support.test.runner.AndroidJUnitRunner"};
            ProcessBuilder proc = new ProcessBuilder(initInstrActivityCmd);
            proc.start();

            TimeHelper.sleep(Config.START_APP_SLEEP_TIMESECOND*1000);
        }
        TimeHelper.sleep(START_APP_SLEEP_TIME);
    }

    public void stopApp(String packageName) throws IOException, InterruptedException, ExecuteCommandErrorException {
        List<String> result;
        if (Config.DEV_KILL_APP_KEYCODE.equals("false")) {
            String[] stopCmd = {"shell", "am", "force-stop", packageName};
            result = this.executeADBCommand(stopCmd);
        }
        else
            result = specifyDeleteCommand();
        assertTrue(result.isEmpty());
        TimeHelper.sleep(Config.CLOSE_APP_SLEEP_TIMESECOND);
    }

    public void clearAppData(String packageName) throws IOException, InterruptedException, ExecuteCommandErrorException {
        String[] command = {"shell", "pm", "clear", packageName};
        List<String> result = this.executeADBCommand(command);
        assertTrue("result.get(0) = " + result.get(0) + ", maybe do not install the app", result.get(0).equals("Success"));
    }

    public void dumpXML(final String fileName) throws InterruptedException, ExecuteCommandErrorException, IOException, UnrecognizableStateException, DocumentException, ProgressBarTimeoutException {
        dumpXMLStopWatch.start();
        String[] dumpUIXMLCmd = {"shell",
                "uiautomator", "dump",
                "/data/local/tmp/temp.xml"};
        String[] removeUIXMLInDeviceCmd = {"shell",
                "rm", "/data/local/tmp/temp.xml"};
        List<String> result = this.executeADBCommand(dumpUIXMLCmd);
        dumpXMLStopWatch.stop();
        if (!result.isEmpty() && result.contains("ERROR: could not get idle state."))
            throw new UnrecognizableStateException();

        pullXMLStopWatch.start();
        try {
            this.pullXML(fileName);
        }
        catch (Exception e)
        {
            throw new UnrecognizableStateException();
        }
        pullXMLStopWatch.stop();

        deleteXMLStopWatch.start();
        this.executeADBCommand(removeUIXMLInDeviceCmd);
        deleteXMLStopWatch.stop();
    }

    private void pullXML(final String fileName) throws InterruptedException, ExecuteCommandErrorException, IOException, DocumentException, UnrecognizableStateException, ProgressBarTimeoutException {
        String[] pullUIXMLCmd = {"pull",
                "/data/local/tmp/temp.xml",
                fileName};
        this.executeADBCommand(pullUIXMLCmd);
        if (Config.WAIT_FOR_PROGRESS_BAR) {
            Document document = new SAXReader().read(fileName);
            XMLEventParser xmlEventParser = new XMLEventParser(document);
            if (xmlEventParser.containClass("android.widget.ProgressBar")) {
                if (this.progressBarRecordTime == 0)
                    this.progressBarRecordTime = System.currentTimeMillis();
                else if (System.currentTimeMillis() - this.progressBarRecordTime > Config.WAIT_FOR_PROGRESS_BAR_TIMESECOND * 1000)
                    throw new ProgressBarTimeoutException();
                Thread.sleep(1000);
                this.dumpXML(fileName);
            } else
                this.progressBarRecordTime = 0;
        }
    }

    // activity name example :
    // mFocusedApp=AppWindowToken{426eee40 token=Token{426ebb88 ActivityRecord{426eb928 u0 com.htc.launcher/.Launcher}}}
    public  String getActivityName() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] command = {"shell", "dumpsys", "activity", "activities", "|", "grep",
                "\"Run\\ #\""};
        List<String> cmdResult = this.executeADBCommand(command);
//        if (!feedBack.contains("/"))
//            return feedBack;
        String result = cmdResult.get(0);
        String activityName = result.substring(result.indexOf("/") + 1, result.indexOf("}"));
        String[] str = activityName.split(" ");
        return str[0].replace(".", "");
    }

    public void getScreenShot(String imgFileName) throws InterruptedException, ExecuteCommandErrorException, IOException {
        getScreenShotStopWatch.start();
        String[] screenshotCmd = {"shell",
                "screencap", "-p", "/data/local/tmp/temp.png"},
                downloadScreenshotCmd = {"pull",
                        "/data/local/tmp/temp.png",
                        imgFileName},
                removeScreenshotInDevice = {"shell",
                        "rm", "/data/local/tmp/temp.png"};
        this.executeADBCommand(screenshotCmd);
        this.executeADBCommand(downloadScreenshotCmd);
        this.executeADBCommand(removeScreenshotInDevice);
        getScreenShotStopWatch.stop();
    }

    public void turnOffSoftKeyboard() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] command = {"shell", "dumpsys", "input_method", "|", "grep", "\"mInputShown=true\""};
        if (this.executeADBCommand(command).size() > 0) { // when keyboard is show
            (new BackKeyEvent()).executeOn(this);
        }
    }

    private List<String> specifyDeleteCommand() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] splitkillAppCmd = Config.KILL_APP_KEYCODE.split(", ");
        int cntKeyCode = 0;
        List<String> executeResult = null;
        while (cntKeyCode < splitkillAppCmd.length) {
            String[] deleteAppCmd = {"shell", "input", "keyevent", splitkillAppCmd[cntKeyCode]};
            executeResult = this.executeADBCommand(deleteAppCmd);
            cntKeyCode++;
        }
        return executeResult;
    }

    public void sendStopTestBroadcast() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] stopTestBroadcastCmd = {"shell", "am", "broadcast", "-a", "\"test\""}; // test is self define in Android
        this.executeADBCommand(stopTestBroadcastCmd);
    }

    public void pullCoverageReport() throws InterruptedException, ExecuteCommandErrorException, IOException {
        final String COVERAGE_PATH = System.getProperty("user.dir") + "/" + Utility.getReportPath() + "/CodeCoverage";
        new File(COVERAGE_PATH).mkdirs();
        File coverageFile = new File(COVERAGE_PATH + "/coverage" + this.coverageCounter + ".ec");
        String[] pullToLocalCmd = {ADB, "-s", this.serialNum, "exec-out", "run-as", Config.PACKAGE_NAME, "cat", "/data/data/" + Config.PACKAGE_NAME + "/files/coverage.ec"};
        ProcessBuilder processBuilder = new ProcessBuilder(pullToLocalCmd);
        processBuilder.redirectOutput(coverageFile);
        Process process = processBuilder.start();
        process.waitFor();
        this.coverageCounter += 1;
    }

    public void setDeviceModel() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] getBrandCmd = {"shell", "getprop", "ro.product.brand"};
        List<String> getBrandCmdResult = this.executeADBCommand(getBrandCmd);
        String brand = getBrandCmdResult.get(0);

        String[] getModelCmd = {"shell", "getprop", "ro.product.model"};
        List<String> getModelCmdResult = this.executeADBCommand(getModelCmd);
        String model = getModelCmdResult.get(0);
        this.deviceModel = "[" + brand + "] " + model;
    }

    public void setAndroidVersion() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] command = {"shell", "getprop", "ro.build.version.release"};
        List<String> cmdResult = this.executeADBCommand(command);
        String version = cmdResult.get(0);
        this.androidVersion = version;
    }

    public void setPermission(String  permission , Boolean enablePremission) throws InterruptedException, ExecuteCommandErrorException, IOException {
        if (enablePremission) {
            String perimessionCommand[]=new String[]{"shell", "pm", "grant", Config.PACKAGE_NAME,permission};
            List<String> A =this.executeADBCommand(perimessionCommand);
        }
    }

    public String getSerialNum(){return serialNum ;}
    public String getDeviceModel() {
        return deviceModel;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public StopWatch getDumpXMLStopWatch() {
        return dumpXMLStopWatch;
    }

    public StopWatch getPullXMLStopWatch() {
        return pullXMLStopWatch;
    }

    public StopWatch getDeleteXMLStopWatch() {
        return deleteXMLStopWatch;
    }

    public StopWatch getGetScreenShotStopWatch() {
        return getScreenShotStopWatch;
    }

    public StopWatch getExecuteCmdStopWatch() {
        return executeCmdStopWatch;
    }
}
