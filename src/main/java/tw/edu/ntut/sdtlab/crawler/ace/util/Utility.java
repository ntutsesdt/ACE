package tw.edu.ntut.sdtlab.crawler.ace.util;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utility {
    private static String timestamp;

    public static String getTimestamp() {
        if(timestamp == null)
           timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        return timestamp;
    }

    public static String getReportPath() {
        final String REPORT_PATH = "output/" + getTimestamp() + "_" + getCreateFilePackageName() + "_" + Config.CRAWLING_ALGORITHM;
        return REPORT_PATH;
    }

    public static String getCreateFilePackageName() {
        String[] name = Config.PACKAGE_NAME.split("\\.");
        return name[name.length - 1];
    }

    public static int getMin(int m, int n) {
        if(m < n)
            return m;
        return n;
    }


}
