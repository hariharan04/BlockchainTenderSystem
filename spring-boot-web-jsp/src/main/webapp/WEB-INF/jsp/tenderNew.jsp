	<!DOCTYPE html>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>    
   <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>   
	<html>
	 <head>
	  <title> Tender Details </title>
	  <meta name="Generator" content="EditPlus">
	  <meta name="Author" content="">
	  <meta name="Keywords" content="">
	  <meta name="Description" content="">
	  <style>
		input[type=text], textarea {
			width: 100%; 
			padding: 12px; 
			border: 1px solid #ccc; 
			border-radius: 4px; 
			box-sizing: border-box; 
			margin-top: 6px; 
			margin-bottom: 16px; 
			resize: vertical 
		}
		input[type=submit] {
			background-color: #4CAF50;
			color: white;
			padding: 12px 20px;
			border: none;
			border-radius: 4px;
			cursor: pointer;
		}
		input[type=submit]:hover {
			background-color: #45a049;
		}
		.container {
			border-radius: 5px;
			background-color: #f2f2f2;
			padding: 20px;
		}
		fieldset {
			width: 100%; 
			padding: 12px; 
			border: 1px solid #ccc; 
			border-radius: 4px; 
			box-sizing: border-box; 
			margin-top: 6px; 
			margin-bottom: 16px; 
		}
	  </style>
	 </head>
	
	 <body>
		 <div class="container">
		  <form action="/register"> 
		  <fieldset>
			<legend><b>Bid Tender</b></legend>
			<label>Company Name</label>
			<input type="text" name="companyName" required />" >
			<label>Address</label>
			<input type="text" name="address" required />">
			<label>Proposal Details</label>
			<textarea name="proposal" placeholder="Enter proposal information here" style="height:100px" required>
			</textarea>
			<label >Bidding Price</label>
			<input type="text" name="bidPrice" required > 
			<label>Additional information</label>
			<textarea name="addInfo" placeholder="Enter proposal information here" style="height:100px" required>
			</textarea>
			<label>Status</label>
			<input type="text" name="status" required  readonly="readonly">
			<input type="hidden" name="uname"  value="<c:out value="${uname}"/>"> 
			<input type="submit" value="Register">
		  </fieldset>
		  </form>
		</div>
	 </body>
	</html>
