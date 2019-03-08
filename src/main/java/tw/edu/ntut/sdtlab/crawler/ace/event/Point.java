package tw.edu.ntut.sdtlab.crawler.ace.event;

import java.io.Serializable;

public class Point implements Serializable {
	private int x = -1;
	private int y = -1;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	public Point clone() {
		Point cloneObject = new Point(x, y);
		return cloneObject;
	}	

}
