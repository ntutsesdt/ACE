package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.state_graph.StateGraph;
import tw.edu.ntut.sdtlab.crawler.ace.util.NodeAttribute;
import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import tw.edu.ntut.sdtlab.crawler.ace.util.Printer;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class EquivalentStateStrategy implements Serializable {
    private List<String> excludeAttribute;
    private int maxListGridSizeThreshold = 0;
    protected int totalEquivalentStateCount = 0;
    protected GUIState eqGUIState;
    protected boolean isMarkEquivalent = false;

    public abstract boolean isEquivalent(GUIState newState, StateGraph stateGraph, GUIState fromState) throws MultipleListOrGridException;

    public abstract boolean getIsMarkEquivalent();

    public abstract void setIsMarkEquivalent(boolean isMarkEquivalent);

    public GUIState getEquivalentState() {
        return this.eqGUIState;
    }

    public EquivalentStateStrategy() {
        this.maxListGridSizeThreshold = Config.MAX_LIST_GRID_SIZE_THRESHOLD;
        this.iniExcludeAttribute();
    }


    protected boolean areAttributesEqual(Element element, Element newElement) {
        if (element.attributes().size() != newElement.attributes().size())
            return false;
        for (Object attribute : element.attributes()) {
            String key = ((DefaultAttribute) attribute).getName();
            if (this.excludeAttribute.contains(key))
                continue;
            if (!element.attributeValue(key).equals(newElement.attributeValue(key)))
                return false;
        }
        return true;
    }

    private void iniExcludeAttribute() {
        this.excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE) {
            excludeAttribute.add(NodeAttribute.Bounds);
        }
    }

    @Deprecated
    protected boolean areEquivalent(Element element1, Element element2, HashMap<String, Integer> lengthMap, GUIState state,
                                    GUIState currentState, String checkType) {

        for (int i = 0; i < Math.max(element1.elements().size(), element2.elements().size()); i++) {
            if ((i == element1.elements().size()) || (i == element2.elements().size()))
                return false;
            if (((element1.attributeValue(NodeAttribute.Class) != null) && (element2.attributeValue(NodeAttribute.Class) != null))
                    && (element1.attributeValue(NodeAttribute.Class).equals(checkType))
                    && (element2.attributeValue(NodeAttribute.Class).equals(checkType))) {

                if (lengthMap.get(getCurrentXpath(getStringList(element1))) == null)
                    lengthMap.put(getCurrentXpath(getStringList(element1)), 0);

                if (!areEquivalent((Element) element1.elements().get(i), (Element) element2.elements().get(i), lengthMap, state,
                        currentState, checkType)) {
                    return false;
                } else {
                    int lengthValue = lengthMap.get(getCurrentXpath(getStringList(element1))) + 1;
                    lengthMap.put(getCurrentXpath(getStringList(element1)), lengthValue);
                    if (lengthMap.get(getCurrentXpath(getStringList(element1))) >= this.maxListGridSizeThreshold) {
                        this.totalEquivalentStateCount++;
                        state.setIsEquivalentState(true);
                        if (!state.getImagelist().contains(currentState.getImagelist().get(0)))
                            state.addImage(currentState.getImagelist().get(0));
                        state.increaseEquivalentStateCount();
                        state.setIsListAndGrid(true);
                        return true;
                    }
                }
            } else {
                if (!areEquivalent((Element) element1.elements().get(i), (Element) element2.elements().get(i), lengthMap, state,
                        currentState, checkType))
                    return false;
            }
        }
        return true;
    }

    private ArrayList<String> getStringList(Element element) {
        ArrayList<String> stringList = new ArrayList<String>();
        ArrayList<String> resultList = new ArrayList<String>();
        while (!element.isRootElement()) {
            stringList.add(element.getName() + "[@'" + element.attributeValue(NodeAttribute.Class) + "']"
                    + "[@'index = " + element.attributeValue(NodeAttribute.Index) + "']");
            if (element.getParent() != null)
                element = element.getParent();
        }
        for (String s : stringList) { // reverse list
            resultList.add(0, s);
        }
        return resultList;
    }

    private String getCurrentXpath(ArrayList<String> elementString) {
        String str = "//";
        boolean first = true;
        for (String s : elementString) {
            if (first)
                str = str + s;
            else
                str = str + "/" + s;
            first = false;
        }
        return str;
    }

    protected void markEquivalentState(GUIState compareState, GUIState newState) {
        Printer printer = new Printer();
        printer.equivalentState(this.getClass().getName());
        this.eqGUIState = compareState;
        this.eqGUIState.setIsEquivalentState(true);
        newState.setIsEquivalentState(true);
        this.eqGUIState.addImage(newState.getImagelist().get(0));
        this.eqGUIState.increaseEquivalentStateCount();
        this.isMarkEquivalent = true;
    }
}
