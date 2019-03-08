package tw.edu.ntut.sdtlab.crawler.ace.event;

import org.junit.Before;
import org.junit.Test;
import tw.edu.ntut.sdtlab.crawler.ace.event.Point;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;

public class PointTest {
    private Point point;

    @Before
    public void setup() {

    }

    @Test
    public void testX() {
        int x = 2, y = 3;
        this.point = new Point(x, y);
        assertEquals(x, this.point.x());
    }

    @Test
    public void testY() {
        int x = 2, y = 3;
        this.point = new Point(x, y);
        assertEquals(y, this.point.y());
    }

    @Test
    public void testClone() {
        int x = 2, y = 3;
        this.point = new Point(x, y);
        Point clonePoint = this.point.clone();
        assertEquals(x, this.point.x());
        assertEquals(y, this.point.y());
        assertNotSame(clonePoint, this.point);
    }
}
