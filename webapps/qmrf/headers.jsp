<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/random-1.0" prefix="rand" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<html>
<body>
<p/>
<c:forEach var="hname" items="${pageContext.request.headerNames}">
    <c:forEach var="hvalue" items="${headerValues[hname]}">
        <b>${hname}</b>&nbsp;${hvalue}
        <p>
    </c:forEach>
</c:forEach>

<br>
<rand:string id="random1" charset="a-zA-Z0-9! @ # $" length='15'/>
<jsp:getProperty name="random1" property="random" />

<hr>
    <h3>&#160;</h3>

    <table border="1" width="539">
      <tr>
        <td colspan="3" width="529" bgcolor="#0000FF">
          <b>
            <font color="#FFFFFF" size="4">HTTP
            Request(pageContext.request.)</font>
          </b>
        </td>
      </tr>

      <tr>
        <td width="210">Access Method</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.method}" />
        </td>
        <td>pageContext.request.method</td>
      </tr>

      <tr>
        <td width="210">Authentication Type</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.authType}" />
        </td>
        <td>pageContext.request.authType</td>
      </tr>

      <tr>
        <td width="210">Context Path</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.contextPath}" />
        </td>
        <td>pageContext.request.contextPath</td>
      </tr>

      <tr>
        <td width="210">Path Information</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.pathInfo}" />
        </td>
        <td>pageContext.request.pathInfo</td>
      </tr>

      <tr>
        <td width="210">Path Translated</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.pathTranslated}" />
        </td>
        <td>pageContext.request.pathTranslated</td>
      </tr>

      <tr>
        <td width="210">Query String</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.queryString}" />
        </td>
        <td>pageContext.request.queryString</td>
      </tr>

      <tr>
        <td width="210">Request URI</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.requestURI}" />
        </td>
        <td>pageContext.request.requestURI</td>
      </tr>
      <tr>
        <td width="210">User</td>

        <td width="313">&#160; 
    	<c:out value="${pageContext.request.userPrincipal.name}" />        
    	<br/>
        <c:out value="${pageContext.request.userPrincipal.password}" />
        <br/>
        <c:forEach var="a" items="${pageContext.request.userPrincipal.roles}">
        	${a}
        </c:forEach>
        </td>
        
        <td>
    	pageContext.request.userPrincipal.name
    	<br/>
        pageContext.request.userPrincipal.password
        <br/>
        pageContext.request.userPrincipal.roles
        <br/>

        </td>
      </tr>      
      <tr>
        <td width="210">Protocol</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.protocol}" />
        </td>
        <td>pageContext.request.protocol</td>
      </tr>          
      <tr>
        <td width="210">Scheme</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.scheme}" />
        </td>
        <td>pageContext.request.scheme</td>
      </tr>              
      <tr>
        <td width="210">Encoding</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.characterEncoding}" />
        </td>
        <td>pageContext.request.characterEncoding</td>
      </tr>            
      <tr>
        <td width="210">content length</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.contentLength}" />
        </td>
        <td>pageContext.request.contentLength</td>
      </tr>             
      <tr>
        <td width="210">LocalAddress</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.localAddr}" />
        </td>
        <td>pageContext.request.localAddr</td>
      </tr>     
      <tr>
        <td width="210">Locale</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.locale}" />
        </td>
        <td>pageContext.request.locale</td>
      </tr>      
      <tr>
        <td width="210">Local name</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.localName}" />
        </td>
        <td>pageContext.request.localName</td>
      </tr>         
      <tr>
        <td width="210">Local port</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.localPort}" />
        </td>
      <td>pageContext.request.localPort</td>   
      </tr>          
      <tr>
        <td width="210">Remote addr</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.remoteAddr}" />
        </td>
      <td>pageContext.request.remoteAddr</td>   
      </tr>      
      <tr>
        <td width="210">Remote port</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.remotePort}" />
        </td>
      <td>pageContext.request.remotePort</td>   
      </tr>   
      <tr>
        <td width="210">Server name</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.serverName}" />
        </td>
      <td>pageContext.request.serverName</td>   
      </tr>                  
      <tr>
        <td width="210">Server port</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.serverPort}" />
        </td>
      <td>pageContext.request.serverPort</td>   
      </tr>              
      <tr>
        <td width="210">isSecure</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.secure}" />
        </td>
      <td>pageContext.request.secure</td>   
      </tr>       
      <tr>
        <td width="210">attribute names</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.request.attributeNames}" />
        </td>
      <td>
      <c:forEach var="a" items="${pageContext.request.attributeNames}">
      	${a}
      </c:forEach>
      </td>   
      </tr>                              
    </table>

    <table border="1" width="539">
      <tr>
        <td colspan="3" width="529" bgcolor="#0000FF">
          <b>
            <font color="#FFFFFF" size="4">HTTP
            Session(pageContext.session.)</font>
          </b>
        </td>
      </tr>

      <tr>
        <td width="210">Creation Time</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.session.creationTime}" />
        </td>
        <td>pageContext.session.creationTime</td>
      </tr>

      <tr>
        <td width="210">Session ID</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.session.id}" />
        </td>
        <td>pageContext.session.id</td>
      </tr>

      <tr>
        <td width="210">Last Accessed Time</td>

        <td width="313">&#160; 
        <c:out value="${pageContext.session.lastAccessedTime}" />
        </td>
        <td>pageContext.session.lastAccessedTime</td>
      </tr>

      <tr>
        <td width="210">Max Inactive Interval</td>

        <td width="313">&#160; 
        <c:out
        value="${pageContext.session.maxInactiveInterval}" />

        seconds</td>
        <td>pageContext.session.maxInactiveInterval</td>
      </tr>

      <tr>
        <td width="210">You have been on-line for</td>

        <td width="313">&#160; 
        <c:out
        value="${(pageContext.session.lastAccessedTime-pageContext.session.creationTime)/1000}" />

        seconds</td>
        <td></td>
      </tr>
    </table>

    <table border="1" width="539">
      <tr>
        <td colspan="3" width="529" bgcolor="#0000FF">
          <b>
            <font color="#FFFFFF" size="4">HTTP
            (servletContext)</font>
          </b>
        </td>
      </tr>

      <tr>
        <td width="210">servletContext.majorVersion</td>

        <td width="313">&#160; 
        <c:out value="${servletContext.majorVersion}" />
        </td>
        <td>servletContext.majorVersion</td>
      </tr>
      </table>
</body>
</html>