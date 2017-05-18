package com.micro.first;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Resource;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;
@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MyMicroserviceBootApplication {

	public static void main(String[] args) {
		Object[] sources = {MyMicroserviceBootApplication.class, EnvironmentConfig.class};
        SpringApplication.run(sources, args);
	}
	
	  @Resource
	  private CloudantConfig dbconfig;
	 
	  @Bean
	  public Database cloudantclient () {
	 
	    Database db = null;
	    try {
	    	System.out.println("inside cloudantclient ");
	      CloudantClient client = ClientBuilder.url(new URL(dbconfig.getHost()))
	                  .username(dbconfig.getUsername())
	                  .password(dbconfig.getPassword())
	                  .build();
	      System.out.println("inside cloudantclient "+dbconfig.getUsername());
	      System.out.println("inside cloudantclient "+dbconfig.getPassword());
	        //Get socialReview db
	        // Get a Database instance to interact with, but don't create it if it doesn't already exist
	        db = client.database("securetender", true);
	        System.out.println("db.info().toString() "+db.info().toString());
	    }catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  return db;
	}
	
	  @Autowired
	  private Database db;
	
	@RestController
	class HelloContoller {
		 public JSONObject jsonRes;
		@JsonRawValue
		@JsonInclude
		@JsonProperty(value = "jsondata")
		public String getJson() {
		    return jsonRes.toString();
		}

		public void setJsonRes(JSONObject jsonRes) {
			this.jsonRes = jsonRes;
		}

		@RequestMapping(value = "/tender/chaincode/award/{awardUser}/{compName}")
	    String postAward(@PathVariable String awardUser,@PathVariable String compName) {
			JSONObject result=null;
			 JSONParser parser = new JSONParser();
			 try {
				 System.out.println(" in postAwatd Usr "+awardUser);
				 System.out.println(" in postAwatd compName "+compName);
				 System.out.println(" In callPostGovtLog");
				 DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				 Date today = Calendar.getInstance().getTime();        
				    StringBuffer loggerMessage =new StringBuffer();
				    String timeStamp = df.format(today);
				  //  if(status!=null && status.equalsIgnoreCase("0")) {
				    	//System.out.println("inisde0 loggermeag:: "+status);
				    
				        String cName = compName;
				        if(cName.contains("}")) {
				        	cName = cName.replaceAll("}", "");
				        }
				    	loggerMessage.append("Tender awarded to ").append(cName).append(" on ").append(timeStamp);
				   // }
				    
			              db.createIndex("querybyitemIdView","designdoc","json",
			                      new IndexField[]{
			                    new IndexField("companyName",SortOrder.asc)
			                        }
			              );
			          System.out.println("Successfully created index");
			          String status ="TENDER_STATUS";
			          //allDocs = db.findByIndex("{\"itemId\" :\"" + itemId + "\"}", Review.class);
			          List allDocs = db.findByIndex("{\"TenderString\" : " + status + "}", TenderAward.class);
			          ObjectMapper mapper = new ObjectMapper();
					   String responseStr = mapper.writeValueAsString(allDocs);
					   System.out.println("responseStr Award "+responseStr);
					   TenderAward  [] awardLst = (TenderAward[]) mapper.readValue(responseStr, TenderAward[].class);
						TenderAward existAward =new TenderAward();
						TenderAward award = new TenderAward();
						System.out.println("existAward "+existAward.toString());
						if(awardLst!=null && awardLst.length > 0) {
							existAward = awardLst[0];
							award.set_id(existAward.get_id());
							award.set_rev(existAward.get_rev());
						}
				    	
				    	award.setStatus("Done");
				    	award.setCompName(cName);
				    	 Response r = null;
						    if (award != null) {
						        r = db.post(award);
						    }
						    String dbId = r.getId();
						    System.out.println("Save Id " + dbId);
					 String content = getFile("postBidData");
					 content = content.replaceAll("#CHAINCODENAME", BlockChainConstants.CHAINCODE);
					 content =content.replaceAll("#USERID", "user_type1_4");
					 content =content.replaceAll("#ARG1", "Govt of India");
					 content =content.replaceAll("#ARG2", loggerMessage.toString());
					 content =content.replaceAll("#ARG3", timeStamp);
					 
					 Object obj = parser.parse(content);
					 System.out.println(" file found Govt "+obj);
					 JSONObject jsonData = (JSONObject) obj;
					 System.out.println("jsonData: GOvt "+jsonData);
					 RestTemplate restTemplate = new RestTemplate();    
					 HttpHeaders httpHeaders = new HttpHeaders();
					 httpHeaders.setContentType(MediaType.APPLICATION_JSON);
					 httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
					 final String uri = BlockChainConstants.BLOCKCHAIN_URI;
					HttpEntity<String> entityCredentials = new HttpEntity<String>(jsonData.toString(), httpHeaders);
					ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(uri,
					        HttpMethod.POST, entityCredentials, JSONObject.class);
					
					if (responseEntity != null) {
						result = responseEntity.getBody();
					}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return result.toString();
		}
		
		@RequestMapping(value = "/tender/chaincode/tenderStatus")
		String getTenderStatus(){

			String responseStr = null;
			
			// Get all documents from securetender
		    List allDocs = null;
		    try
		    {
		      
		          // create Index
		          // Create a design doc named designdoc
		          // A view named querybyitemIdView
		          // and an index named itemId
		              db.createIndex("querybyitemIdView","designdoc","json",
		                      new IndexField[]{
		                    new IndexField("companyName",SortOrder.asc)
		                        }
		              );
		          System.out.println("Successfully created index");
		          String status ="TENDER_STATUS";
		          //allDocs = db.findByIndex("{\"itemId\" :\"" + itemId + "\"}", Review.class);
		          allDocs = db.findByIndex("{\"TenderString\" : " + status + "}", TenderAward.class);
		          ObjectMapper mapper = new ObjectMapper();
				    responseStr = mapper.writeValueAsString(allDocs);
		          System.out.println("allDocs :"+allDocs);
		    } catch (Exception e) {
		                System.out.println("Exception thrown : " + e.getMessage());
		        }
			return responseStr;
		
		}
		
		
		@RequestMapping(value = "/tender/chaincode/allBid")
	    String getAllBids() 
		{
			System.out.println("before call post");
			JSONObject result=null;
			 JSONParser parser = new JSONParser();
			 String  responseStr = null;
			 try {
				String chainCodeName = BlockChainConstants.CHAINCODE;
				 System.out.println("Ready to read file");
				 String content = getFile("postDataAll");
				 System.out.println("After to read file");
				 content = content.replaceAll("#CHAINCODENAME", chainCodeName);
				 content =content.replaceAll("#USERID", "user_type1_4");
				 Object obj = parser.parse(content);
				 System.out.println(" file found "+obj);
				 JSONObject jsonData = (JSONObject) obj;
				 System.out.println("jsonData:  "+jsonData);
				 RestTemplate restTemplate = new RestTemplate();    
				 HttpHeaders httpHeaders = new HttpHeaders();
				 httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				 httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				 final String uri = BlockChainConstants.BLOCKCHAIN_URI;
				HttpEntity<String> entityCredentials = new HttpEntity<String>(jsonData.toString(), httpHeaders);
				ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(uri,
				        HttpMethod.POST, entityCredentials, JSONObject.class);
				
				if (responseEntity != null) {
					result = responseEntity.getBody();
				}
				  
               
   System.out.println("result from chaincode : "+result);
   System.out.println(result.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} 
			 
			 
		   return result.toString();
		}
	    
		
		List callBids(String userId) {
			
			
			// Get all documents from securetender
		    List allDocs = null;
		    try
		    {
		      if(userId == null)
		      {
		        allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
		            .getDocsAs(BidValues.class);
		      }else{
		          // create Index
		          // Create a design doc named designdoc
		          // A view named querybyitemIdView
		          // and an index named itemId
		              db.createIndex("querybyitemIdView","designdoc","json",
		                      new IndexField[]{
		                    new IndexField("companyName",SortOrder.asc)
		                        }
		              );
		          System.out.println("Successfully created index");
		          //allDocs = db.findByIndex("{\"itemId\" :\"" + itemId + "\"}", Review.class);
		          allDocs = db.findByIndex("{\"username\" : " + userId + "}", BidValues.class);
		          System.out.println("allDocs :"+allDocs);
		      }
		    } catch (Exception e) {
		                System.out.println("Exception thrown : " + e.getMessage());
		        }
			return allDocs;
		}
		
List callAllBids(String status) {
			
			// Get all documents from securetender
		    List allDocs = null;
		    try
		    {
		      if(status == null)
		      {
		        allDocs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse()
		            .getDocsAs(BidValues.class);
		      }else{
		          // create Index
		          // Create a design doc named designdoc
		          // A view named querybyitemIdView
		          // and an index named itemId
		              db.createIndex("querybyitemIdView1","designdoc1","json",
		                      new IndexField[]{
		                    new IndexField("bidPrice",SortOrder.asc)
		                        }
		              );
		          System.out.println("Successfully created index calling  user");
		          //allDocs = db.findByIndex("{\"itemId\" :\"" + itemId + "\"}", Review.class);
		          allDocs = db.findByIndex("{\"queryType\" : " + "bids" + "}", BidValues.class);
		          System.out.println("allDocs :"+allDocs);
		      }
		    } catch (Exception e) {
		                System.out.println("Exception thrown : " + e.getMessage());
		        }
			return allDocs;
		}

@RequestMapping(value = "/tender/chaincode/bids")
String callBidFrmDB() 
{
	System.out.println("before call post");
	JSONObject result=null;
	 JSONParser parser = new JSONParser();
	 String responseStr = null;
	 try {
		 
		    List allDocs = callAllBids("1");
		    ObjectMapper mapper = new ObjectMapper();
		    responseStr = mapper.writeValueAsString(allDocs);
		    System.out.println("responseStr :"+responseStr);
	} catch (Exception e) {
		e.printStackTrace();
	} 
	 
	 
   return responseStr;
}
		
	    @RequestMapping(value = "/tender/chaincode/{userId}")
	    String callChaincode(@PathVariable String userId) 
		{
			System.out.println("before call post");
			JSONObject result=null;
			 JSONParser parser = new JSONParser();
			 String responseStr = null;
			 try {
				 
				    List allDocs = callBids(userId);
				    ObjectMapper mapper = new ObjectMapper();
				    responseStr = mapper.writeValueAsString(allDocs);
				    System.out.println("responseStr :"+responseStr);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			 
			 
		   return responseStr;
		}
	    
	    @RequestMapping(value = "/tender/bid/{userId}/{companyName}/{address}/{proposal}/{bidPrice}/{addInfo}/{status}")
	    String postBid(@PathVariable String userId,@PathVariable String companyName,@PathVariable String address,@PathVariable String proposal,@PathVariable String bidPrice,@PathVariable String addInfo,@PathVariable String status) 
		{
			System.out.println("before call post");
			JSONObject result=null;
			 JSONParser parser = new JSONParser();
			 
			 try {
				String chainCodeName = BlockChainConstants.CHAINCODE;
				BidValues bids = new BidValues();
				bids.setUsername(userId);
				bids.setCompanyName(companyName);
				bids.setAddress(address);
				bids.setProposal(proposal);
				bids.setBidPrice(bidPrice);
				bids.setAddInfo(addInfo);
				bids.setStatus("1");
				 System.out.println("status "+status+":"+chainCodeName);
				 System.out.println("userId "+userId+":"+companyName);
				 System.out.println("proposal "+proposal+":"+addInfo);
				 DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				 Date today = Calendar.getInstance().getTime();        
				    StringBuffer loggerMessage =new StringBuffer();
				    String timeStamp = df.format(today);
				    	loggerMessage.append(companyName).append(" ").append(BlockChainConstants.BID_STATUS_1).append(" ").append(timeStamp);
				    System.out.println("loggerMessage "+loggerMessage.toString()+":"+timeStamp);
				    
				List allDocs = callBids(userId);
				if(allDocs!=null && allDocs.size() > 0) {
					loggerMessage =new StringBuffer();
					loggerMessage.append(companyName).append(" ").append(BlockChainConstants.BID_STATUS_2).append(" ").append(timeStamp);
					BidValues existBid = (BidValues) allDocs.get(0);
					bids.set_rev(existBid.get_rev());
					bids.set_id(existBid.get_id());
				}
				// Mock data for testing
			    //db.save(new Review(true));
			    //Review doc = db.find(Review.class,"111");
			    //return doc.toString();
			    System.out.println("Save Review " + bids.toString());
			    System.out.println(db);
			    Response r = null;
			    if (bids != null) {
			        r = db.post(bids);
			    }
			    String dbId = r.getId();
			    System.out.println("Save Id " + dbId);
			
			    
			    
				 String content = getFile("postBidData");
				 System.out.println("content "+content+":"+chainCodeName);
				 System.out.println("userId "+userId+":"+companyName);
				 System.out.println("loggerMessage "+loggerMessage+":"+timeStamp);
				 content = content.replaceAll("#CHAINCODENAME", chainCodeName);
				 content =content.replaceAll("#USERID", "user_type1_4");
				 content =content.replaceAll("#ARG1", companyName);
				 content =content.replaceAll("#ARG2", loggerMessage.toString());
				 content =content.replaceAll("#ARG3", timeStamp);
				 System.out.println("Content after "+content);
				 Object obj = parser.parse(content);
				 System.out.println(" file found "+obj);
				 JSONObject jsonData = (JSONObject) obj;
				 System.out.println("jsonData:  "+jsonData);
				 RestTemplate restTemplate = new RestTemplate();    
				 HttpHeaders httpHeaders = new HttpHeaders();
				 httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				 httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				 final String uri = BlockChainConstants.BLOCKCHAIN_URI;
				HttpEntity<String> entityCredentials = new HttpEntity<String>(jsonData.toString(), httpHeaders);
				ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(uri,
				        HttpMethod.POST, entityCredentials, JSONObject.class);
				
				if (responseEntity != null) {
					result = responseEntity.getBody();
				}
				callPostGovtLog(userId,companyName);
   System.out.println("result from chaincode  1 : "+result);
   System.out.println(result.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} 
		   return result.toString();
		}
}
	
	
	
	
	String callPostGovtLog(String userId,String companyName) {
		JSONObject result=null;
		 JSONParser parser = new JSONParser();
		 try {
			 System.out.println(" In callPostGovtLog");
			 DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			 Date today = Calendar.getInstance().getTime();        
			    StringBuffer loggerMessage =new StringBuffer();
			    String timeStamp = df.format(today);
			  //  if(status!=null && status.equalsIgnoreCase("0")) {
			    	//System.out.println("inisde0 loggermeag:: "+status);
			    	loggerMessage.append(BlockChainConstants.BID_STATUS_GOVT).append(" ").append(companyName).append(" on ").append(timeStamp);
			   // }
			    
				 String content = getFile("postBidData");
				 content = content.replaceAll("#CHAINCODENAME", BlockChainConstants.CHAINCODE);
				 content =content.replaceAll("#USERID", "user_type1_4");
				 content =content.replaceAll("#ARG1", "Govt of India");
				 content =content.replaceAll("#ARG2", loggerMessage.toString());
				 content =content.replaceAll("#ARG3", timeStamp);
				 
				 Object obj = parser.parse(content);
				 System.out.println(" file found Govt "+obj);
				 JSONObject jsonData = (JSONObject) obj;
				 System.out.println("jsonData: GOvt "+jsonData);
				 RestTemplate restTemplate = new RestTemplate();    
				 HttpHeaders httpHeaders = new HttpHeaders();
				 httpHeaders.setContentType(MediaType.APPLICATION_JSON);
				 httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				 final String uri = BlockChainConstants.BLOCKCHAIN_URI;
				HttpEntity<String> entityCredentials = new HttpEntity<String>(jsonData.toString(), httpHeaders);
				ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(uri,
				        HttpMethod.POST, entityCredentials, JSONObject.class);
				
				if (responseEntity != null) {
					result = responseEntity.getBody();
				}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result.toString();
	}
	
	private String getFile(String fileName) {

		StringBuilder result = new StringBuilder("");

		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();

	  }
	
}
