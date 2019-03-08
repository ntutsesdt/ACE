package tw.edu.ntut.sdtlab.crawler.ace.crawler;

import org.dom4j.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MockElement implements Element{

    public Object clone() {
        return this.clone();
    }

    @Override
    public QName getQName() {
        return null;
    }

    @Override
    public void setQName(QName qName) {

    }

    @Override
    public Namespace getNamespace() {
        return null;
    }

    @Override
    public QName getQName(String s) {
        return null;
    }

    @Override
    public Namespace getNamespaceForPrefix(String s) {
        return null;
    }

    @Override
    public Namespace getNamespaceForURI(String s) {
        return null;
    }

    @Override
    public List getNamespacesForURI(String s) {
        return null;
    }

    @Override
    public String getNamespacePrefix() {
        return null;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getQualifiedName() {
        return null;
    }

    @Override
    public List additionalNamespaces() {
        return null;
    }

    @Override
    public List declaredNamespaces() {
        return null;
    }

    @Override
    public Element addAttribute(String s, String s1) {
        return null;
    }

    @Override
    public Element addAttribute(QName qName, String s) {
        return null;
    }

    @Override
    public Element addComment(String s) {
        return null;
    }

    @Override
    public Element addCDATA(String s) {
        return null;
    }

    @Override
    public Element addEntity(String s, String s1) {
        return null;
    }

    @Override
    public Element addNamespace(String s, String s1) {
        return null;
    }

    @Override
    public Element addProcessingInstruction(String s, String s1) {
        return null;
    }

    @Override
    public Element addProcessingInstruction(String s, Map map) {
        return null;
    }

    @Override
    public Element addText(String s) {
        return null;
    }

    @Override
    public void add(Attribute attribute) {

    }

    @Override
    public void add(CDATA cdata) {

    }

    @Override
    public void add(Entity entity) {

    }

    @Override
    public void add(Text text) {

    }

    @Override
    public void add(Namespace namespace) {

    }

    @Override
    public boolean remove(Attribute attribute) {
        return false;
    }

    @Override
    public boolean remove(CDATA cdata) {
        return false;
    }

    @Override
    public boolean remove(Entity entity) {
        return false;
    }

    @Override
    public boolean remove(Namespace namespace) {
        return false;
    }

    @Override
    public boolean remove(Text text) {
        return false;
    }

    @Override
    public boolean supportsParent() {
        return false;
    }

    @Override
    public Element getParent() {
        return null;
    }

    @Override
    public void setParent(Element element) {

    }

    @Override
    public Document getDocument() {
        return null;
    }

    @Override
    public void setDocument(Document document) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean hasContent() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(String s) {

    }

    @Override
    public String getTextTrim() {
        return null;
    }

    @Override
    public String getStringValue() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getPath(Element element) {
        return null;
    }

    @Override
    public String getUniquePath() {
        return null;
    }

    @Override
    public String getUniquePath(Element element) {
        return null;
    }

    @Override
    public String asXML() {
        return null;
    }

    @Override
    public void write(Writer writer) throws IOException {

    }

    @Override
    public short getNodeType() {
        return 0;
    }

    @Override
    public String getNodeTypeName() {
        return null;
    }

    @Override
    public Node detach() {
        return null;
    }

    @Override
    public List selectNodes(String s) {
        return null;
    }

    @Override
    public Object selectObject(String s) {
        return null;
    }

    @Override
    public List selectNodes(String s, String s1) {
        return null;
    }

    @Override
    public List selectNodes(String s, String s1, boolean b) {
        return null;
    }

    @Override
    public Node selectSingleNode(String s) {
        return null;
    }

    @Override
    public String valueOf(String s) {
        return null;
    }

    @Override
    public Number numberValueOf(String s) {
        return null;
    }

    @Override
    public boolean matches(String s) {
        return false;
    }

    @Override
    public XPath createXPath(String s) throws InvalidXPathException {
        return null;
    }

    @Override
    public Node asXPathResult(Element element) {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object o) {

    }

    @Override
    public List attributes() {
        return null;
    }

    @Override
    public void setAttributes(List list) {

    }

    @Override
    public int attributeCount() {
        return 0;
    }

    @Override
    public Iterator attributeIterator() {
        return null;
    }

    @Override
    public Attribute attribute(int i) {
        return null;
    }

    @Override
    public Attribute attribute(String s) {
        return null;
    }

    @Override
    public Attribute attribute(QName qName) {
        return null;
    }

    @Override
    public String attributeValue(String s) {
        return null;
    }

    @Override
    public String attributeValue(String s, String s1) {
        return null;
    }

    @Override
    public String attributeValue(QName qName) {
        return null;
    }

    @Override
    public String attributeValue(QName qName, String s) {
        return null;
    }

    @Override
    public void setAttributeValue(String s, String s1) {

    }

    @Override
    public void setAttributeValue(QName qName, String s) {

    }

    @Override
    public Element element(String s) {
        return null;
    }

    @Override
    public Element element(QName qName) {
        return null;
    }

    @Override
    public List elements() {
        return null;
    }

    @Override
    public List elements(String s) {
        return null;
    }

    @Override
    public List elements(QName qName) {
        return null;
    }

    @Override
    public Iterator elementIterator() {
        return null;
    }

    @Override
    public Iterator elementIterator(String s) {
        return null;
    }

    @Override
    public Iterator elementIterator(QName qName) {
        return null;
    }

    @Override
    public boolean isRootElement() {
        return false;
    }

    @Override
    public boolean hasMixedContent() {
        return false;
    }

    @Override
    public boolean isTextOnly() {
        return false;
    }

    @Override
    public void appendAttributes(Element element) {

    }

    @Override
    public Element createCopy() {
        return null;
    }

    @Override
    public Element createCopy(String s) {
        return null;
    }

    @Override
    public Element createCopy(QName qName) {
        return null;
    }

    @Override
    public String elementText(String s) {
        return null;
    }

    @Override
    public String elementText(QName qName) {
        return null;
    }

    @Override
    public String elementTextTrim(String s) {
        return null;
    }

    @Override
    public String elementTextTrim(QName qName) {
        return null;
    }

    @Override
    public Node getXPathResult(int i) {
        return null;
    }

    @Override
    public Node node(int i) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public int indexOf(Node node) {
        return 0;
    }

    @Override
    public int nodeCount() {
        return 0;
    }

    @Override
    public Element elementByID(String s) {
        return null;
    }

    @Override
    public List content() {
        return null;
    }

    @Override
    public Iterator nodeIterator() {
        return null;
    }

    @Override
    public void setContent(List list) {

    }

    @Override
    public void appendContent(Branch branch) {

    }

    @Override
    public void clearContent() {

    }

    @Override
    public List processingInstructions() {
        return null;
    }

    @Override
    public List processingInstructions(String s) {
        return null;
    }

    @Override
    public ProcessingInstruction processingInstruction(String s) {
        return null;
    }

    @Override
    public void setProcessingInstructions(List list) {

    }

    @Override
    public Element addElement(String s) {
        return null;
    }

    @Override
    public Element addElement(QName qName) {
        return null;
    }

    @Override
    public Element addElement(String s, String s1) {
        return null;
    }

    @Override
    public boolean removeProcessingInstruction(String s) {
        return false;
    }

    @Override
    public void add(Node node) {

    }

    @Override
    public void add(Comment comment) {

    }

    @Override
    public void add(Element element) {

    }

    @Override
    public void add(ProcessingInstruction processingInstruction) {

    }

    @Override
    public boolean remove(Node node) {
        return false;
    }

    @Override
    public boolean remove(Comment comment) {
        return false;
    }

    @Override
    public boolean remove(Element element) {
        return false;
    }

    @Override
    public boolean remove(ProcessingInstruction processingInstruction) {
        return false;
    }

    @Override
    public void normalize() {

    }
}
