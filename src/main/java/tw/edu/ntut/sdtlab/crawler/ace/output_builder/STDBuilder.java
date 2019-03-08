package tw.edu.ntut.sdtlab.crawler.ace.output_builder;

import tw.edu.ntut.sdtlab.crawler.ace.util.CommandHelper;
import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import static org.junit.Assert.assertTrue;

public class STDBuilder extends ReportBuilder {
    private File dotFile = null;
    private final String DOT_DIR = "/Dot/";
    private final String DOT_FILE_STD = "StateTransitionDiagram.dot";
    List<GUIState> guiStateList = null;

    public STDBuilder(StateGraph stateGraph) throws FileNotFoundException, UnsupportedEncodingException {
        super(stateGraph);
        final String DOT_PATH_STD = this.reportPath + DOT_DIR + DOT_FILE_STD;
        new File(this.reportPath + DOT_DIR).mkdirs();
        this.dotFile = new File(DOT_PATH_STD);
        this.writer = new PrintWriter(dotFile, "UTF-8");
        this.guiStateList = stateGraph.getAllStates();
    }

    @Override
    protected void addHeader() {
        this.writer.println("digraph g {");
        this.writer.println("graph [color=red];");
        this.writer.println("imagepath = \"" + this.reportPath + "\";");
    }

    @Override
    public void buildDot() throws IOException, InterruptedException, ExecuteCommandErrorException {
        addHeader();
        createActivityBounds();
        createGraph();
        addFooter();
    }

    @Override
    public void buildSVG() throws InterruptedException, ExecuteCommandErrorException, IOException {
        String dot = Config.GRAPHVIZ_LAYOUT_PATH;
        assertTrue("dotFile is not exist", dotFile.exists());
        String createFilePath = this.reportPath + "/StateTransitionDiagram.svg";
        String[] cmd = {dot, "-Tsvg", dotFile.getPath().toString(), "-o", createFilePath};
        CommandHelper.executeCommand(cmd);
        assertTrue("create result file error", new File(createFilePath).exists());
    }

    @Override
    public void buildTxt() throws IOException {
        assertTrue("dotFile is not exist", dotFile.exists());
        final String TXT_FILE_STD = "StateTransitionDiagram.txt";
        String TXT_PATH_STD = this.reportPath + "/" + TXT_FILE_STD;

        File txtFile = new File(TXT_PATH_STD);
        FileReader fr = new FileReader(dotFile);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(txtFile);
        String dotContent = null;
        String[] tokens;
        fw.write("Total state count is: " + guiStateList.size());
        fw.write(System.getProperty("line.separator"));
        Pattern pattern = Pattern.compile("[a-z]{5}_[0-9] ->");
        while ((dotContent = br.readLine()) != null) {
            if (pattern.matcher(dotContent).find()) {
                tokens = dotContent.split("\\[label = \"");
                fw.write(tokens[0]);
                fw.write(" [ ");
                fw.write(tokens[1]);
                while ((dotContent = br.readLine()) != null) {
                    if (dotContent.equals("\"];"))
                        break;
                    fw.write(" ,");
                    fw.write(dotContent);
                }
                fw.write(" ]");
                fw.write(System.getProperty("line.separator"));
            }
        }
        fr.close();
        br.close();
        fw.close();
        assertTrue("create State txt file fail", txtFile.exists());
    }

    @Override
    protected void addFooter() {
        this.writer.println("}");
        this.writer.close();
    }

    protected int getIndexOfTypeList(ArrayList<String> typeList, String activityName) {
        int index = -1;
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).compareTo(activityName) == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    protected ArrayList<String> getTypeOfActivityNameList() {
        List<String> activityNameList = super.getActivityNames(this.stateGraph);
        ArrayList<String> typeList = new ArrayList<String>();
        for (String activityName : activityNameList) {
            int index = getIndexOfTypeList(typeList, activityName);
            if (index < 0) {
                typeList.add(activityName);
            }
        }
        return typeList;
    }



