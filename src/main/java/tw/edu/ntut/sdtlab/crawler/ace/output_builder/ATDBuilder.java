package tw.edu.ntut.sdtlab.crawler.ace.output_builder;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ATDBuilder extends ReportBuilder {
    private final String ATDPATH = this.DOT_DIR + "ActivityTransitionDiagram.dot";

    public ATDBuilder(StateGraph stateGraph) throws FileNotFoundException, UnsupportedEncodingException {
        super(stateGraph);
        final String DOT_PATH = this.reportPath + ATDPATH;
        File dotFile = new File(DOT_PATH);
        this.writer = new PrintWriter(dotFile, "UTF-8");
    }

    @Override
    protected void addHeader() {
        this.writer.println("digraph result {");
        this.writer.println("rankdir=LR;");
        this.writer.println("imagepath=\"" + this.reportPath + "\";");
    }

    @Override
    public void buildDot() throws FileNotFoundException, UnsupportedEncodingException {
        // ATD add header
        this.addHeader();
        List<GUIState> guiStates = this.stateGraph.getAllStates();
        for (int i = 0; i < guiStates.size(); i++) {
            // ATD add vertex
            this.writer.println(this.stateToVertex(guiStates.get(i)));
        }
        // ATD add edge
        if (Config.OUTPUT_LAYOUT_MULTIPLE_TRANSITION_AGGREGATION)
            createActivityConnectionWhenAggregation(this.writer);
        else
            createActivityConnectionWhenNotAggregation(this.writer);
        // ATD add footer
        this.addFooter();
    }

    @Override
    public void buildSVG() throws InterruptedException, ExecuteCommandErrorException, IOException {
        File dotFile = new File(this.reportPath + ATDPATH);
        String targetSVGFile = this.reportPath + "/ActivityTransitionDiagram.svg";
        this.createSVGFile(dotFile, targetSVGFile);
    }

    @Override
    public void buildTxt() {

    }

    @Override
    protected void addFooter() {
        this.writer.println("}");
        this.closeWriter();
    }

    // TODO : this method maybe can move to GUIState
    private String stateToVertex(GUIState guiState) {
        String crashStyle = guiState.isCrashState() ? "color = red," : "";
        int incoming = this.calculateActivityIncomingEvents(guiState.getActivityName());
        int outgoing = this.calculateActivityOutgoingEvents(guiState.getActivityName());
        return guiState.getActivityName() +
                "[label=<<table border=\"0\" cellborder=\"0\" " +
                "cellspacing=\"0\" cellpadding=\"0\">" +
                "<tr><td>" + guiState.getActivityName() + "</td></tr>" +
                "<tr><td>incoming: " + incoming + "</td></tr>" +
                "<tr><td>outgoing: " + outgoing + "</td></tr>" +
                "<tr><td width=\"130px\" height=\"230px\"" +
                " fixedsize=\"true\"><img src=\"States/" + guiState.getId() + ".png\"/>" +
                "</td></tr></table>>,shape=box,margin=0," + crashStyle + "URL=\"" +
                "ActivitySubstateDiagram/" + guiState.getActivityName() + ".svg\"];";
    }

    protected int calculateActivityIncomingEvents(String activityName) {
        int incomingEvents = 0;
        List<GUIState> guiStates = this.stateGraph.getAllStates();
        for (int i = 0; i < guiStates.size(); i++) {
            for (AndroidEvent e : guiStates.get(i).getEvents()) {
                if (e.getFromState() != null && e.getToState() != null &&
                        e.getFromState() != e.getToState() &&
                        !e.getFromState().getActivityName().equals(activityName) && e.getToState().getActivityName().equals(activityName))
                    incomingEvents++;
            }
        }
        return incomingEvents;
    }

    protected int calculateActivityOutgoingEvents(String activityName) {
        int outgoingEvents = 0;
        List<GUIState> guiStates = this.stateGraph.getAllStates();
        for (int i = 0; i < guiStates.size(); i++) {
            if (guiStates.get(i).getActivityName().equals(activityName)) {
                for (AndroidEvent e : guiStates.get(i).getEvents()) {
                    if (e.getToState() != null && !e.getToState().getActivityName().equals(activityName))
                        outgoingEvents++;
                }
            }
        }
        return outgoingEvents;
    }

    private void createActivityConnectionWhenNotAggregation(PrintWriter writer) {
        for (GUIState state : this.stateGraph.getAllStates()) {
            for (AndroidEvent event : state.getEvents()) {
                if (event.getToState() != null) { // Problem : somewhere e.getToState is null
                    if (!state.getActivityName().equals(event.getToState().getActivityName())) {
                        writer.print(writeStateConnect(state, event.getToState(), event));
                    }
                }
            }
        }
    }

    private String getAggregationBetweenActivities(String fromActivity, String toActivity) {
        String label = "";
        for (GUIState state : this.stateGraph.getAllStates()) {
            List<AndroidEvent> events = new ArrayList<>();
            for (AndroidEvent event : state.getEvents()) {
                if (fromActivity.equals(state.getActivityName()) && event.getToState() != null && event.getToState().getActivityName().equals(toActivity)) {
                    events.add(event);
                }
            }
            if (events.size() > 0) {
                label += this.writeStateConnectAggregation(state, events.get(0).getToState(), events) + "\n";
            }
        }
        return label;
    }

    // write the connection when config aggregation is true
    private void createActivityConnectionWhenAggregation(PrintWriter writer) {
        for (String toActivity : super.getActivityNames(this.stateGraph)) {
            for (String fromActivity : super.getActivityNames(this.stateGraph)) {
                if (!fromActivity.equals(toActivity)) {
                    String label = this.getAggregationBetweenActivities(fromActivity, toActivity);
                    if (label != "") {
                        writer.print(fromActivity + " -> " + toActivity + " [label = \"");
                        writer.print(label);
                        writer.print("\" ] ;");
                    }
                }
            }
        }
    }

    private String writeStateConnect(GUIState fromState, GUIState toState, AndroidEvent androidEvent) {
        List<AndroidEvent> events = new ArrayList<>();
        events.add(androidEvent);
        return fromState.getActivityName() + " -> " + toState.getActivityName() + " [label = \"" + this.writeStateConnectAggregation(fromState, toState, events)+ "\" ] ;";
    }

    private String writeStateConnectAggregation(GUIState fromState, GUIState toState, List<AndroidEvent> androidEvents) {
        final String CHANGE_LINE = "\n";
        String eventsReportLabel = "";
        for(int i = 0; i < androidEvents.size(); i++){
            eventsReportLabel += "  <" + fromState.getActivityName() + ".s" + fromState.getId() + ",";
            eventsReportLabel += this.displayOrder(androidEvents.get(i));
            eventsReportLabel += "," + toState.getActivityName() +
                    ".s" + toState.getId() + ">  ";
            if(i != androidEvents.size() - 1)
                eventsReportLabel += CHANGE_LINE;
        }
        return eventsReportLabel;
    }
}