package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import org.junit.Before;
import org.junit.Test;
import tw.edu.ntut.sdtlab.crawler.ace.equivalent.Distance;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class DistanceTest {
    private Distance distance;

    @Before
    public void setup() {
        this.distance = new Distance();
    }

    @Test
    public void testSetDistance() throws NoSuchFieldException, IllegalAccessException {
        int d = 1;
        this.distance.setDistance(d);
        Field field = Distance.class.getDeclaredField("distance");
        field.setAccessible(true);
        int actual = (int) field.get(this.distance);
        assertEquals(d, actual);
    }

    @Test
    public void testGetDistance() throws NoSuchFieldException, IllegalAccessException {
        int d = 1;
        Field field = Distance.class.getDeclaredField("distance");
        field.setAccessible(true);
        field.set(this.distance, d);
        assertEquals(d, this.distance.getDistance());
    }
}
