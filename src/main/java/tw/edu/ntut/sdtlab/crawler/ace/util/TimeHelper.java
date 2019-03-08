package tw.edu.ntut.sdtlab.crawler.ace.util;

public class TimeHelper {
	public static void sleep(long ms) throws InterruptedException {
		Thread.sleep(ms);
	}
	
	public static long getWaitingTime(String configurationType) throws NumberFormatException {
		String time = ConfigReader.getConfigurationValue(configurationType);
		Double tempTime = Double.parseDouble(time)*1000;
		long result = tempTime.longValue();
		return result;
	}
}
