package com.micro.first;

public class TenderAward {
	
	private String TenderString ="TENDER_STATUS";
	 private String _id;
	    
		private String _rev;
	public String get_id() {
			return _id;
		}
		public void set_id(String _id) {
			this._id = _id;
		}
		public String get_rev() {
			return _rev;
		}
		public void set_rev(String _rev) {
			this._rev = _rev;
		}
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
