package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;

public class CompositeActivityEquivalentStrategy extends CompositeStrategy {

    public CompositeActivityEquivalentStrategy() {
        this.eqStateStrategy.add(new ExactlyEquivalentStrategy());
        this.eqStateStrategy.add(new ActivityEquivalentStrategy());
    }

    @Override
    public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) throws MultipleListOrGridException {
        if (eqCompare(newState, stateGraph, fromState)) {
            return true;
        } else
            return false;
    }

    @Override
    public boolean getIsMarkEquivalent() {
        return isMarkEquivalent();
    }

    @Override
    public void setIsMarkEquivalent(boolean isMarkEquivalent) {
        initIsMarkEquivalent(isMarkEquivalent);
    }
}
