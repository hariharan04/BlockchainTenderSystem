package com.ibm;

public class TenderAward {
	
	private String TenderString ="TENDER_STATUS";
	public String getTenderString() {
		return TenderString;
	}
	public void setTenderString(String tenderString) {
		TenderString = tenderString;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	private String Status;
	private String compName;
	public String getCompName() {
		return compName;
	}
	public void setCompName(String compName) {
		this.compName = compName;
	}


}
