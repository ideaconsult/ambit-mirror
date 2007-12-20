<%@ page import="java.io.*" %>
<%
	
	String fname = request.getParameter("name");
	int i = fname.lastIndexOf('/');
	if (i > 0)
		fname = fname.substring(i+1);
		
		ServletContext context = getServletContext();
		String dir = context.getInitParameter("attachments-dir");
        String file = dir + "/" + request.getParameter("name");
        response.setHeader("Content-Disposition","attachment;filename="+fname);
			InputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(file) );
				int ch;
				while ((ch = in.read()) !=-1) {
					out.print((char)ch);
				}
			}
			finally {
					if (in != null) in.close(); 
			}

%>