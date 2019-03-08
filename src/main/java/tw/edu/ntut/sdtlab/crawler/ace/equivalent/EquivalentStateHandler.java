package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.edu.ntut.sdtlab.crawler.ace.exception.MultipleListOrGridException;
import tw.edu.ntut.sdtlab.crawler.ace.util.Config;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;


import tw.edu.ntut.sdtlab.crawler.ace.state_graph.GUIState;
import tw.edu.ntut.sdtlab.crawler.ace.util.NodeAttribute;

public class EquivalentStateHandler implements Equivalent {
    protected int maxListGridSizeThreshold = 0;
    protected int maxEqualDistanceTreshold = 0;
    protected int totalDistanceEquivalentStateCount = 0;
    protected int totalListGridEquivalentStateCount = 0;
    private List<String> excludeAttribute;

    public EquivalentStateHandler() {
        maxListGridSizeThreshold = Config.MAX_LIST_GRID_SIZE_THRESHOLD;
        maxEqualDistanceTreshold = Config.MAX_EQUAL_DISTANCE_THRESHOLD;
        this.iniExcludeAttribute();
    }

    private void iniExcludeAttribute() {
        this.excludeAttribute = new ArrayList<>();
        excludeAttribute.add(NodeAttribute.Focused);
        excludeAttribute.add(NodeAttribute.Selected);
        if (Config.IGNORE_BOUNDS_ATTRIBUTE) {
            excludeAttribute.add(NodeAttribute.Bounds);
        }
    }

    @Override
    public GUIState getStateInStateList(List<GUIState> stateList, GUIState state) throws MultipleListOrGridException {
        // TODO Auto-generated method stub
        Document content = null, targetContent = null;
        targetContent = state.contentClone();
        Element element = targetContent.getRootElement();
        Distance distance = new Distance();
        HashMap<String, Integer> distanceMap = new HashMap<String, Integer>();
        int distanceValue = 0;
        int targetDistance = 0;
        for (GUIState s : stateList) {
            if (state.getActivityName().equals(s.getActivityName())) {
                content = s.contentClone();
                Element e = content.getRootElement();
                if (state.isExactlyEquivalentTo(s))
                    return s;
            }
        }
        element = targetContent.getRootElement();
        for (GUIState s : stateList) {
            if (state.getActivityName().equals(s.getActivityName())) {
                content = s.contentClone();
                Element e = content.getRootElement();
                HashMap<String, Integer> lengthMap = new HashMap<String, Integer>();
                checkContainsListViewOrGridView(state, element);
                if (state.containListOrGrid()) {
                    if (allAttributesAreTheSameExcludeListAndGrid(e, element)) {
                        if (areEqual(e, element, lengthMap, s, state)) {
                            if (state.isDynamicListAndGrid())
                                return s;
                        } else
                            return state;
                    }
                }
            }
        }
        element = targetContent.getRootElement();
        for (GUIState s : stateList) {
            if (state.getActivityName().equals(s.getActivityName())) {
                content = s.contentClone();
                Element e = content.getRootElement();

                if (checkTwoStateIsSimilarState(e, element)) {
                    distance.setDistance(0);
                    getComponentDistance(e, element, distance);
                    if (distanceMap.get(String.valueOf(distance.distance)) == null) {
                        distanceMap.put(String.valueOf(distance.distance), 1);

                        if (distanceMap.get(String.valueOf(distance.distance)) >= maxEqualDistanceTreshold) {
                            targetDistance = distance.getDistance();
                            break;
                        }
                    } else {
                        distanceValue = distanceMap.get(String.valueOf(distance.distance)) + 1;
                        if (distanceValue >= maxEqualDistanceTreshold) {
                            targetDistance = distance.getDistance();
                            break;
                        } else {
                            //distanceValue++;
                            distanceMap.put(String.valueOf(distance.distance), distanceValue);
                        }

                    }
                }
            }
        }
        element = targetContent.getRootElement();
        for (GUIState s : stateList) {
            if (state.getActivityName().equals(s.getActivityName())) {
                content = s.contentClone();
                Element e = content.getRootElement();
                distance.setDistance(0);
                if (checkTwoStateIsSimilarState(e, element)) {
                    getComponentDistance(e, element, distance);
                    if (distance.getDistance() == targetDistance) {
                        return s;
                    }
                }
            }
        }
        return state;
    }

    public boolean areTheSame(Element element1, Element element2) {
        if (!isAttributeEqual(element1, element2)) {
            return false;
        }
        if (element1.elements().size() != element2.elements().size()) {
            return false;
        }
        for (int i = 0; i < element1.elements().size(); i++) {
            Element oldElement = (Element) element1.elements().get(i);
            Element newElement = (Element) element2.elements().get(i);
            if (!areTheSame(oldElement, newElement)) {
                return false;
            }
        }
        return true;
    }

