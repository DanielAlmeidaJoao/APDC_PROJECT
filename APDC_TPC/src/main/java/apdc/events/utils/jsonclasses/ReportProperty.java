package apdc.events.utils.jsonclasses;

import java.util.LinkedList;

public class ReportProperty {

	LinkedList<String> reports;
	public ReportProperty() {
		reports=new LinkedList<>();
	}
	
	public void addReport(String report) {
		reports.add(report);
	}

}
