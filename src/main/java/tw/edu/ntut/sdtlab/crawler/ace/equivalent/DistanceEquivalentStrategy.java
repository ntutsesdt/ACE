package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;

import java.util.HashMap;
import java.util.Map;


public class DistanceEquivalentStrategy extends EquivalentStateStrategy {
    private HashMap<Integer, GUIState> eqStateMap;

    private Map<Integer, Integer> calculateAllDistance(GUIState newState, StateGraph stateGraph) {
        Map<Integer, Integer> distanceMap = new HashMap<>();
        this.eqStateMap = new HashMap<>();
        for (GUIState state : stateGraph.getAllStates()) {
            if (newState.isSimilarTo(state)) {
                int distance = newState.calculateDistance(state);
                if (!distanceMap.containsKey(distance))
                    eqStateMap.put(distance, state);
                int occurs = distanceMap.containsKey(distance) ? distanceMap.get(distance) + 1 : 1;
                distanceMap.put(distance, occurs);
            }
        }
        return distanceMap;
    }

    @Override
    public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) {
        Map<Integer, Integer> distanceMap = this.calculateAllDistance(newState, stateGraph);
        for (int key : distanceMap.keySet()) {
            if (distanceMap.get(key) >= Config.MAX_EQUAL_DISTANCE_THRESHOLD) {
                this.markEquivalentState(this.eqStateMap.get(key), newState);
                stateGraph.increaseTotalDistanceEquivalentStateCount();
                return true;
            }
        }
        return false;
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