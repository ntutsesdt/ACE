package tw.edu.ntut.sdtlab.crawler.ace.event;

import tw.edu.ntut.sdtlab.crawler.ace.event.Point;

public class PointHelper {
	//TODO UT
	public static int getAverage(int a, int b) {
		return (a+b)/2;
	}
	
	public static Point getCenterPoint(Point a, Point b) {
		Integer x = null, y = null;
		x = getAverage(a.x(), b.x());
		y = getAverage(a.y(), b.y());
		return new Point(x, y);
	}
}
