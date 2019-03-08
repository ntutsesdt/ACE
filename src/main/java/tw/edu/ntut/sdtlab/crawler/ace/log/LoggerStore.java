package tw.edu.ntut.sdtlab.crawler.ace.log;

import java.io.Serializable;
import java.util.ArrayList;

public class LoggerStore implements Serializable {
	ArrayList errorList = new ArrayList<String>();
	ArrayList messageList = new ArrayList<String>();
	public LoggerStore() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<String> getErrorList() {
		return this.errorList;
	}
	
	public ArrayList<String> getMessageList() {
		return this.messageList;
	}
	
	public void addErrorMessage(String errorMessage) {
		errorList.add(errorMessage);
	}

	public void addMessage(String message) {
		messageList.add(message);
	}
}
