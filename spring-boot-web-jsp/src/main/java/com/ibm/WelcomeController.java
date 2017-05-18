package com.ibm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
* <h1>Welcome Controller</h1>
* The WelcomeController program implements Blockchain based
* Tender management system.This application 
* include Tender creation,Tender processed and blockchain Ledger Entry .
* <p> 
*
* @author  HariHaran
* @version 1.0
* @since   2017-05-10 
*/
@Controller
@Scope("session")
public class WelcomeController {

	
	// inject via application.properties
	@Value("${welcome.message:test}")
	private String message = "Hello World";

	@RequestMapping("/welcome")
	public String welcome(Map<String, Object> model,HttpSession session) {
		session.removeAttribute("uname");
		model.put("message", this.message);
		return "welcome";
	}
	BidValues bids ;
	
	public BidValues getBids() {
		return bids;
	}

	public void setBids(BidValues bids) {
		this.bids = bids;
	}

	@RequestMapping("/login")
	public String login(Map<String, Object> model,@RequestParam("uname") String uname,HttpSession session) {
		try {
			if(uname==null || uname.equalsIgnoreCase("")) {
				uname = (String) session.getAttribute("uname");
			}
			session.setAttribute("uname", uname);
			System.out.println("uname"+uname);
			
			model.put("uname", uname);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//return "tender";
		return "Home";
	}
	
	@RequestMapping("/postTender")
	public String postTender(Map<String, Object> model,@RequestParam("uname") String uname,HttpSession session) {
		try {
			if(uname==null || uname.equalsIgnoreCase("")) {
				uname = (String) session.getAttribute("uname");
			}
			session.setAttribute("uname", uname);
			System.out.println("uname"+uname);
			
			model.put("uname", uname);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//return "tender";
		return "Home";
	}
	
	@RequestMapping("/tender")
	public String tender(@RequestParam Map<String,String> requestParams,Map<String, Object> model,HttpSession session) {
		String retStr = null;
		try {
			String uname=requestParams.get("uname");
			
			if(uname==null || uname.equalsIgnoreCase("")) {
				uname = (String) session.getAttribute("uname");
			}
			System.out.println("Path uname :"+uname);
			String actionVal=requestParams.get("actionVal");
			System.out.println("uname actionVal :"+actionVal);
			model.put("uname", uname);
			String name=actionVal;
			if(name!=null && name.equalsIgnoreCase("pstTend")) {
				retStr = "postTender";
			}else if(name!=null && name.equalsIgnoreCase("logout")) {
				  return "forward:/welcome";
			}
			else if(name!=null && name.equalsIgnoreCase("log")) {
				RestTemplate restTemplate = new RestTemplate();
				String uri = BlockChainUIConstants.bluemixUri+"/tender/chaincode/allBid";
				String result = restTemplate.getForObject(uri, String.class);
				ObjectMapper mapper = new ObjectMapper();
				System.out.println("result "+result);
				ResJsonObj resJson = (ResJsonObj) mapper.readValue(result, ResJsonObj.class);
				System.out.println("resJson.getJsonrpc() :"+resJson.getJsonrpc());
				System.out.println(resJson.getResult().getMessage());
				JSONArray jsonObj = new JSONArray(resJson.getResult().getMessage());
				System.out.println("jsonObj "+jsonObj);
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				BlockChainLog[] logs = (BlockChainLog[]) mapper.readValue(jsonObj.toString(), BlockChainLog[].class);
				ArrayList<BlockChainLog> logList = new ArrayList<BlockChainLog>(Arrays.asList(logs));
				
				model.put("logList", logList);
				retStr = "blockchainLog";
			}else if(name!=null && name.equalsIgnoreCase("prcsTend")) {
				RestTemplate restTemplate = new RestTemplate();
				String uri = BlockChainUIConstants.bluemixUri+"/tender/chaincode/bids";
				System.out.println("Bid DB URI : "+uri);
				String result = restTemplate.getForObject(uri, String.class);
				System.out.println("bid result "+result);
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				BidValues  [] bidLst = (BidValues[]) mapper.readValue(result, BidValues[].class);
				ArrayList<BidValues> bidList = new ArrayList<BidValues>(Arrays.asList(bidLst));
				
				model.put("bidList", bidList);
				String awardUuri = BlockChainUIConstants.bluemixUri+"/tender/chaincode/tenderStatus";
				String awardresult = restTemplate.getForObject(awardUuri, String.class);
				TenderAward  [] awardLst = (TenderAward[]) mapper.readValue(awardresult, TenderAward[].class);
				TenderAward award =new TenderAward();
				if(awardLst!=null && awardLst.length > 0) {
					award = awardLst[0];
					 String status = award.getStatus();
					 String companyName = award.getCompName();
					 if(status!=null && status.equalsIgnoreCase("Done")){
						 model.put("companyName", companyName);
						 return "biddingClose";
					 }
					 
				}
				retStr = "bidding";
				
			}else if(name!=null && name.equalsIgnoreCase("bid")) {
				RestTemplate restTemplate = new RestTemplate();
				String uri = BlockChainUIConstants.bluemixUri+"/tender/chaincode/"+uname;
				String awardUuri = BlockChainUIConstants.bluemixUri+"/tender/chaincode/tenderStatus";
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				System.out.println("Bid UrI : "+uri);
				String result = restTemplate.getForObject(uri, String.class);
				System.out.println("bid result "+result);
				
				BidValues  [] bidLst = (BidValues[]) mapper.readValue(result, BidValues[].class);
				BidValues bids =new BidValues();
				if(bidLst!=null && bidLst.length > 0) {
					 bids = bidLst[0];
					 String status = bids.getStatus();
					 status = status.replaceAll("}", "");
					 bids.setStatus(status);
				}
				
				model.put("bids", bids);
				String awardresult = restTemplate.getForObject(awardUuri, String.class);
				TenderAward  [] awardLst = (TenderAward[]) mapper.readValue(awardresult, TenderAward[].class);
				TenderAward award =new TenderAward();
				if(awardLst!=null && awardLst.length > 0) {
					award = awardLst[0];
					 String status = award.getStatus();
					 String companyName = award.getCompName();
					 if(status!=null && status.equalsIgnoreCase("Done")){
						 model.put("companyName", companyName);
						 return "bidClose";
					 }
					 
				}
				retStr = "tender";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//return "tender";
		return retStr;
	}
	
	@RequestMapping("/award")
	public String updateAward(@RequestParam("awardName") String awardName,@RequestParam("compName") String compName,Map<String, Object> model,HttpSession session) {

		try {//compName
			RestTemplate restTemplate = new RestTemplate();
			 UriTemplate template = new UriTemplate(BlockChainUIConstants.bluemixUri+"/tender/chaincode/award/{awardName}/{compName}}");
			 Map<String, String> uriVariables = new HashMap<String, String>();
			   uriVariables.put("awardName", awardName);
			   uriVariables.put("compName", compName);
			   URI uri = template.expand(uriVariables);
			System.out.println("updateAward : "+uri);
			String result = restTemplate.getForObject(uri, String.class);
			System.out.println("updateAward "+result);
				String uname = (String) session.getAttribute("uname");
			System.out.println("Path uname :"+uname);
			model.put("uname", uname);
			
			model.put("result", result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
		
		
		return "Home";
		
	}
	
	@RequestMapping("/register")
	public String register(@RequestParam Map<String,String> requestParams,Map<String, Object> model,HttpSession session) {
		 System.out.println(" in Reister call");
		  String companyName=requestParams.get("companyName");
		   String address=requestParams.get("address");
		   String proposal=requestParams.get("proposal");
		   String bidPrice=requestParams.get("bidPrice");
		   String addInfo=requestParams.get("addInfo");
		   String status=requestParams.get("status");
		   if(status==null && status.equalsIgnoreCase("")){
			   status ="0";
		   }
		   String uname=requestParams.get("uname");
		   if(uname==null || uname.equalsIgnoreCase("")) {
				uname = (String) session.getAttribute("uname");
			}
		  
		   UriTemplate template = new UriTemplate(BlockChainUIConstants.bluemixUri+"/tender/bid/{userId}/{companyName}/{address}/{proposal}/{bidPrice}/{addInfo}/{status}}");
		   Map<String, String> uriVariables = new HashMap<String, String>();
		   uriVariables.put("userId", uname.trim());
		   uriVariables.put("companyName", companyName.trim());
		   uriVariables.put("address", address.trim());
		   uriVariables.put("proposal", proposal.trim());
		   uriVariables.put("bidPrice", bidPrice.trim());
		   uriVariables.put("addInfo", addInfo.trim());
		   uriVariables.put("status", status.trim());
		   URI uri = template.expand(uriVariables);
		   
		   
		  // String uri = "http://localhost:8090/hello/bid/";
		  // StringBuffer uriBuff = new StringBuffer(uri).append(uname).append("/").append(companyName).append("/").append(address).append("/").append(proposal).append("/").append(bidPrice).append("/")
				 //  .append(addInfo).append("/").append(status);
		System.out.println("uriBuff "+uri);
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		model.put("message", result);
		return "Home";
	}

}