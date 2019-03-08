package tw.edu.ntut.sdtlab.crawler.ace.equivalent;


import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;

import java.util.List;

public interface Equivalent {
    boolean checkTwoStateIsEquivalent(GUIState state1, GUIState state2);

    GUIState getStateInStateList(List<GUIState> stateList, GUIState state) throws MultipleListOrGridException;

    boolean checkStateIsInStateList(List<GUIState> stateList, GUIState state) throws MultipleListOrGridException;

    int getTotalDistanceEquivalentStateCount();

    int getTotalListGridEquivalentStateCount();
}
