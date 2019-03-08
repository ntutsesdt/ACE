package tw.edu.ntut.sdtlab.crawler.ace.crawler;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.util.Timeout;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.crawling_algorithm.Iterator;
import tw.edu.ntut.sdtlab.crawler.ace.log.LoggerStore;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Printer;
import tw.edu.ntut.sdtlab.crawler.ace.util.StopWatch;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.BackKeyEvent;
import tw.edu.ntut.sdtlab.crawler.ace.event.EventOrder;
import tw.edu.ntut.sdtlab.crawler.ace.event.EventOrderFactory;
import tw.edu.ntut.sdtlab.crawler.ace.exception.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.io.IOException;
import java.io.File;

public class AndroidCrawler implements Serializable {
    private GUIState currentState = null;
    private GUIState crashState = null;
    private GUIState desktopState = null;
    private GUIState rootState = null;
    private GUIState unrecognizableState = null;
    private GUIState progressBarTimeoutState = null;
    private Device device = null;
    private transient StateGraph stateGraph = null;
    private int restartCount = 0;
    private int guiIndex = 0;
    private long startTime = 0;
    private boolean firstTimeRestart = true;
    private Printer printer;
    private LoggerStore loggerStore;
    private int eventOrder = 1;
    private int nafStateCount = 0, nafCount = 0;
    private StopWatch convertXMLToGUIStateStopWatch, updateStateGraphStopWatch, restartAppStopWatch, checkNonDeterministicEventStopWatch, executeEventStopWatch;
    private StopWatch executeOnStopWatch, turnOffSoftKeyboardStopWatch;
    private int equivalentRootStateRestartAttemptCount = 0, totalEquivalentRootStateRestartCount = 0, rootStateCount = 0;
    private enum StateType {UNRECOGNIZABLE_STATE, PROGRESS_BAR_TIMEOUT_EXCEPTION}

    public AndroidCrawler(Device device, StateGraph stateGraph) throws InterruptedException, ExecuteCommandErrorException, IOException {
        this.device = device;
        this.device.setDeviceModel();
        this.device.setAndroidVersion();
        this.loggerStore = new LoggerStore();
        this.stateGraph = stateGraph;
        this.stateGraph.setLoggerStore(this.loggerStore);
        this.restartCount = 0;
        this.guiIndex = 0;
        this.printer = new Printer();
        this.convertXMLToGUIStateStopWatch = new StopWatch();
        this.updateStateGraphStopWatch = new StopWatch();
        this.restartAppStopWatch = new StopWatch();
        this.checkNonDeterministicEventStopWatch = new StopWatch();
        this.executeEventStopWatch = new StopWatch();
        this.executeOnStopWatch = new StopWatch();
        this.turnOffSoftKeyboardStopWatch = new StopWatch();
    }

