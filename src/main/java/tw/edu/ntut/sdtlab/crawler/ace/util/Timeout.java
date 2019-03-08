package tw.edu.ntut.sdtlab.crawler.ace.util;

import tw.edu.ntut.sdtlab.crawler.ace.util.Config;

public class Timeout {
	private long timeout = 0;
	public Timeout() {
		long configTimeout = Config.TIMEOUT_SECOND;
		if(configTimeout != 0) {
			timeout = configTimeout * 1000;
		}
	}
	
	public boolean check(long currentExecuteTime) {
		if(timeout == 0)
			return false;
		else {
			if(currentExecuteTime > timeout)
				return true;
			else
				return false;
		}
	}

}
