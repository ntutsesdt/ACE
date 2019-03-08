package tw.edu.ntut.sdtlab.crawler.ace.util;

public class Config {
    public static final String ADB_PATH = ConfigReader.getConfigurationValue("adb");
    public static final String GRAPHVIZ_LAYOUT_PATH = ConfigReader.getConfigurationValue("graphvizLayout");
    public static final String DEVICE_SERIAL_NUMBER=ConfigReader.getConfigurationValue("deviceSerialNumber");
    public static final String PACKAGE_NAME = ConfigReader.getConfigurationValue("packageName");
    public static final String LAUNCHER_ACTIVITY = ConfigReader.getConfigurationValue("launcherActivity");
    public static final double EVENT_SLEEP_TIMESECOND = Double.valueOf(ConfigReader.getConfigurationValue("eventSleepTimeSecond"));
    public static final int START_APP_SLEEP_TIMESECOND = Integer.valueOf(ConfigReader.getConfigurationValue("startAppSleepTimeSecond"));
    public static final int CLOSE_APP_SLEEP_TIMESECOND = Integer.valueOf(ConfigReader.getConfigurationValue("closeAppSleepTimeSecond"));
    public static final int CROSS_APP_EVENT_THRESHHOLD = Integer.valueOf(ConfigReader.getConfigurationValue("crossAppEventThreshold"));
    public static final int MAX_LIST_GRID_SIZE_THRESHOLD = Integer.valueOf(ConfigReader.getConfigurationValue("maxListGridSizeThreshold"));
    public static final int CONTENT_SENSITIVE_LIST_GRID_SIZE_THRESHOLD = Integer.valueOf(ConfigReader.getConfigurationValue("contentSensitiveListGridSizeThreshold"));
    public static final int HEAD_TAIL_LIST_GRID_SIZE_THRESHOLD = Integer.valueOf(ConfigReader.getConfigurationValue("headTailListGridSizeThreshold"));
    public static final int NONDETERMINISTIC_EVENT_ATTEMPT_COUNT_THRESHOLD = Integer.valueOf(ConfigReader.getConfigurationValue("nondeterministicEventAttemptCountThreshold"));
    public static final int EQUIVALENT_ROOTSTATE_RESTART_ATTEMPT_COUNT = Integer.valueOf(ConfigReader.getConfigurationValue("equivalentRootStateRestartAttemptCount"));
    public static final long TIMEOUT_SECOND = Long.valueOf(ConfigReader.getConfigurationValue("timeoutSecond"));
    public static final int MAX_EQUAL_DISTANCE_THRESHOLD = Integer.valueOf(ConfigReader.getConfigurationValue("maxEqualDistanceThreshold"));
    public static final String CRASH_MESSAGE = ConfigReader.getConfigurationValue("crashMessage");
    public static final boolean OUTPUT_LAYOUT_MULTIPLE_TRANSITION_AGGREGATION = isTrue("outputLayoutMultipleTransitionAggregation");
    public static final boolean OUTPUT_LAYOUT_MULTIPLE_SELF_LOOP_AGGREGATION = isTrue("outputLayoutMultipleSelfLoopAggregation");
    public static final boolean BLOCK_NONDETERMINISTIC_EVENT = isTrue("blockNondeterministicEvent");
    public static final String CRAWLING_ALGORITHM = ConfigReader.getConfigurationValue("crawlingAlgorithm");
    public static final boolean DISPLAY_EVENT_EXECUTION_ORDER = isTrue("displayEventExecutionOrder");
    public static final int OUTPUT_MAX_EVENT_LABEL_LENGTH = Integer.valueOf(ConfigReader.getConfigurationValue("outputMaxEventLabelLength"));
    public static final boolean ENABLE_MENUKEY_EVENT = isTrue("enableMenukeyEvent");
    public static final boolean ENABLE_BACKKEY_EVENT = isTrue("enableBackkeyEvent");
    public static final boolean ENABLE_SWIPE_EVENT = isTrue("enableSwipeEvent");
    public static final boolean ENABLE_LONGCLICK_EVENT = isTrue("enableLongClickEvent");
    public static final boolean ENABLE_CHECK_EVENT = isTrue("enableCheckEvent");
    public static final boolean ENABLE_ACTIVITY_SCROLL_EVENT = isTrue("enableActivityScrollEvent");
    public static final boolean ENABLE_COMPONENT_SCROLL_EVENT = isTrue("enableComponentScrollEvent");
    public static final String TEXT_VIEW_CLICK_EVENT_OPTION = getTextViewClickEventOption("textViewClickEventOption");
    public static final boolean ENABLE_IMAGEVIEW_CLICK_EVENT = isTrue("enableImageViewClickEvent");
    public static final boolean DISABLE_CLICK_EVENT_WHEN_COMPONENT_IS_CHECKABLE = isTrue("disableClickEventWhenComponentIsCheckable");
    public static final boolean ENABLE_PERMISSION_WRITE_EXTERNAL_STORAGE =isTrue("enablePermission_WRITE_EXTERNAL_STORAGE");
    public static final boolean ENABLE_PERMISSION_READ_EXTERNAL_STORAGE =isTrue("enablePermission_READ_EXTERNAL_STORAGE");
    public static final boolean IGNORE_BOUNDS_ATTRIBUTE = isTrue("ignoreBoundsAttribute");
    public static final boolean WAIT_FOR_PROGRESS_BAR = isTrue("waitForProgressBar");
    public static final long WAIT_FOR_PROGRESS_BAR_TIMESECOND = Integer.valueOf(ConfigReader.getConfigurationValue("waitForProgressBarTimeSec"));
    public static final String DEV_KILL_APP_KEYCODE = ConfigReader.getConfigurationValue("deviceKillAppKeyCode");
    public static final String KILL_APP_KEYCODE = ConfigReader.getConfigurationValue(DEV_KILL_APP_KEYCODE);
    public static final boolean APP_INSTRUMENTED = isTrue("appInstrumented");
    public static final String EQUIVALENT_LEVEL = ConfigReader.getConfigurationValue("equivalentLevel");
    public static final boolean ENABLE_EACH_CHOICE = isTrue("enableEachChoice");
    public static final String EVENT_ORDER = ConfigReader.getConfigurationValue("eventOrder");
    public static final long AT_THE_SAME_STATE_TIMEOUT = Long.valueOf(ConfigReader.getConfigurationValue("atTheSameStateTimeoutSecond"));
    public static final long CROSS_APP_STATE_TIMEOUT = Long.valueOf(ConfigReader.getConfigurationValue("crossAppStateTimeoutSecond"));
    public static final boolean INTEGRATE_EDIT_TEXT = isTrue("integrateEditText");
    public static final String INPUT_DATA_FILE_NAME = ConfigReader.getConfigurationValue("inputDataFileName");
    public static final boolean IGNORE_NAF = isTrue("ignoreNAF");
    public static final boolean IGNORE_FOCUSED_ATTRIBUTE = isTrue("ignoreFocusedAttribute");
    private String serailNum = null;

    private static boolean isTrue(String config) throws IllegalArgumentException {
        if(ConfigReader.getConfigurationValue(config).equals("true"))
            return true;
        else if(ConfigReader.getConfigurationValue(config).equals("false"))
            return false;
        throw new IllegalArgumentException();
    }

    private static String getTextViewClickEventOption(String config) throws  IllegalArgumentException {
        String option = ConfigReader.getConfigurationValue(config);
        if (option.equals("clickIfClickableIsTrue") || option.equals("alwaysClick") || option.equals("alwaysDoNotClick"))
            return option;
        throw new IllegalArgumentException("textViewClickEventOption should be clickIfClickableIsTrue, alwaysClick, or alwaysDoNotClick.");
    }

    public final String getDeviceSerialNum() {
        if(this.serailNum == null) {
            this.serailNum = String.valueOf(ConfigReader.getConfigurationValue("deviceSerialNumber"));
        }
        return this.serailNum;
    }
}
