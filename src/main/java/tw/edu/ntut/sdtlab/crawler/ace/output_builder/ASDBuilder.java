package tw.edu.ntut.sdtlab.crawler.ace.output_builder;

import tw.edu.ntut.sdtlab.crawler.ace.exception.ExecuteCommandErrorException;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.event.AndroidEvent;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class ASDBuilder extends ReportBuilder {
    private final boolean multipleSelfLoopAggregation = Config.OUTPUT_LAYOUT_MULTIPLE_SELF_LOOP_AGGREGATION;
    private final boolean multipleTransitionAggregation = Config.OUTPUT_LAYOUT_MULTIPLE_TRANSITION_AGGREGATION;
    private final String ASD_PATH = this.reportPath + "/ActivitySubstateDiagram/";

    public ASDBuilder(StateGraph stateGraph) {
        super(stateGraph);
        new File(ASD_PATH).mkdirs();
    }

    @Override
    protected void addHeader() {

    }

    @Override
    public void buildDot() throws IOException, InterruptedException, ExecuteCommandErrorException {
        File stateDOTFile = null;
        int id = 0;
        int incoming = 0;
        int outgoing = 0;
        int loop = 0;
        List<String> activities = super.getActivityNames(this.stateGraph);
        for (int i = 0; i < activities.size(); i++) {
            String activity = activities.get(i);
            stateDOTFile = new File(this.reportPath + this.DOT_DIR + activities.get(i) + ".dot");
            this.writer = new PrintWriter(stateDOTFile, "UTF-8");
            // ASD add header
            this.writer.println("digraph " + activities.get(i) + "{");
            //writer.println("charset=utf8" +   ";");
            this.writer.println("imagepath=\"" + this.reportPath + "\";");
            for (GUIState s : this.stateGraph.getAllStates()) {
                if (s.getActivityName().equals(activity)) {
                    id = s.getId();
                    incoming = this.calculateStateIncomingEvents(s);
                    outgoing = this.calculateStateOutgoingEvents(s);
                    loop = this.calculateStateLoopEvents(s);
                    // ASD add vertex
                    String message = "";
                    String equivalentStyle = "";
                    if (!s.isEquivalentState() && !s.isCrashState()) { // normal state
                        this.writer.println("state_" + id +
                                "[label=<<table border=\"1\" cellborder=\"0\" " +
                                "cellspacing=\"0\" cellpadding=\"0\">" +
                                "<tr><td>state_" + id + "</td></tr>" +
                                "<tr><td>incoming: " + incoming + "</td></tr>" +
                                "<tr><td>outgoing: " + outgoing + "</td></tr>" +
                                "<tr><td>loop: " + loop + "</td></tr>" +
                                "<tr><td width=\"130px\" height=\"230px\" " +
                                "fixedsize=\"true\">" +
                                "<img src=\"States/" + s.getImagelist().get(0) + "\"/>" +
                                "</td></tr></table>>,shape=box,margin=0," +
                                "URL=\"../States/" +
                                s.getImagelist().get(0) + "\"];");
                    } else if (s.isEquivalentState() && !s.isCrashState()) { // equivalent state
                        this.createSubGraph(s);
                        equivalentStyle = "color = peru,";
                        message = "Equivalent State";

                        this.writer.println("state_" + id +
                                "[label=<<table border=\"1\" cellborder=\"0\" " +
                                "cellspacing=\"0\" cellpadding=\"0\">" +
                                "<tr><td>state_" + id + "</td></tr>" +
                                "<tr><td>incoming: " + incoming + "</td></tr>" +
                                "<tr><td>outgoing: " + outgoing + "</td></tr>" +
                                "<tr><td>loop: " + loop + "</td></tr>" +
                                "<tr><td>" + message + "</td></tr>" +
                                "<tr><td>" + "EquivalentStateCount: " + s.getEquivalentStateCount() + "</td></tr>" +
                                "<tr><td width=\"150px\" height=\"230px\" " +
                                "fixedsize=\"true\">" +
                                "<img src=\"States/" + s.getImagelist().get(0) + "\"/>" +
                                "</td></tr></table>>,shape=box,margin=0," + equivalentStyle +
                                "URL=\"../States/" +
                                id + "Equivalent" + ".svg\"];");
                    } else if (s.isCrashState()) { // crash state
                        message = "Crash State";
                        this.writer.println("state_" + id +
                                "[label=<<table border=\"0\" cellborder=\"0\" " +
                                "cellspacing=\"0\" cellpadding=\"0\">" +
                                "<tr><td>state_" + id + "</td></tr>" +
                                "<tr><td>incoming: " + incoming + "</td></tr>" +
                                "<tr><td>outgoing: " + outgoing + "</td></tr>" +
                                "<tr><td>loop: " + loop + "</td></tr>" +
                                "<tr><td>" + message + "</td></tr>" +
                                "<tr><td width=\"130px\" height=\"230px\" " +
                                "fixedsize=\"true\">" +
                                "<img src=\"States/" + s.getImagelist().get(0) + "\"/>" +
                                "</td></tr></table>>,shape=box,margin=0," + "color = red," +
                                "URL=\"../States/" +
                                s.getImagelist().get(0) + "\"];");
                    }
                }
            }
            this.createStateConnection(this.writer, i);
            this.addFooter();
        }
    }

    @Override
    public void buildSVG() throws InterruptedException, ExecuteCommandErrorException, IOException {
        List<String> activities = super.getActivityNames(this.stateGraph);
        for (int i = 0; i < activities.size(); i++) {
            File dotFile = new File(this.reportPath + this.DOT_DIR + activities.get(i) + ".dot");
            String targetSVGFile = ASD_PATH + activities.get(i) + ".svg";
            this.createSVGFile(dotFile, targetSVGFile);
        }
    }

    @Override
    public void buildTxt() {

    }

    @Override
    protected void addFooter() {
        this.writer.println("}");
        this.closeWriter();
    }

    private int calculateStateIncomingEvents(GUIState s) {
        int incomingCount = 0;
        List<GUIState> guiStates = this.stateGraph.getAllStates();
        for (int i = 0; i < guiStates.size(); i++) {
            for (AndroidEvent e : guiStates.get(i).getEvents()) {
                if (e.getFromState() != null && e.getToState() != null &&
                        e.getFromState() != e.getToState() && e.getToState() == s)
                    incomingCount++;
            }
        }
        return incomingCount;
    }

    private int calculateStateOutgoingEvents(GUIState s) {
        int outGoingCount = 0;
        for (AndroidEvent e : s.getEvents()) {
            if (e.getFromState() != null && e.getToState() != null &&
                    e.getFromState() != e.getToState())
                outGoingCount++;
        }
        return outGoingCount;
    }

    private void createStateConnection(PrintWriter writer, int index) {
        String str = "";
        HashMap<Integer, String> multiTransitionMap = new HashMap<Integer, String>();
        HashMap<Integer, String> selfLoopMap = new HashMap<Integer, String>();
        HashMap<Integer, String> nondeterministicMultiTransitionMap = new HashMap<Integer, String>();
        HashMap<Integer, String> nonDeterministicSelfLoopMap = new HashMap<Integer, String>();

        for (GUIState s : this.stateGraph.getAllStates()) {
            // clear four maps
            multiTransitionMap.clear();
            selfLoopMap.clear();
            nondeterministicMultiTransitionMap.clear();
            nonDeterministicSelfLoopMap.clear();


            if (s.getActivityName().equals(super.getActivityNames(this.stateGraph).get(index))) {
                for (AndroidEvent e : s.getEvents()) {
                    str = "";
                    if (e.getToState() != null) { // Problem : somewhere e.getToState is null
                        // same activity
                        if (e.getToState().getActivityName().equals(super.getActivityNames(this.stateGraph).get(index))) {
                            if (e.getToState().getId() == s.getId()) { // self loop
                                if (multipleSelfLoopAggregation) {
                                    if (e.isNonDeterministic()) { // event is nondeterministic
                                        if (nonDeterministicSelfLoopMap.get(e.getToState().getId()) != null) {
                                            str = nonDeterministicSelfLoopMap.get(e.getToState().getId());
                                        }
                                        str = str + this.displayOrder(e) + "\n";
                                        nonDeterministicSelfLoopMap.put(e.getToState().getId(), str);
                                    } else {
                                        if (selfLoopMap.get(e.getToState().getId()) != null) {
                                            str = selfLoopMap.get(e.getToState().getId());
                                        }
                                        str = str + this.displayOrder(e) + "\n";
                                        selfLoopMap.put(e.getToState().getId(), str);
                                    }
                                } else { // no aggregation
                                    if (e.isNonDeterministic()) {
                                        writer.print("state_" + s.getId() + " -> ");
                                        writer.print("state_" + e.getToState().getId());
                                        writer.println(" [label = \"" + this.displayOrder(e) + "\" ,fontcolor = DodgerBlue,color = DodgerBlue];");
                                    } else {
                                        writer.print("state_" + s.getId() + " -> ");
                                        writer.print("state_" + e.getToState().getId());
                                        writer.println(" [label = \"" + this.displayOrder(e) + "\"];");
                                    }
                                }
                            }
                        }
                        // to different activity
                        else {
                            writer.println(e.getToState().getActivityName() +
                                    " [fontcolor = teal,color = teal];");
                            writer.print("state_" + s.getId() + " -> " +
                                    e.getToState().getActivityName());
                            writer.println(" [label = \"   " + this.displayOrder(e) + "   \"," +
                                    "fontcolor = teal,color = teal];");
                        }
                    }
                } // end of for(Android e:...
                for (Object key : multiTransitionMap.keySet()) {
                    writer.print("state_" + s.getId() + " -> ");
                    writer.print("state_" + key);
                    writer.println("[label = \"" + multiTransitionMap.get(key) + "\"];");
                }
                for (Object key : selfLoopMap.keySet()) {
                    writer.print("state_" + s.getId() + " -> ");
                    writer.print("state_" + key);
                    writer.println("[label = \"" + selfLoopMap.get(key) + "\"];");
                }
                for (Object key : nondeterministicMultiTransitionMap.keySet()) {
                    writer.print("state_" + s.getId() + " -> ");
                    writer.print("state_" + key);
                    writer.println("[label = \"" + nondeterministicMultiTransitionMap.get(key) + "\",fontcolor = DodgerBlue,color = DodgerBlue];");
                }
                for (Object key : nonDeterministicSelfLoopMap.keySet()) {
                    writer.print("state_" + s.getId() + " -> ");
                    writer.print("state_" + key);
                    writer.println("[label = \"" + nonDeterministicSelfLoopMap.get(key) + "\",fontcolor = DodgerBlue,color = DodgerBlue];");
                }
            } // end of for(GUIState s:...

            // from different activity's event to this activity
            else {
                for (AndroidEvent e : s.getEvents()) {
                    if (e.getToState() != null) { // �����D�������A�Y��e.toState�|�Onull
                        if (e.getToState().getActivityName().equals(super.getActivityNames(this.stateGraph).get(index))) {
                            this.writeStateConnectionFormAnotherAcitvity(writer, s.getActivityName(), e.getToState().getId(), this.displayOrder(e));
                            //writeStateConnectionFormAnotherAcitvity(writer, getActivityCount(s.getActivity()), e.getToState().getId(), e.getReportLabel());
                        }
                    }
                }
            }
        }
    }

    private String nonDeterministicColor(boolean isNondeterministic) {
        if (isNondeterministic) {
            return "\" ,fontcolor = DodgerBlue,color = DodgerBlue];";
        } else {
            return "\"];";
        }
    }

    private int calculateStateLoopEvents(GUIState s) {
        int loopCount = 0;
        for (AndroidEvent e : s.getEvents()) {
            if (e.getFromState() != null && e.getToState() != null &&
                    e.getFromState() == e.getToState())
                loopCount++;
        }
        return loopCount;
    }

    private void createSubGraph(GUIState s) throws InterruptedException, ExecuteCommandErrorException, IOException {
        File dotFile = new File(this.reportPath + this.DOT_DIR + s.getId() + "Equivalent" + ".dot");
        String targetSVGFile = this.reportPath + "/States/" + s.getId() + "Equivalent" + ".svg";
        PrintWriter writer = null;
        writer = new PrintWriter(dotFile, "UTF-8");
        writer.println("graph " + "temp" + " {");
        writer.println("rankdir=LR;");
        writer.println("imagepath = \"" + this.reportPath + "/States" + "\";");
        this.createStateNodes(s,writer );
        writer.println("}");
        writer.close();
        createSVGFile(dotFile, targetSVGFile);
    }



    private void writeStateConnectionFormAnotherAcitvity(PrintWriter writer, String activity, int nextStateID, String label) {
        writer.println(activity + " [fontcolor = teal,color = teal];");
        writer.print(activity + " -> state_" + nextStateID);
        writer.println(" [label = \"   " + label + "   \"," + "fontcolor = teal,color = teal];");
    }
}