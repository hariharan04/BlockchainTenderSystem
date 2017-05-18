package com.ibm;
import com.fasterxml.jackson.annotation.JsonView;
public class BlockChainLog {
	private String compname;

    public String getCompname() {
		return compname;
	}

	public void setCompname(String compname) {
		this.compname = compname;
	}

	public String getLogmessage() {
		return logmessage;
	}

	public void setLogmessage(String logmessage) {
		this.logmessage = logmessage;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	private String logmessage;

    private String datetime;


}