    public void restartApp() throws InterruptedException, ExecuteCommandErrorException, IOException, ClickTypeErrorException, DocumentException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        final String PACKAGE_NAME = Config.PACKAGE_NAME;
        final String ACTIVITY_NAME = Config.LAUNCHER_ACTIVITY;
        // restart app: 由於restart的時候有可能進入另一個root或root的equivalent state
        // 所以需要特別處理。當出現root的equivalent state時，需要迴圈重新restart。
        while (true) {
            if (!this.firstTimeRestart && Config.APP_INSTRUMENTED) {
                this.device.sendStopTestBroadcast();
                this.device.pullCoverageReport();
            }

            restartAppStopWatch.start();
            this.device.stopApp(PACKAGE_NAME);
            this.device.clearAppData(PACKAGE_NAME);
            new BackKeyEvent().executeOn(this.device);
            this.device.pressHome();
            device.setPermission("android.permission.WRITE_EXTERNAL_STORAGE",Config.ENABLE_PERMISSION_WRITE_EXTERNAL_STORAGE);
            device.setPermission("android.permission.READ_EXTERNAL_STORAGE",Config.ENABLE_PERMISSION_READ_EXTERNAL_STORAGE);
            this.device.startApp(PACKAGE_NAME, ACTIVITY_NAME);
            restartAppStopWatch.stop();

            if (!firstTimeRestart) {
                this.restartCount++;
                this.printer.restart(restartCount);
            }

            GUIState newState = this.dumpCurrentState();
            this.updateStateGraph(newState);

            // 設定root state
            if (rootState == null) { // 如果rootState==null，則設定rootState
                rootState = currentState;
                rootStateCount++;
                break;
            }

            // restart之後，有可能出現新的rootState與舊的rootState不同，若確實不同則更換rootState
            boolean isRootChanged = newState.isExactlyEquivalentTo(rootState);
            boolean isRootAnEquivalentState = newState.isEquivalentState();
            if (isRootChanged && !isRootAnEquivalentState) { // 沒有換root，而且root不是equivalent state，就不用重複restart
                totalEquivalentRootStateRestartCount += equivalentRootStateRestartAttemptCount;
                equivalentRootStateRestartAttemptCount = 0;
                break; // currentState = 狀態圖裡面的root
            }

            rootState = newState; //換新的root，更新rootState
            if (!isRootAnEquivalentState) { // 有換root，且不是equivalent的root，就不用重複restart
                totalEquivalentRootStateRestartCount += equivalentRootStateRestartAttemptCount;
                equivalentRootStateRestartAttemptCount = 0;
                if (newState == currentState)
                    rootStateCount++;
                break;
            }
            // 不管有沒有換root，只要新的root是equivalent state，則restart，試試看能不能換root
            equivalentRootStateRestartAttemptCount++;
            if (equivalentRootStateRestartAttemptCount >= Config.EQUIVALENT_ROOTSTATE_RESTART_ATTEMPT_COUNT) {
                totalEquivalentRootStateRestartCount += equivalentRootStateRestartAttemptCount;
                // 清除newState的events，使得跳出while迴圈後，爬行會自動終止 // 換currentState為newState
                newState.clearEvents();
                currentState = newState;
                break; // 如果重複restart超過設定次數，則終止爬行 // currentState = equivalent state = 狀態圖裡面的root
            }
            // 回while loop繼續restart
        }

