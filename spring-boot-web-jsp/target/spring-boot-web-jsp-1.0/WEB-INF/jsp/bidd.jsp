	<!DOCTYPE html>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>    
   <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>   
	<html>
	 <head>
<style>
table {
    font-family: arial, sans-serif;
    border-collapse: collapse;
    width: 100%;
}

td, th {
    border: 1px solid #dddddd;
    text-align: left;
    padding: 8px;
}

tr:nth-child(even) {
    background-color: #dddddd;
}
</style>
</head>
<body>

<table>
  <tr>
    <th>Company Name</th>
    <th>Activity message</th>
    <th>Log</th>
  </tr>
  
 <c:forEach items="${bidList}" var="bid">
 <tr>
    <td>${log.companyName}</td>
    <td>${log.address}</td>
    <td>${log.proposal}</td>
  </tr>
</c:forEach>
  
  
</table>

	 </body>
	</html>
