package tw.edu.ntut.sdtlab.crawler.ace.util;

import tw.edu.ntut.sdtlab.crawler.ace.exception.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EnvironmentChecker {
    public EnvironmentChecker() {


    }

    public void checkEnviroment() {
        try {
            checkConfiguration("configuration/configuration.xml");
            checkADBPath(Config.ADB_PATH);
            checkGraphvizPath(Config.GRAPHVIZ_LAYOUT_PATH);
            checkSerialNumber(Config.DEVICE_SERIAL_NUMBER);
            checkPackageExist(Config.PACKAGE_NAME);
            checkActivityExist(Config.LAUNCHER_ACTIVITY);
            checkInputDataPath(Config.INPUT_DATA_FILE_NAME);
            TimeHelper.sleep(500);
        } catch (Exception e) {
            System.out.println();
            System.out.println("--  Environment Error :");
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private void checkADBPath(String adbPath) throws Exception {
        String ADB_PATH_ERROR_MASSAGE = "Cannot find adb command at " + adbPath;
        try {
            String testAdbCommand[] = {adbPath, "version"};
            CommandHelper.executeCmd(testAdbCommand);
        } catch (Exception e) {
            throw new ConfigItemException(ADB_PATH_ERROR_MASSAGE);
        }
    }

    private void checkGraphvizPath(String graphvizPath) throws Exception {
        String GRAPHIZ_PATH_ERROR_MASSAGE = "Cannot find graphviz command at " + graphvizPath;
        try {
            String testGraphvizCommand[] = {graphvizPath, "-V"};
            CommandHelper.executeCmd(testGraphvizCommand);
        } catch (Exception e) {
            throw new ConfigItemException(GRAPHIZ_PATH_ERROR_MASSAGE);
        }
    }

    private void checkSerialNumber(String serialNumber) throws Exception {
        String SERIALNUMBER_ERROR_MASSAGE = "Cannot find the device with serial number \"" + serialNumber + "\"";
        try {
            boolean isSerialNumberExisted = false;
            String testSerialNumberCommand[] = {Config.ADB_PATH, "devices"};
            List<String> result = CommandHelper.executeCmd(testSerialNumberCommand);
            for (int i = 0; i < result.size(); i++) {
                if ((result.get(i).indexOf("\t") != -1)) {
                    String temp = result.get(i).substring(0, result.get(i).indexOf("\t")); // to catch result of "ADB devices" that if they have serialNumber
                    if (temp.equals(serialNumber)) {
                        isSerialNumberExisted = true;
                        break;
                    }
                }
            }
            if (!isSerialNumberExisted)
                throw new ConfigItemException(SERIALNUMBER_ERROR_MASSAGE);
        } catch (Exception e) {
            throw new ConfigItemException(SERIALNUMBER_ERROR_MASSAGE);
        }
    }

    private void checkPackageExist(String packageName) throws Exception {
        final String PACKAGENAME_ERROR_MASSAGE = "Cannot find the app with packageName \"" + packageName + "\"";
        try {
            String testPackageCommand[] = {Config.ADB_PATH, "-s", Config.DEVICE_SERIAL_NUMBER, "shell", "pm", "path", packageName};
            if (CommandHelper.executeAndGetFeedBack(testPackageCommand).length() == 0)
                throw new ConfigItemException(PACKAGENAME_ERROR_MASSAGE);
        } catch (Exception e) {
            throw new ConfigItemException(PACKAGENAME_ERROR_MASSAGE);
        }
    }

    private void checkActivityExist(String activityName) throws Exception {
        final String ACTIVITY_ERROR_MASSAGE = "Cannot launch the app with launcherActivity \"" + activityName + "\"";
        try {
            String[] startCmd = {Config.ADB_PATH, "-s", Config.DEVICE_SERIAL_NUMBER, "shell", "am", "start", "-n", Config.PACKAGE_NAME + "/" + activityName};
            CommandHelper.executeCmd(startCmd);
            if (!activityName.equals(Config.PACKAGE_NAME + "." + getCurrentActivityForCheckActivityExist()))
                throw new ConfigItemException(ACTIVITY_ERROR_MASSAGE);
            else {
                return;
            }
        } catch (Exception e) {
            throw new ConfigItemException(ACTIVITY_ERROR_MASSAGE);
        }
    }


    private void checkInputDataPath(String inputDataPath) throws Exception {
        final String INPUTDATA_ERROR_MASSAGE = "Cannot find the input data at ./input-data/" + inputDataPath;
        if (!Config.INPUT_DATA_FILE_NAME.isEmpty()) {
            try {
                File file = new File("input-data/" + inputDataPath);
                if (!file.exists())
                    throw new ConfigItemException(INPUTDATA_ERROR_MASSAGE);
            } catch (Exception e) {
                throw new ConfigItemException(INPUTDATA_ERROR_MASSAGE);
            }
        }
    }

    private void checkConfiguration(String configuration) throws ConfigItemException {
        final String CONFIGURATION_ERROR_MASSAGE = "Cannot find the Configuration at ./configuration/" + configuration;
        try {
            File ConfigurationFile = new File(configuration);
            if (!ConfigurationFile.exists()) {
                throw new ConfigItemException(CONFIGURATION_ERROR_MASSAGE);
            }
        } catch (Exception e) {
            throw new ConfigItemException(CONFIGURATION_ERROR_MASSAGE);
        }
    }

    private String getCurrentActivityForCheckActivityExist() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String[] command = {Config.ADB_PATH, "-s",Config.DEVICE_SERIAL_NUMBER,"shell", "dumpsys", "activity", "activities", "|", "grep",
                "\"Run\\ #\""};
        List<String> cmdResult = CommandHelper.executeCmd(command);
        String result = cmdResult.get(0);
        String activityName = result.substring(result.indexOf("/") + 1, result.indexOf("}"));
        String[] str = activityName.split(" ");
        return str[0].replace(".", "");
    }
}
