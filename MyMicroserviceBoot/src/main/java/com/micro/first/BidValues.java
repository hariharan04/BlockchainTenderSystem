package com.micro.first;
import com.fasterxml.jackson.annotation.JsonView;
public class BidValues
{
	private String companyName;

    private String address;

    private String proposal;

    private String bidPrice;

    private String username;
    
    private String status;
    
    private String addInfo;
    
    private String _id;
    
	private String _rev;
	
	private String queryType="bids";

	
	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	@Override
	public String toString() {
		return "BidValues [companyName=" + companyName + ", address=" + address + ", proposal=" + proposal
				+ ", bidPrice=" + bidPrice + ", userName=" + username + ", status=" + status + ", addInfo=" + addInfo
				+ ", _id=" + _id + ", _rev=" + _rev + "]";
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProposal() {
		return proposal;
	}

	public void setProposal(String proposal) {
		this.proposal = proposal;
	}

	public String getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(String bidPrice) {
		this.bidPrice = bidPrice;
	}

	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddInfo() {
		return addInfo;
	}

	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

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

	

    }
