package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class CompositeStrategy extends EquivalentStateStrategy implements Serializable {
    protected List<EquivalentStateStrategy> eqStateStrategy = null;

    protected CompositeStrategy() {
        this.eqStateStrategy = new ArrayList<>();
    }

    public abstract boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) throws MultipleListOrGridException;

    public abstract boolean getIsMarkEquivalent();

    public abstract void setIsMarkEquivalent(boolean isMarkEquivalent);

    protected boolean eqCompare(GUIState newState, StateGraph stateGraph, GUIState fromState) throws MultipleListOrGridException {
        for (EquivalentStateStrategy eqState : eqStateStrategy) {
            if (eqState.isEquivalent(newState, stateGraph, fromState)) {
                this.eqGUIState = eqState.getEquivalentState();
                return true;
            }
        }
        return false;
    }

    protected boolean isMarkEquivalent() {
        for (EquivalentStateStrategy eqState : eqStateStrategy) {
            if (eqState.getIsMarkEquivalent())
                return true;
        }
        return false;
    }

    protected void initIsMarkEquivalent(boolean isMarkEquivalent) {
        for (EquivalentStateStrategy eqState : eqStateStrategy) {
            eqState.setIsMarkEquivalent(isMarkEquivalent);
        }
    }
}