        this.firstTimeRestart = false;
    }

    public void executeEvent(AndroidEvent event) throws ExecuteCommandErrorException, InterruptedException, IOException, EventFromStateErrorException {
        executeEventStopWatch.start();
        if (event.getFromState() != null && event.getFromState() != this.getCurrentState())
            throw new EventFromStateErrorException();
        event.setFromState(this.getCurrentState());
        this.stateGraph.increaseTotalExecutedEventCount();
        this.printer.executeEvent(event, this.stateGraph.getTotalExecutedEventCount());
        executeOnStopWatch.start();
        event.executeOn(this.device);
        executeOnStopWatch.stop();
        turnOffSoftKeyboardStopWatch.start();
        this.device.turnOffSoftKeyboard();
        turnOffSoftKeyboardStopWatch.stop();
        executeEventStopWatch.stop();
    }

    public void executeEvents(List<AndroidEvent> events) throws ExecuteCommandErrorException, EventFromStateErrorException, InterruptedException, IOException, ClickTypeErrorException, DocumentException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        for (AndroidEvent event : events) {
            // TODO : check currentState exist any input data, if exist, input
            this.executeEvent(event);
            GUIState newState = this.dumpCurrentState();

            this.updateStateGraph(newState);
            // 判斷是否為 Equivalent State
            if (newState.isEquivalentState() && !newState.isUnrecognizableState()) {
//                assert(newState.getId() == guiIndex-1);
                event.setToStatePictureId(this.guiIndex - 1);  // mark real "toStateId" to realize whether this event is to original state // 設定該event指向哪個state，因為在updateStateGraph中有將guiIndex加1，所以在這邊需減1才是真正的state
                try {
                    this.updateEventAndCheckNonDeterministicEvent(newState, event); // check if event is nondeterministic event
                } catch (CannotReachTargetStateException e1) {
                    this.restartApp();
                    throw e1;
                }
                // 當遇到EquivalentState時，若stateB equivalent stateA，則stateB不要繼續往下爬，
                //   而是restart，讓stateA有機會爬完，放棄stateB
                this.restartApp();
                break;
            }
            event.setToStatePictureId(this.currentState.getId());
            this.updateEventAndCheckNonDeterministicEvent(this.currentState, event); // check if event is nondeterministic event
        }
    }

    public void startExplore() throws InterruptedException, ExecuteCommandErrorException, IOException, ClickTypeErrorException, DocumentException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
        this.startTime = System.currentTimeMillis();
        this.printer.start(this.startTime);
        new File(System.getProperty("user.dir") + "/" + this.getStatePath()).mkdirs();
        this.device.resetDevice();    // packageName will null after force-stop app, need pressHome to refresh device
        updateStateGraph(this.dumpCurrentState());
        this.desktopState = this.currentState;
        this.desktopState.clearEvents();
        this.restartApp();
    }

    public void exploreAllStates(Iterator iterator) throws InterruptedException, ExecuteCommandErrorException, CrawlerControllerInitialErrorException, DocumentException, IOException, ClickTypeErrorException, EventFromStateErrorException, CannotReachTargetStateException, NullPackageNameException, MultipleListOrGridException, EquivalentStateException, ProgressBarTimeoutException {
//        this.startExplore();
        for (iterator.first(); !iterator.isDone(); iterator.next()) {
            // TODO : state size out, event size out (implement TerminateCondition class if there are a lot of terminate condition)
            long executeTime = System.currentTimeMillis() - this.startTime;
            Timeout timeout = new Timeout();
            if (timeout.check(executeTime)) {
                this.restartApp();  // restart to serialization and pull coverage report if instrument
                break;
            }

            List<AndroidEvent> eventSequence = iterator.getEventSequence();
            try {
                this.executeEvents(eventSequence);
            } catch (CannotReachTargetStateException e) {
                e.getNondeterministicEvent().increaseNondeterministicEventAttemptCount();
            }
        }
        this.printer.end();
    }

    private GUIState dumpCurrentState() throws InterruptedException, ExecuteCommandErrorException, IOException, ClickTypeErrorException, DocumentException, NullPackageNameException {
        final String FILE_PATH = this.getStatePath() + "/" + this.guiIndex + ".xml";

        try {
            this.device.dumpXML(FILE_PATH);
        } catch (UnrecognizableStateException e) {
            return this.createState(StateType.UNRECOGNIZABLE_STATE, FILE_PATH);
        } catch (ProgressBarTimeoutException e) {
            return this.createState(StateType.PROGRESS_BAR_TIMEOUT_EXCEPTION, FILE_PATH);
        }
        return this.createState(null, FILE_PATH);
    }

    private GUIState createState(StateType stateType, final String FILE_PATH) throws InterruptedException, ExecuteCommandErrorException, IOException, DocumentException, ClickTypeErrorException, NullPackageNameException {
        if (stateType == StateType.UNRECOGNIZABLE_STATE || stateType == StateType.PROGRESS_BAR_TIMEOUT_EXCEPTION) {
            GUIState state = stateType == StateType.UNRECOGNIZABLE_STATE ? this.unrecognizableState : this.progressBarTimeoutState;
            if (state == null) {
                state = new GUIState(DocumentHelper.parseText("<test></test>"), new ArrayList<AndroidEvent>());
                if (stateType == StateType.UNRECOGNIZABLE_STATE)
                    state.setUnrecognizableState(true);
                else if (stateType == StateType.PROGRESS_BAR_TIMEOUT_EXCEPTION)
                    state.setProgressBarTimeoutState(true);
                state.setId(this.guiIndex);
            } else {
                state.setIsEquivalentState(true);
                state.increaseEquivalentStateCount();
            }
            state.addImage(this.guiIndex + ".png");
            if (stateType == StateType.UNRECOGNIZABLE_STATE)
                this.printer.unrecognizableState();
            else if (stateType == StateType.PROGRESS_BAR_TIMEOUT_EXCEPTION)
                this.printer.progressBarTimeoutState();
            return state;
        } else {
            GUIState state = null;
            final String ACTIVITY_NAME = this.device.getActivityName();
            EventOrder eventOrder = new EventOrderFactory().createEventOrder();
            convertXMLToGUIStateStopWatch.start();
            state = GUIState.convertDeviceXMLToGUIState(FILE_PATH, eventOrder, ACTIVITY_NAME);
            if (desktopState != null && !state.getBounds().equals(desktopState.getBounds())) // 判斷是不是popup
                state.removeSwipeEvents(); // 如果是popup，就不做swipe。在此移除swipe是為了避免變更eventOrder
            convertXMLToGUIStateStopWatch.stop();
            state.setId(this.guiIndex);
            state.addImage(this.guiIndex + ".png");
            return state;
        }
    }

    public void updateStateGraph(GUIState newState) throws InterruptedException, ExecuteCommandErrorException, IOException, NullPackageNameException, MultipleListOrGridException {
        // update state
        // TODO : maybe crossApp state (loss)
        final String PNG_PATH = this.getStatePath() + "/" + this.guiIndex + ".png";
        updateStateGraphStopWatch.start();
        this.updateNafCount(newState);
        if (this.desktopState != null && this.isDesktopState(newState)) {  // desktop state
            this.currentState = this.desktopState;
        } else if (newState.isCrashState()) {                   // crash state
            if (this.crashState == null) { // first crash state
                this.device.getScreenShot(PNG_PATH);
                this.crashState = newState;
                if (!crashState.getBounds().equals(desktopState.getBounds()))
                        markBounds(crashState.getBounds(), PNG_PATH);
                this.stateGraph.addState(this.crashState);
            }
            this.currentState = this.crashState;
            this.increaseGUIIndex();
        } else if (!this.stateGraph.isInStateList(newState, device, PNG_PATH, this.currentState)) {  // new state
            this.device.getScreenShot(PNG_PATH);
            if (desktopState != null && (!newState.getBounds().equals(desktopState.getBounds()) && !newState.getBounds().equals("")))
                markBounds(newState.getBounds(), PNG_PATH);
            this.stateGraph.addState(newState);
            this.currentState = newState;
            this.increaseGUIIndex();
        } else {                                                // visited state
            this.currentState = this.stateGraph.getEquivalentState();
            if (this.currentState.isEquivalentState()) {
                // 如果是equivalent state的話，檢查是否要更換圖片，並且不做addState
                if (newState.hasFewerElementThan(this.currentState)) {
                    if (!this.currentState.getImagelist().contains(newState.getImagelist().get(0)))
                        this.currentState.changeImage(newState.getImagelist().get(0));
                }
            }
            this.increaseGUIIndex();
        }
        updateStateGraphStopWatch.stop();
    }

    private void updateNafCount(GUIState newState) {
        int count = newState.getNafCount();
        this.nafCount += count;
        if (count > 0)
            this.nafStateCount++;
    }

    private void updateEventAndCheckNonDeterministicEvent(GUIState newState, AndroidEvent event) throws CannotReachTargetStateException {
        checkNonDeterministicEventStopWatch.start();
        if (!event.isVisited()) { // 第一次執行此事件
            event.setToState(this.currentState);
            event.setVisited(true);
            this.stateGraph.updateStatesCrossAppDepth(this.currentState);
        } else if (!newState.isExactlyEquivalentTo(event.getToState())) { // 第n次執行此事件，且此事件抵達之狀態與第一次執行此事件所抵達的狀態不同
            // 此事件為nondeterministic
            AndroidEvent nonDeterministicEvent = this.getNondeterministicEventIfExist(event);
            // 令event的出發狀態為Si，抵達狀態為Sj
            if (nonDeterministicEvent == null) {
                // Si過去沒有任何其他NondeterministicEvent，剛好也是抵達Sj，所以為Si製造一個新的 non deterministic event
                event.setNondeterministic(true);
                AndroidEvent duplicatedEvent = event.clone();
                duplicatedEvent.setVisited(true);
                duplicatedEvent.setToState(this.currentState);
                this.stateGraph.updateStatesCrossAppDepth(this.currentState);
                duplicatedEvent.setNondeterministic(true);
                duplicatedEvent.setFromState(event.getFromState());
                duplicatedEvent.setOrder(String.valueOf(this.eventOrder));
                duplicatedEvent.setToStatePictureId(this.currentState.getId());
                event.getFromState().addEvent(duplicatedEvent);
            } else // Si過去已經有一個NondeterministicEvent，剛好也是抵達Sj
                nonDeterministicEvent.setOrder(String.valueOf(this.eventOrder));
            this.eventOrder++;

            throw new CannotReachTargetStateException(event);
        }
        event.setOrder(String.valueOf(this.eventOrder));
        this.eventOrder++;
        checkNonDeterministicEventStopWatch.stop();
    }

    private AndroidEvent getNondeterministicEventIfExist(AndroidEvent originalEvent) {
        List<AndroidEvent> events = originalEvent.getFromState().getEvents();
        for (AndroidEvent event : events) {
            //copy pictureId並比較
            if (event.isNonDeterministic() && event.getToState() == this.currentState && event.getToStatePictureId() == originalEvent.getToStatePictureId())
                return event;
        }
        return null;
    }

    public GUIState getCurrentState() {
        return this.currentState;
    }

    public GUIState getRootState() {
        return this.rootState;
    }

    public StateGraph getStateGraph() {
        return this.stateGraph;
    }

    public boolean isDesktopState(GUIState state) throws NullPackageNameException {
        if (this.desktopState == null)
            throw new NullPointerException();
        boolean packageNameEqual = state.getPackageName().equals(this.desktopState.getPackageName());
        boolean activityNameEqual = state.getActivityName().equals(this.desktopState.getActivityName());
        return packageNameEqual && activityNameEqual;
    }

    private void increaseGUIIndex() {
        this.guiIndex += 1;
    }

    public String getStartTime() {
        return String.valueOf(this.startTime);
    }

    public int getRestartCount() {
        return restartCount;
    }

    private final String getStatePath() {
        return this.getReportPath() + "/States";
    }

    // for serialize
    public final String getReportPath() {
        final String REPORT_PATH = Utility.getReportPath();
        return REPORT_PATH;
    }

    // for serialize
    public void setStateGraph(StateGraph stateGraph) {
        this.stateGraph = stateGraph;
    }

    // return the count of states which contains NAF
    public int getNafStateCount() {
        return this.nafStateCount;
    }

    // return the count of NAF
    public int getNafCount() {
        return this.nafCount;
    }

    public int getTotalEquivalentRootStateRestartCount() {
        return  this.totalEquivalentRootStateRestartCount;
    }

    public int getRootStateCount() {
        return this.rootStateCount;
    }

    public Device getDevice() {
        return device;
    }

    public GUIState getDesktopState(){return desktopState;}

    public StopWatch getDumpXMLTimer() {
        return device.getDumpXMLStopWatch();
    }

    public StopWatch getPullXMLTimer() {
        return device.getPullXMLStopWatch();
    }

    public StopWatch getDeleteXMLTimer() {
        return device.getDeleteXMLStopWatch();
    }

    public StopWatch getGetScreenShotTimer() {
        return device.getGetScreenShotStopWatch();
    }

    public StopWatch getConvertXMLToGUIStateStopWatch() {
        return convertXMLToGUIStateStopWatch;
    }

    public StopWatch getUpdateStateGraphStopWatch() {
        return updateStateGraphStopWatch;
    }

    public StopWatch getRestartAppStopWatch() {
        return restartAppStopWatch;
    }

    public StopWatch getCheckNonDeterministicEventStopWatch() {
        return checkNonDeterministicEventStopWatch;
    }

    public StopWatch getExecuteEventStopWatch() {
        return executeEventStopWatch;
    }

    public StopWatch getExecuteOnStopWatch() {
        return executeOnStopWatch;
    }

    public StopWatch getTurnOffSoftKeyboardStopWatch() {
        return turnOffSoftKeyboardStopWatch;
    }

    public StopWatch getExecuteCmdTimer() {
        return device.getExecuteCmdStopWatch();
    }

    // mark bounds
    private void markBounds(String bounds, String FILE_PATH) {
        String[] parseBounds = this.parseCoordinate(bounds);
        try {
            paint(parseBounds, FILE_PATH);
        }
        catch (Exception ex) {

        }
    }

    private void paint(String[] parseBounds, String FILE_PATH) throws Exception {
        FileInputStream in = new FileInputStream(FILE_PATH);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        in.close();
        ImageIcon imageIcon = new ImageIcon(bytes);
        BufferedImage bufferedImage=new BufferedImage(imageIcon.getIconWidth(),imageIcon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g=(Graphics2D)bufferedImage.getGraphics();
        g.drawImage(imageIcon.getImage(),0,0,imageIcon.getIconWidth(),imageIcon.getIconHeight(),imageIcon.getImageObserver());
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(10.0f));
        g.drawRect(Integer.parseInt(parseBounds[0]), Integer.parseInt(parseBounds[1]),Integer.parseInt(parseBounds[2])-Integer.parseInt(parseBounds[0]),Integer.parseInt(parseBounds[3])-Integer.parseInt(parseBounds[1]));
        ImageIO.write(bufferedImage, "png",  new File(FILE_PATH));
    }

    private String[] parseCoordinate(String str) {
        String parseBounds = str.replace("[", "");
        parseBounds = parseBounds.substring(0, parseBounds.length() - 1);
        parseBounds = parseBounds.replace("]", ",");
        return parseBounds.split(",");
    }
}