    public boolean allAttributesAreTheSameExcludeListAndGrid(Element element1, Element element2) {
        if (element1.attributeValue(NodeAttribute.Class) != null && element2.attributeValue(NodeAttribute.Class) != null
                && (!element1.attributeValue(NodeAttribute.Class).equals(NodeAttribute.ListView)
                && !element2.attributeValue(NodeAttribute.Class).equals(NodeAttribute.ListView)
                && !element1.attributeValue(NodeAttribute.Class).equals(NodeAttribute.GridView)
                && !element2.attributeValue(NodeAttribute.Class).equals(NodeAttribute.GridView))) {
            for (int i = 0; i < max(element1.elements().size(), element2.elements().size()); i++) {
                if (!isAttributeEqual(element1, element2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean areEqual(Element element1, Element element2, HashMap<String, Integer> lengthMap, GUIState state, GUIState currentState) throws MultipleListOrGridException {
        // check whether list or grid can dynamic add row
        if (!isAttributeEqual(element1, element2)) {
            return false;
        }

        for (int i = 0; i < max(element1.elements().size(), element2.elements().size()); i++) {
            if (i == ((Element) element1).elements().size() || i == ((Element) element2).elements().size())
                return false;
            if (element1.attributeValue(NodeAttribute.Class) != null && element2.attributeValue(NodeAttribute.Class) != null &&
                    (element1.attributeValue(NodeAttribute.Class).equals(NodeAttribute.ListView) && element2.attributeValue(NodeAttribute.Class).equals(NodeAttribute.ListView)
                            || element1.attributeValue(NodeAttribute.Class).equals(NodeAttribute.GridView) && element2.attributeValue(NodeAttribute.Class).equals(NodeAttribute.GridView))) {

                if (state.getListSize() != currentState.getListSize() && state.getGridSize() != currentState.getGridSize()) {
                    currentState.setIsDynamicListAndGrid(true);
                }
                if (currentState.isDynamicListAndGrid()) {
                    if (lengthMap.get(getCurrentXpath(element1, getStringList(element1))) == null)
                        lengthMap.put(getCurrentXpath(element1, getStringList(element1)), 0);

                    if (!areEqual((Element) element1.elements().get(i), (Element) element2.elements().get(i), lengthMap, state, currentState)) {
                        return false;
                    } else {
                        int lengthValue = lengthMap.get(getCurrentXpath(element1, getStringList(element1))) + 1;
                        lengthMap.put(getCurrentXpath(element1, getStringList(element1)), lengthValue);
                        if (lengthMap.get(getCurrentXpath(element1, getStringList(element1))) >= maxListGridSizeThreshold) {
                            this.totalListGridEquivalentStateCount++;
                            state.setIsEquivalentState(true);
                            if (!state.getImagelist().contains(currentState.getImagelist().get(0)))
                                state.addImage(currentState.getImagelist().get(0));
                            state.increaseEquivalentStateCount();
                            state.setIsListAndGrid(true);
                            return true;
                        }
                    }
                } else {
                    return false;
                }

            } else {
                if (!areEqual((Element) element1.elements().get(i), (Element) element2.elements().get(i), lengthMap, state, currentState)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected int min(int size1, int size2) {
        if (size1 < size2)
            return size1;
        return size2;
    }

    protected int max(int size1, int size2) {
        if (size1 > size2)
            return size1;
        return size2;
    }

    protected boolean isAttributeEqual(Element oldElement, Element newElement) {
        int oldAttrSize = oldElement.attributes().size(),
                newAttrSize = newElement.attributes().size();
        if (oldAttrSize != newAttrSize) {
            return false;
        }
        String oldValue = null, newValue = null;
        for (int i = 0; i < oldAttrSize; i++) {
            String oldAttributeName = ((Attribute) oldElement.attributes().get(i)).getName();
            String newAttributeName = ((Attribute) newElement.attributes().get(i)).getName();
            if (this.excludeAttribute.contains(oldAttributeName) || this.excludeAttribute.contains(newAttributeName)) {
                continue;
            }
            oldValue = ((Node) oldElement.attributes().get(i)).getText();
            newValue = ((Node) newElement.attributes().get(i)).getText();
            if (!newValue.equals(oldValue)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> getStringList(Element element) {
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

    public String getCurrentXpath(Element element, ArrayList<String> elementString) {
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


    public void getComponentDistance(Element element1, Element element2, Distance distance) {
        if (!isAttributeEqual(element1, element2)) {
            distance.setDistance(distance.getDistance() + 1);
        }
        if (element1.elements().size() != element2.elements().size())
            distance.setDistance(distance.getDistance() + Math.abs(element1.elements().size() - element2.elements().size()));
        for (int i = 0; i < min(element1.elements().size(), element2.elements().size()); i++) {
            getComponentDistance((Element) element1.elements().get(i), (Element) element2.elements().get(i), distance);
        }
    }

    private void checkContainsListViewOrGridView(GUIState state, Element element) {
        String className = element.attributeValue(NodeAttribute.Class);
        boolean classNotNull = (className != null);
        if (classNotNull) {
            boolean isListOrGrid = (className.equals(NodeAttribute.ListView) || className.equals(NodeAttribute.GridView));
            if (isListOrGrid) {
                if (className.equals(NodeAttribute.ListView))
                    state.setListSize(element.elements().size());
                else
                    state.setGridSize(element.elements().size());
                state.setIsListAndGrid(true);
            }
        }

        for (int i = 0; i < element.elements().size(); i++) {
            checkContainsListViewOrGridView(state, (Element) element.elements().get(i));
        }
    }

    // state = currentState
    @Override
    public boolean checkStateIsInStateList(List<GUIState> stateList, GUIState newState) throws MultipleListOrGridException {
        int distanceValue = 0;
        Distance distance = new Distance();
        Document targetDocument = null, content = null;
        targetDocument = newState.contentClone();
        Element newStateElement = targetDocument.getRootElement();

        HashMap<String, Integer> componentMap = new HashMap<String, Integer>();

        for (GUIState s : stateList) {
            if (newState.getActivityName().equals(s.getActivityName())) {
                if (newState.isExactlyEquivalentTo(s))
                    return true;
            }
        }

        newStateElement = targetDocument.getRootElement();
        for (GUIState s : stateList) {
            if (newState.getActivityName().equals(s.getActivityName())) {
                content = s.contentClone();
                Element e = content.getRootElement();
                HashMap<String, Integer> lengthMap = new HashMap<String, Integer>();
                if (areEqual(e, newStateElement, lengthMap, s, newState))
                    return true;
            }
        }
        newStateElement = targetDocument.getRootElement();
        for (GUIState s : stateList) {
            if (newState.getActivityName().equals(s.getActivityName())) {
                content = s.contentClone();
                Element e = content.getRootElement();
                if (checkTwoStateIsSimilarState(e, newStateElement)) {
                    distance.setDistance(0);
                    getComponentDistance(e, newStateElement, distance);
                    if (componentMap.get(String.valueOf(distance.distance)) == null) {
                        componentMap.put(String.valueOf(distance.distance), 1);

                        if (componentMap.get(String.valueOf(distance.distance)) >= maxEqualDistanceTreshold) {
                            this.totalDistanceEquivalentStateCount++;
                            int targetDistance = distance.getDistance(); //
                            for (GUIState temp : stateList) {
                                if (newState.getActivityName().equals(temp.getActivityName())) {
                                    content = temp.contentClone();
                                    Element tempE = content.getRootElement();
                                    if (checkTwoStateIsSimilarState(tempE, newStateElement)) {
                                        distance.setDistance(0);
                                        getComponentDistance(tempE, newStateElement, distance);
                                        if (distance.getDistance() == targetDistance) {
                                            temp.setIsEquivalentState(true);
                                            temp.addImage(newState.getImagelist().get(0));
                                            temp.increaseEquivalentStateCount();
                                            return true;
                                            //break;
                                        }
                                    }
                                }
                            }
                            return true;
                        }

                    } else {
                        distanceValue = componentMap.get(String.valueOf(distance.distance)) + 1;
                        if (distanceValue >= maxEqualDistanceTreshold) {
                            this.totalDistanceEquivalentStateCount++;
                            int targetDistance = distance.getDistance(); //
                            for (GUIState temp : stateList) {
                                if (newState.getActivityName().equals(temp.getActivityName())) {
                                    content = temp.contentClone();
                                    Element tempE = content.getRootElement();
                                    if (checkTwoStateIsSimilarState(tempE, newStateElement)) {
                                        distance.setDistance(0);
                                        getComponentDistance(tempE, newStateElement, distance);
                                        if (distance.getDistance() == targetDistance) {
                                            temp.setIsEquivalentState(true);
                                            temp.addImage(newState.getImagelist().get(0));
                                            temp.increaseEquivalentStateCount();
                                            return true;
                                        }
                                    }
                                }
                            }
                            return true;
                        } else {
                            componentMap.put(String.valueOf(distance.distance), distanceValue);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkTwoStateIsEquivalent(GUIState state1, GUIState state2) {
        return state1.isExactlyEquivalentTo(state2);
    }


    protected boolean checkTwoStateIsSimilarState(Element element1, Element element2) {
        if (element1.elements().size() != element2.elements().size())
            return false;
        for (int i = 0; i < element1.elements().size(); i++) {
            if (!checkTwoStateIsSimilarState((Element) element1.elements().get(i), (Element) element2.elements().get(i)))
                return false;
        }
        return true;
    }

    @Override
    public int getTotalDistanceEquivalentStateCount() {
        // TODO Auto-generated method stub
        return this.totalDistanceEquivalentStateCount;
    }

    @Override
    public int getTotalListGridEquivalentStateCount() {
        // TODO Auto-generated method stub
        return this.totalListGridEquivalentStateCount;
    }

}
