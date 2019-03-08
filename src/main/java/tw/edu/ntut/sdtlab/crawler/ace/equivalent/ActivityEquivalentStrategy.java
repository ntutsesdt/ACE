package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;


public class ActivityEquivalentStrategy extends EquivalentStateStrategy{
    public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState){
        for(GUIState compareState : stateGraph.getAllStates()) {
            if(newState.getActivityName().equals(compareState.getActivityName())) {
                super.eqGUIState = compareState;
                this.markEquivalentState(compareState, newState);
                return true;
            }

        }
        return false;
    }

    @Override
    public GUIState getEquivalentState(){
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