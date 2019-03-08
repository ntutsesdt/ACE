package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;


public class ExactlyEquivalentStrategy extends EquivalentStateStrategy {
    private GUIState eqGUIState;

    public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) {
        for (GUIState compareState : stateGraph.getAllStates()) {
            if (compareState.isExactlyEquivalentTo(newState)) {
                this.eqGUIState = compareState;
                return true;
            }
        }
        return false;
    }

    @Override
    public GUIState getEquivalentState() {
        return this.eqGUIState;
    }

    @Override
    public boolean getIsMarkEquivalent() {
        return isMarkEquivalent;
    }

    @Override
    public void setIsMarkEquivalent(boolean isMarkEquivalent) {
        this.isMarkEquivalent = isMarkEquivalent;
    }
}