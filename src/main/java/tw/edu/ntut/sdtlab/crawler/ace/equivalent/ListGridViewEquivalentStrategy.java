package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.util.NodeAttribute;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import java.util.ArrayList;
import java.util.List;


public class ListGridViewEquivalentStrategy extends EquivalentStateStrategy {
    public boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) throws MultipleListOrGridException {
        if (newState.containListOrGrid()) {
            for (GUIState compareState : stateGraph.getAllStates()) {
                if (this.satisfyEquivalentPrecodition(compareState, newState)) {
                    boolean listEquivalent = !newState.containList() || this.isListOrGridEquivalent(compareState, newState, true);
                    boolean gridEquivalent = !newState.containGrid() || this.isListOrGridEquivalent(compareState, newState, false);
                    if (listEquivalent && gridEquivalent) {
                        this.markEquivalentState(compareState, newState);
                        stateGraph.increaseTotalListGridEquivalentStateCount();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isListOrGridEquivalent(GUIState compareState, GUIState newState, boolean isList) throws MultipleListOrGridException {
        if (this.headTailEquivalent(compareState, newState, isList))
            return true;
        else if (this.contentSensitiveEquivalent(compareState, newState, isList))
            return true;
        else if (this.listGridSizeEquivalent(compareState, newState, isList))
            return true;
        else
            return false;
    }

    private boolean headTailEquivalent(GUIState compareState, GUIState newState, boolean isList) throws MultipleListOrGridException {
        final int HEAD_TAIL_EQUIVALENT_THRESHOLD = Config.HEAD_TAIL_LIST_GRID_SIZE_THRESHOLD;
        final String XPATH = isList ? "//node[@class='android.widget.ListView']" : "//node[@class='android.widget.GridView']";
        List compareStateSubElements = this.getSubElementsByXPath(compareState, XPATH);
        List newStateSubElements = this.getSubElementsByXPath(newState, XPATH);

        if (compareStateSubElements.size() >= HEAD_TAIL_EQUIVALENT_THRESHOLD && newStateSubElements.size() >= HEAD_TAIL_EQUIVALENT_THRESHOLD) {
            // compare from head
            boolean headTheSame = true;
            for (int i = 0; i < HEAD_TAIL_EQUIVALENT_THRESHOLD; i++) {
                if (!this.areAttributesEquals((Element) compareStateSubElements.get(i), (Element) newStateSubElements.get(i))) {
                    headTheSame = false;
                    break;
                }
            }
            // compare from tail
            boolean tailTheSame = true;
            for (int i = 0; i < HEAD_TAIL_EQUIVALENT_THRESHOLD; i++) {
                int compareIndex = compareStateSubElements.size() - 1 - i;
                int newStateIndex = newStateSubElements.size() - 1 - i;
                if (!this.areAttributesEquals((Element) compareStateSubElements.get(compareIndex), (Element) newStateSubElements.get(newStateIndex))) {
                    tailTheSame = false;
                    break;
                }
            }
            return headTheSame || tailTheSame;
        } else {
            if (compareStateSubElements.size() == newStateSubElements.size()) {
                for (int i = 0; i < compareStateSubElements.size(); i++) {
                    if (!this.areAttributesEquals((Element) compareStateSubElements.get(i), (Element) newStateSubElements.get(i)))
                        return false;
                }
                return true;
            }
            return false;
        }
    }

    private List getSubElementsByXPath(GUIState state, final String XPATH) {
        List elements = new ArrayList();
        for (Object node : state.contentClone().selectNodes(XPATH)) {
            elements.addAll(((Element) node).elements());
        }
        return elements;
    }

    private boolean contentSensitiveEquivalent(GUIState compareState, GUIState newState, boolean isList) throws MultipleListOrGridException {
        final int CONTENT_SENSITIVE_EQUIVALENT_THRESHOLD = Config.CONTENT_SENSITIVE_LIST_GRID_SIZE_THRESHOLD;
        final String XPATH = isList ? "//node[@class='android.widget.ListView']" : "//node[@class='android.widget.GridView']";
        List compareStateSubElements = this.getSubElementsByXPath(compareState, XPATH);
        List newStateSubElements = this.getSubElementsByXPath(newState, XPATH);
        int count = 0;
        for (Object compareNode : compareStateSubElements) {
            for (Object newStateNode : newStateSubElements) {
                if (this.areAttributesEquals((Element) compareNode, (Element) newStateNode)) {
                    count += 1;
                    break;
                }
            }
            if (count >= CONTENT_SENSITIVE_EQUIVALENT_THRESHOLD)
                return true;
        }
        return false;
    }

    private boolean listGridSizeEquivalent(GUIState compareState, GUIState newState, boolean isList) throws MultipleListOrGridException {
        final int MAX_LIST_GRID_SIZE_THRESHOLD = Config.MAX_LIST_GRID_SIZE_THRESHOLD;
        int compareSize = isList ? compareState.getListSize() : compareState.getGridSize();
        int newStateSize = isList ? newState.getListSize() : newState.getGridSize();
        if (compareSize >= MAX_LIST_GRID_SIZE_THRESHOLD && newStateSize >= MAX_LIST_GRID_SIZE_THRESHOLD)
            return true;
        return false;
    }

    private boolean satisfyEquivalentPrecodition(GUIState compareState, GUIState newState) throws MultipleListOrGridException {
        if (compareState.getActivityName().equals(newState.getActivityName())) {
            if (compareState.areTheSameExcludeListAndGrid(newState)) {
                if (newState.getListSize() != compareState.getListSize() || newState.getGridSize() != compareState.getGridSize()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public GUIState getEquivalentState() {
        return this.eqGUIState;
    }

    private boolean areAttributesEquals(Element element, Element newElement) {
        List<String> excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        excludeAttribute.add(NodeAttribute.Index);
        excludeAttribute.add(NodeAttribute.Bounds);
        if (element.attributes().size() != newElement.attributes().size())
            return false;
        for (Object attribute : element.attributes()) {
            String key = ((DefaultAttribute) attribute).getName();
            if (excludeAttribute.contains(key))
                continue;
            if (!element.attributeValue(key).equals(newElement.attributeValue(key)))
                return false;
        }
        return true;
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