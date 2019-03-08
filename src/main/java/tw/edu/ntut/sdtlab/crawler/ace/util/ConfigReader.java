package tw.edu.ntut.sdtlab.crawler.ace.util;

import tw.edu.ntut.sdtlab.crawler.ace.crawler.Device;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class ConfigReader {
    protected int guiIndex;
    protected Document document;
    protected CommandHelper commandHelper;
    //    protected String reportPath;
    protected String guiPagesPath; // put gui page file folder in reportPath
    protected String timeStamp;
    protected final String SLASH = "/";
    protected final String adbPath = Config.ADB_PATH;
    protected String filePackageName = "";
    protected String crashMessage = "";
    private Device device;
    private final String DOT_DIR = "Dot";
    private final String REPORT_PATH = System.getProperty("user.dir") + SLASH + Utility.getReportPath();
    private static Map<String, String> configMap = new LinkedHashMap<>();


    public ConfigReader(Device device) throws FileNotFoundException {
        this.device = device;
        guiIndex = 0;
        document = null;
        commandHelper = null;
        timeStamp = Utility.getTimestamp();
        filePackageName = Utility.getCreateFilePackageName();
        guiPagesPath = new String("gui_pages" + SLASH + timeStamp + "_" + filePackageName + "_" + Config.CRAWLING_ALGORITHM + "/States");
        crashMessage = Config.CRASH_MESSAGE;
        this.createReportPath();
        this.createDotPath();
        this.createASDPath();
        this.createStateFolder();
    }

    private void createReportPath() throws FileNotFoundException {
        this.createPath(REPORT_PATH);
    }

    // store .dot file
    private void createDotPath() throws FileNotFoundException {
        final String DOT_PATH = REPORT_PATH + SLASH + DOT_DIR;
        this.createPath(DOT_PATH);
    }

    // store ASD file
    private void createASDPath() throws FileNotFoundException {
        final String ASD_PATH = REPORT_PATH + SLASH + "ActivitySubstateDiagram";
        this.createPath(ASD_PATH);
    }

    // create state folder : put gui page file folder
    private void createStateFolder() throws FileNotFoundException {
        final String STATE_FOLDER = REPORT_PATH + SLASH + "States";
        this.createPath(STATE_FOLDER);
    }

    private void createPath(String path) throws FileNotFoundException {
        File file = new File(path);
        file.mkdirs();
        if (!file.exists())
            throw new FileNotFoundException("create path error : " + path);
    }

    public static String getConfigurationValue(String elementName) {
        return getConfigMap().get(elementName);
    }

    public static Map<String, String> getConfigMap() {
        if (configMap.isEmpty()) {
            readConfig();
        }
        return configMap;
    }
    private static void readConfig() {
        File pathConfigFile;
        Element element, rootElement = null;
        final String CONFIG_DIR_PATH = "configuration/";
        File folderConfig = new File(CONFIG_DIR_PATH);
        String[] listConfig = folderConfig.list();

        int cntConfigList = 0;
        while (cntConfigList < listConfig.length) {
            String CONFIG_FILE_PATH = listConfig[cntConfigList];
            pathConfigFile = new File(CONFIG_DIR_PATH + CONFIG_FILE_PATH);
            try {
                Document document = (new SAXReader()).read(pathConfigFile);
                rootElement = (Element) document.getRootElement().clone();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < rootElement.elements().size(); i++) {
                element = (Element) rootElement.elements().get(i);
                if (element.elements().size() > 0){ // does element contain sub elements
                    // put elements, include category one and innerElements
                    configMap.put(element.getName(), "\n"); // The element is a category element by recognizing "\n"
                    for (int j = 0; j < element.elements().size(); j++) {
                        Element innerElement = (Element) element.elements().get(j);
                        configMap.put(innerElement.getName(), innerElement.getText());
                    }
                } else {
                    configMap.put(element.getName(), element.getText());
                }
            }
            cntConfigList++;
        }
    }

}