    protected void createSubGraph(GUIState s) throws InterruptedException, ExecuteCommandErrorException, IOException {
        File dotFile = new File(this.reportPath + this.DOT_DIR + s.getId() + "Equivalent" + ".dot");
        String targetSVGFile = this.reportPath + "/States/" + s.getId() + "Equivalent" + ".svg";
        try {
            PrintWriter w = new PrintWriter(dotFile, "UTF-8");
            w.println("graph " + "temp" + " {");
            w.println("rankdir=LR;");
            w.println("imagepath = \"" + this.reportPath + "/States" + "\";");
            this.createStateNodes(s, w);
            w.println("}");
            w.close();
            this.createSVGFile(dotFile, targetSVGFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected void createActivityBounds() throws InterruptedException, ExecuteCommandErrorException, IOException {
        List<String> typeList = this.getTypeOfActivityNameList();
        List<GUIState> guiStateList = stateGraph.getAllStates();
        for (int i = 0; i < typeList.size(); i++) {
            this.writer.println("label = \"  " + typeList.get(i) + "   \";");
            for (GUIState s : guiStateList) {
                if (typeList.get(i).equals(s.getActivityName())) {
                    if (s.isCrashState()) {
                        this.writer.print("state_" + s.getId());
                        this.writer.println(" [label=<<table border=\"1\" cellborder=\"0\" " +
                                "cellspacing=\"0\" cellpadding=\"0\">" +
                                "<tr><td>" + "Crash State" + "</td></tr>" +
                                "<tr><td width=\"130px\" height=\"230px\"" +
                                " fixedsize=\"true\"><img src=\"States/" + s.getImagelist().get(0) + "\"/>" +
                                "</td></tr></table>>,shape=box,margin=0," +
                                "color = red," +
                                "URL=\"States/" + s.getImagelist().get(0) + "\"];");
                    } else if (s.isUnrecognizableState() || s.isProgressBarTimeoutState()) {
                        this.writer.print("state_" + s.getId());
                        this.writer.println(" [label=<<table border=\"0\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
                        String stateName = s.isUnrecognizableState() ? "Unrecognizable State" : "ProgressBarTimeout State";
                        this.writer.print("<tr><td>" + stateName + "</td></tr>");
                        String path;
                        if (s.isEquivalentState()) {
                            this.writer.print("<tr><td>" + "EquivalentStateCount = " + s.getEquivalentStateCount() + "</td></tr>");
                            path = s.getId() + "Equivalent.svg";
                        } else {
                            path = s.getImagelist().get(0);
                        }
                        this.writer.print("<tr><td width=\"150px\" height=\"230px\" fixedsize=\"true\">" +
                                "<img src=\"States/" + s.getImagelist().get(0) + "\"/>" +
                                "</td></tr></table>>,shape=box,margin=0," +
                                "color = peru," +
                                "URL=\"States/" + path + "\"];");
                    } else if (s.isEquivalentState()) {
                        createSubGraph(s);
                        this.writer.print("state_" + s.getId());
                        this.writer.println(" [label=<<table border=\"0\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                                "<tr><td>" + "Equivalent State" + "</td></tr>" +
                                "<tr><td>" + "EquivalentStateCount = " + s.getEquivalentStateCount() + "</td></tr>" +
                                "<tr><td width=\"150px\" height=\"230px\"" +
                                " fixedsize=\"true\"><img src=\"States/" + s.getImagelist().get(0) + "\"/>" +
                                "</td></tr></table>>,shape=box,margin=0," +
                                "color = peru," +
                                "URL=\"States/" + s.getId() + "Equivalent" + ".svg\"];");
                    } else {
                        this.writer.print("state_" + s.getId());
                        this.writer.print(" [label=\"\",shape=box,fixedsize=true,width=1.8,height=3.2");
                        this.writer.print(",image=\"States/" + s.getImagelist().get(0) + "\"");
                        this.writer.println(",URL=\"States/" + s.getImagelist().get(0) + "\"];");
                    }
                }
            }
        }
    }

    protected void createGraph() {
        for (GUIState state : this.stateGraph.getAllStates()) {
            List<AndroidEvent> selfLoopEvents = new ArrayList<>();
            List<AndroidEvent> multiTransitionEvents = new ArrayList<>();
            for (AndroidEvent event : state.getEvents()) {
                if (event.getToState() != null) {
                    if (event.getToState().getId() == state.getId())
                        selfLoopEvents.add(event);
                    else
                        multiTransitionEvents.add(event);
                }
            }
            this.createSelfLoopEvents(selfLoopEvents);
            this.createMultipleTransitionEvents(multiTransitionEvents);
        }
    }

    private List<AndroidEvent> selectNondeterministicEvents(List<AndroidEvent> events) {
        List<AndroidEvent> selectedEvents = new ArrayList<>();
        for (AndroidEvent event : events) {
            if (event.isNonDeterministic())
                selectedEvents.add(event);
        }
        return selectedEvents;
    }

    private List<AndroidEvent> selectDeterministicEventsAndNotToOriginState(List<AndroidEvent> events) {
        List<AndroidEvent> selectedEvents = new ArrayList<>();
        for (AndroidEvent event : events) {
            if (!event.isNonDeterministic() && !event.isToOriginalState())
                selectedEvents.add(event);
        }
        return selectedEvents;
    }

    private List<AndroidEvent> selectDeterministicEventsAndToOriginState(List<AndroidEvent> events) {
        List<AndroidEvent> selectedEvents = new ArrayList<>();
        for (AndroidEvent event : events) {
            if (!event.isNonDeterministic() && event.isToOriginalState())
                selectedEvents.add(event);
        }
        return selectedEvents;
    }

    private List<AndroidEvent> selectToStateTheSameWithState0(List<AndroidEvent> events) {
        List<AndroidEvent> selectedEvents = new ArrayList<>();
        for (AndroidEvent event : events) {
            if (event.getToState().getId() == events.get(0).getToState().getId())
                selectedEvents.add(event);
        }
        return selectedEvents;
    }

    private void createMultipleTransitionEvents(List<AndroidEvent> multiTransitionEvents) {
        if (!multiTransitionEvents.isEmpty()) {
            if (super.multipleTransitionAggregation) {
                // nonDeterministic event
                List<AndroidEvent> nonDeterministicEvents = this.selectNondeterministicEvents(multiTransitionEvents);
                while (!nonDeterministicEvents.isEmpty()) {
                    List<AndroidEvent> events = this.selectToStateTheSameWithState0(nonDeterministicEvents);
                    String label = this.combineEventsLabel(events);
                    AndroidEvent tempEvent = events.get(0);
                    this.printTransition(tempEvent.getFromState(), tempEvent.getToState().getId(), label, super.NONDETERMINISTIC_COLOR);
                    nonDeterministicEvents.removeAll(events);
                }
                // toEquivalentsState event
                List<AndroidEvent> toEquivalentStateEvents = this.selectDeterministicEventsAndNotToOriginState(multiTransitionEvents);
                while (!toEquivalentStateEvents.isEmpty()) {
                    List<AndroidEvent> events = this.selectToStateTheSameWithState0(toEquivalentStateEvents);
                    String label = this.combineEventsLabel(events);
                    AndroidEvent tempEvent = events.get(0);
                    this.printTransition(tempEvent.getFromState(), tempEvent.getToState().getId(), label, super.TO_EQUIVALENT_STATE_COLOR);
                    toEquivalentStateEvents.removeAll(events);
                }
                // deterministic event
                List<AndroidEvent> deterministicEvents = this.selectDeterministicEventsAndToOriginState(multiTransitionEvents);
                while (!deterministicEvents.isEmpty()) {
                    List<AndroidEvent> events = this.selectToStateTheSameWithState0(deterministicEvents);
                    String label = this.combineEventsLabel(events);
                    AndroidEvent tempEvent = events.get(0);
                    this.printTransition(tempEvent.getFromState(), tempEvent.getToState().getId(), label, super.DEFAULT_COLOR);
                    deterministicEvents.removeAll(events);
                }
            } else {
                for (AndroidEvent event : multiTransitionEvents) {
                    String color = this.getColor(event);
                    this.printTransition(event.getFromState(), event.getToState().getId(), super.displayOrder(event), color);
                }
            }
        }
    }

    private void createSelfLoopEvents(List<AndroidEvent> selfLoopEvents) {
        if (!selfLoopEvents.isEmpty()) {
            if (super.multipleSelfLoopAggregation) {
                // nonDeterministic event
                List<AndroidEvent> nonDeterministicEvents = this.selectNondeterministicEvents(selfLoopEvents);
                if (!nonDeterministicEvents.isEmpty()) {
                    String label = this.combineEventsLabel(nonDeterministicEvents);
                    AndroidEvent tempEvent = nonDeterministicEvents.get(0);
                    this.printTransition(tempEvent.getFromState(), tempEvent.getToState().getId(), label, super.NONDETERMINISTIC_COLOR);
                }
                // toEquivalentState event
                List<AndroidEvent> toEquivalentStateEvents = this.selectDeterministicEventsAndNotToOriginState(selfLoopEvents);
                if (!toEquivalentStateEvents.isEmpty()) {
                    String label = this.combineEventsLabel(toEquivalentStateEvents);
                    AndroidEvent tempEvent = toEquivalentStateEvents.get(0);
                    this.printTransition(tempEvent.getFromState(), tempEvent.getToState().getId(), label, super.TO_EQUIVALENT_STATE_COLOR);
                }
                // deterministic event
                List<AndroidEvent> deterministicEvents = this.selectDeterministicEventsAndToOriginState(selfLoopEvents);
                if (!deterministicEvents.isEmpty()) {
                    String label = this.combineEventsLabel(deterministicEvents);
                    AndroidEvent tempEvent = deterministicEvents.get(0);
                    this.printTransition(tempEvent.getFromState(), tempEvent.getToState().getId(), label, super.DEFAULT_COLOR);
                }
            } else {
                for (AndroidEvent event : selfLoopEvents) {
                    String color = this.getColor(event);
                    this.printTransition(event.getFromState(), event.getToState().getId(), super.displayOrder(event), color);
                }
            }
        }
    }

    private String combineEventsLabel(List<AndroidEvent> events) {
        String combineLabel = "";
        for (AndroidEvent event : events) {
            combineLabel += super.displayOrder(event) + "\n";
        }
        return combineLabel;
    }

    private String getColor(AndroidEvent event) {
        if (event.isNonDeterministic())
            return super.NONDETERMINISTIC_COLOR;
        else if (!event.isToOriginalState())
            return super.TO_EQUIVALENT_STATE_COLOR;
        else
            return super.DEFAULT_COLOR;
    }

    private void printTransition(GUIState fromState, int toStateId, String label, String color) {
        this.writer.print("state_" + fromState.getId() + " -> ");
        this.writer.print("state_" + toStateId);
        this.writer.println("[label = \"" + label + "\",fontcolor = " + color + ",color = " + color + "];");
    }
}