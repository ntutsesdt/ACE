package tw.edu.ntut.sdtlab.crawler.ace.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UtilityTest {
    @Test
    public void testGetMin() {
        int m = 10;
        int n = 5;
        assertEquals(n, Utility.getMin(m, n));
    }

    @Test
    public void testGetMinOpposite() {
        int m = 10;
        int n = 5;
        assertEquals(n, Utility.getMin(n, m));
    }

    @Test
    public void testGetMinWhenTheSame() {
        int m = 10;
        int n = 10;
        assertEquals(n, Utility.getMin(n, m));
    }

    @Ignore
    public void testGetNAFClassName() throws DocumentException {
        String dir = "/Volumes/Transcend/Thesis/naf/20170422_163154_loginapplication_NFS/States";
        File directory = new File(dir);
        Map<String, Integer> map = new HashMap<>();
        for (File file : directory.listFiles()) {
            if (file.getName().contains(".xml")) {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(file);
                List nodes = document.getRootElement().selectNodes("//node");
                for (Object node : nodes) {
                    Element element = (Element) node;
                    if (element.attributeValue("NAF") != null && element.attributeValue("NAF").compareTo("true") == 0) {
                        int count = map.containsKey(element.attributeValue(NodeAttribute.Class)) ? map.get(element.attributeValue(NodeAttribute.Class)) + 1 : 1;
                        map.put(element.attributeValue(NodeAttribute.Class), count);
                    }
                }
            }
        }
        for(String key : map.keySet()) {
            System.out.println(key + " => " + map.get(key));
        }
    }
}
