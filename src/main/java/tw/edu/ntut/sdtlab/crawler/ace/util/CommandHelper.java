package tw.edu.ntut.sdtlab.crawler.ace.util;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandHelper {

    public CommandHelper() {
        // TODO Auto-generated constructor stub
    }

    public static void executeCommand(String[] command) throws IOException {
        executeAndGetFeedBack(command);
    }

    public static String executeAndGetFeedBack(String[] command) throws IOException {
        String feedBack = null;
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        feedBack = bufferedReader.readLine();
        process.destroy();
        return feedBack;
    }

    public static List<String> executeCmd(String... cmd) throws IOException, InterruptedException, ExecuteCommandErrorException {
        ProcessBuilder proc = new ProcessBuilder(cmd);

        Process p = proc.start();
        p.waitFor();
        List<String> output = parseResult(p.getInputStream());
        return output;
    }

    private static List<String> parseResult(InputStream is) throws IOException {
        List<String> result = new ArrayList<>();
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bReader = new BufferedReader(reader);
        String line = null;
        while ((line = bReader.readLine())!=null){
            result.add(line);
        }
        return result;
    }
}
