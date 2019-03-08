package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class EachChoiceEquivalentStrategy extends EquivalentStateStrategy {

    public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) {
        List<Element> newStateChoiceWidgets = newState.getChoiceWidgets();
        if (newStateChoiceWidgets.size() > 0) {
            // 找出State Graph中所有結構相似的State
            List<GUIState> similarStates = getSimilarStates(newState, stateGraph);
            // 再依序比較 choice widget(Switch button, check box, spinner等)的狀態是否在State Graph中的任一State上出現過
            for (int i = 0; i < newStateChoiceWidgets.size(); i++) {
                boolean isSame = false;
                for (GUIState compareState : similarStates) {
                    String newStateWidgetClass = newStateChoiceWidgets.get(i).attributeValue("class");
                    if (newStateWidgetClass.equals("android.widget.Switch")
                            || newStateWidgetClass.equals("android.widget.ToggleButton")
                            || newStateWidgetClass.equals("android.widget.CheckBox")
                            || newStateWidgetClass.equals("android.widget.RadioButton")) {
                        if (newStateChoiceWidgets.get(i).attributeValue("checked").equals(compareState.getChoiceWidgets().get(i).attributeValue("checked"))) {
                            isSame = true;
                        }
                    }
                    else if (newStateWidgetClass.equals("android.widget.Spinner")) {
                        if( newStateChoiceWidgets.get(i).elements().size() > 0) {
                            Element newStateSpinnerTextViewElement = (Element) newStateChoiceWidgets.get(i).elements().get(0);
                            Element compareStateSpinnerTextViewElement = (Element) compareState.getChoiceWidgets().get(i).elements().get(0);
                            if (newStateSpinnerTextViewElement.attributeValue("text").equals(compareStateSpinnerTextViewElement.attributeValue("text"))) {
                                isSame = true;
                            }
                        }
                    }
                }
                // 若是發現任一未出現過的狀態，即為新狀態，中斷For
                if (!isSame)
                    return false;
            }
            this.markEquivalentState(fromState, newState);
            stateGraph.increaseTotalEachChoiceEquivalentStateCount();
            return true;
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

    private List<GUIState> getSimilarStates(GUIState newState, StateGraph stateGraph) {
        ArrayList<GUIState> similarStates = new ArrayList<>();
        for (GUIState compareState : stateGraph.getAllStates()) {
            if (newState.getActivityName().equals(compareState.getActivityName())) {    // Activity Name相同
                if (newState.isSameView(compareState)) {   // 兩者架構相似
                    similarStates.add(compareState);
                }
            }
        }
        return similarStates;
    }
}
