<%

  if (request.getParameter("logoff") == "true") {
    session.invalidate();
    response.sendRedirect("index.jsp");
    return;
  }
  if (request.getParameter("logoff") != null) {
    session.invalidate();
    response.sendRedirect("index.jsp");
    return;
  }
%>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<%

  if (request.getUserPrincipal() != null)  {
  	session.setAttribute("username",request.getUserPrincipal().getName());
  	session.setAttribute("iseditor",request.isUserInRole("qmrf_editor"));
	  session.setAttribute("ismanager",request.isUserInRole("qmrf_manager"));
	  session.setAttribute("isadmin",request.isUserInRole("qmrf_admin"));
	  session.setAttribute("isauthor",request.isUserInRole("qmrf_user"));
	  response.sendRedirect("index.jsp");
  }

%>
