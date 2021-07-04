package apdc.events.utils.jsonclasses;

public class ReportEventArgs {

	long eventId;
	String reportText;
	public ReportEventArgs() {
	}
	public long getEventId() {
		return eventId;
	}
	public String getReportText() {
		return reportText;
	}
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	public void setReportText(String reportText) {
		this.reportText = reportText;
	}

}
