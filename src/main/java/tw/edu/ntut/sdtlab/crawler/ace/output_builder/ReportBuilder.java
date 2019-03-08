package tw.edu.ntut.sdtlab.crawler.ace.output_builder;

import tw.edu.ntut.sdtlab.crawler.ace.util.CommandHelper;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Utility;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class ReportBuilder {
    protected StateGraph stateGraph  = null;
    protected String timestamp;
    protected String reportPath;
    protected PrintWriter writer;
    protected boolean multipleSelfLoopAggregation = false;
    protected boolean multipleTransitionAggregation = false;
    protected final String DOT_DIR = "/Dot/";
    protected final String DEFAULT_COLOR = "Black";
    protected final String NONDETERMINISTIC_COLOR = "DodgerBlue";
    protected final String TO_EQUIVALENT_STATE_COLOR = "Brown";

    protected ReportBuilder(StateGraph stateGraph) {
        this.timestamp = Utility.getTimestamp();
        this.setStateGraph(stateGraph);
        this.reportPath = Utility.getReportPath();
        this.setMultipleTransitionAggregation();
        this.setSelfLoopAggregation();
    }

    protected abstract void addHeader();

    public abstract void buildDot() throws IOException, InterruptedException, ExecuteCommandErrorException;

    public abstract void buildSVG() throws InterruptedException, ExecuteCommandErrorException, IOException;

    public abstract void buildTxt() throws IOException;

    protected abstract void addFooter();

    public void setStateGraph(StateGraph stateGraph) {
        this.stateGraph = stateGraph;
    }

    private void setMultipleTransitionAggregation() {
        if (Config.OUTPUT_LAYOUT_MULTIPLE_TRANSITION_AGGREGATION)
            multipleTransitionAggregation = true;
        else
            multipleTransitionAggregation = false;
    }

    private void setSelfLoopAggregation() {
        if (Config.OUTPUT_LAYOUT_MULTIPLE_SELF_LOOP_AGGREGATION)
            multipleSelfLoopAggregation = true;
        else
            multipleSelfLoopAggregation = false;
    }

    protected String displayOrder(AndroidEvent event) {
        if (Config.DISPLAY_EVENT_EXECUTION_ORDER) {
            return event.getReportLabel() + ": " + event.getOrder();
        } else {
            return event.getReportLabel();
        }
    }

    protected void createSVGFile(File dotFile, String outputPath) throws IOException, InterruptedException, ExecuteCommandErrorException {
        String dot = Config.GRAPHVIZ_LAYOUT_PATH;
        String dotFilePath = dotFile.getPath();
        String[] cmd = {dot, "-Tsvg", dotFilePath, "-o", outputPath};
        List<String> result = CommandHelper.executeCmd(cmd);
        assertTrue(result.isEmpty());
        assertTrue("targetFile: " + outputPath + " is not exist.", new File(outputPath).exists());
    }

    protected void closeWriter() {
        this.writer.close();
    }

    public List<String> getActivityNames(StateGraph stateGraph) {
        List<String> activityNames = new ArrayList<>();
        for(GUIState guiState : stateGraph.getAllStates()){
            if(!activityNames.contains(guiState.getActivityName())) {
                activityNames.add(guiState.getActivityName());
            }
        }
        return activityNames;
    }
    protected void createStateNodes(GUIState s, PrintWriter writer) {
        int counter = 1;
        String str = "";
        for (int i = 0; i < s.getImagelist().size(); i++) {
            str = s.getImagelist().get(i);
            if (i == 0) {
                writer.println("pic" + counter +
                        "[label=<<table border=\"1\" cellborder=\"0\" " +
                        "cellspacing=\"0\" cellpadding=\"0\">" +
                        "<tr><td>Original pic" + "</td></tr>" +
                        "<tr><td width=\"130px\" height=\"230px\" " +
                        "fixedsize=\"true\">" +
                        "<img src=\"" + str + "\"/>" +
                        "</td></tr></table>>,shape=box,margin=0," +
                        "URL=\"" + str + "\"];");
            } else {
                writer.println("pic" + counter +
                        "[label=<<table border=\"1\" cellborder=\"0\" " +
                        "cellspacing=\"0\" cellpadding=\"0\">" +
                        "<tr><td width=\"130px\" height=\"230px\" " +
                        "fixedsize=\"true\">" +
                        "<img src=\"" + str + "\"/>" +
                        "</td></tr></table>>,shape=box,margin=0," +
                        "URL=\"" + str + "\"];");
            }
            counter++;
        }

    }
}