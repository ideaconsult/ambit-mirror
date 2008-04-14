package ambit2.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.data.DefaultData;
import ambit2.data.molecule.MoleculeTools;
import ambit2.database.ConnectionPool;
import ambit2.smiles.SmilesParserWrapper;

/**
 * A descendant of {@link javax.servlet.http.HttpServlet} encapsulating common task for AMBIT servlets.
 * Performs basic search in the database. <br>
 * Database parameters are configurable via servlet configuration (e.g. in Tomcat) and are:
 * database  (default ambit) <br>
 * host  (default localhost) <br>
 * port  (default 3306) <br>
 * user  (default guest) <br>
 * password  (default guest) <br>
 * 
 * <br>
 * Searching in database can have following parameters (send by HTTP POST or GET)
 * smiles  (SMILES string, optional) <br>
 * mol  (MOL file format representation of a compound, optional) <br>
 * name  (chemical name, optional) <br>
 * formula (chemical formula, optional) <br>
 * cas  (CAS Registry number, optional) <br>
 * identifier  (an identifier, stored in alias table, e.g. NCS number, optional) <br>
 * AliasType  (the type of the identifier above, e.g. AliasType=NSC, optional) <br>
 * Empty parameters are ignored, others are combined with logical "and".
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */	
public class AmbitServlet extends HttpServlet {
	protected SmilesGenerator sg;
	private String imageURL = "http://nina.acad.bg:8080/ambit/image?smiles=";
    private String displayURL = "http://ambit2.acad.bg/ambit/php/display2.php";
	protected ConnectionPool pool;
	protected DefaultData data;
	//config param, property name, default
	protected String[][] params = {
			{"database",DefaultData.DATABASE,"ambit"},
			{"host",DefaultData.HOST,"localhost"},
			{"port",DefaultData.PORT,"3306"},
			{"user",DefaultData.USER,"guest"},
			{"password",DefaultData.PASSWORD,"guest"},
			{"imageurl","imageurl",imageURL},
			{"displayurl","displayurl",displayURL},
			{"threshold","threshold","0.5"}
	};
	
   
	public AmbitServlet() {
		super();
	}
	public void init() throws ServletException {
		super.init();
		data = new DefaultData();
		/*
		data.put(DefaultData.DATABASE,"ambit");
		data.put(DefaultData.HOST,"localhost");
		data.put(DefaultData.PORT,"3306");
		data.put(DefaultData.USER,"guest");
		data.put(DefaultData.PASSWORD,"guest");
		*/
		
		ServletContext context = getServletContext();
		Enumeration e = context.getInitParameterNames();
		System.out.print("Servlet context");

		while (e.hasMoreElements()) {
			String s = e.nextElement().toString();
			/*
			System.out.print(s);
			System.out.print('=');
			System.out.println(context.getInitParameter(s));
			*/
		}

		ServletConfig config = getServletConfig();
		e = config.getInitParameterNames();
		
		
		//System.out.print("Servlet config of servlet ");
		//System.out.println(config.getServletName());
		/*
		while (e.hasMoreElements()) {
			String s = e.nextElement().toString();
			System.out.print(s);
			System.out.print('=');
			System.out.println(config.getInitParameter(s));
		}
		
		*/
		for (int i=0; i < params.length; i++) {
			String param = config.getInitParameter(params[i][0]);
			if (param != null) data.put(params[i][1],param);
			else data.put(params[i][1],params[i][2]);
			
		}
		/*
		param = config.getInitParameter("host");
		if (param != null) data.put(DefaultData.HOST,param);
		param = config.getInitParameter("port");
		if (param != null) data.put(DefaultData.PORT,param);
		param = config.getInitParameter("user");
		if (param != null) data.put(DefaultData.USER,param);
		param = config.getInitParameter("password");
		if (param != null) data.put(DefaultData.PASSWORD,param);
        param = config.getInitParameter("imageurl");
        if (param != null) data.put("imageurl",param);
        else data.put("imageurl", imageURL);
        if (param != null) data.put("displayurl",param);
        else data.put("displayurl", displayURL);        
        */
        //System.out.println(data);
        
		sg = new SmilesGenerator();
		try {
			pool = new ConnectionPool(data.get(DefaultData.HOST).toString(),
			        data.get(DefaultData.PORT).toString(),
			        data.get(DefaultData.DATABASE).toString(),
			        data.get(DefaultData.USER).toString(),
			        data.get(DefaultData.PASSWORD).toString(),
			        1,1
			        );

		} catch (Exception x) {
			pool = null;
		}
		
	}
	public void destroy() {
		/*
	    try {
		      if (dbConnection != null) dbConnection.close();
		    }
		catch (SQLException ignored) { }
		*/
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Connection conn = pool.getConnection();
			if (conn == null) {
				response.getWriter().println("Too many connections!");
				return;
			}			
			process(conn,request,response);
			pool.returnConnection(conn);
		} catch (Exception x) {
			x.printStackTrace(response.getWriter());
		}
	
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			Connection conn = pool.getConnection();		
			if (conn == null) {
				response.getWriter().println("Too many connections!");
				return;
			}
			process(conn,request,response);
			pool.returnConnection(conn);
		} catch (Exception x) {
			x.printStackTrace(response.getWriter());
		}
	}	
	protected void process(Connection connection,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();
        printHeaders(out,"styles/pstyle.css","details");
        
        String smiles = "";
        String formula = "";
        String name = "";
        String identifier = "";
        String cas = "";
        StringBuffer condition = new StringBuffer();
        StringBuffer conditionTitle = new StringBuffer();
        conditionTitle.append("<center>");
        conditionTitle.append("Looking for :\t ");
        try 
        { 
            smiles = request.getParameter("smiles");
            if ((smiles != null)  && (!smiles.equals(""))) {
            	smiles = getCanonicSmiles(smiles.trim());
            } else smiles = "";	
            
            String molecule = request.getParameter("mol");
            if ((molecule != null)  && (!molecule.equals(""))) {
            	molecule = URLDecoder.decode(molecule.trim());
            	try {
	                IMolecule newMol = MoleculeTools.readMolfile(molecule);
	                
	            	if ((newMol != null) && (newMol.getAtomCount() > 0)) {
	                    SmilesGenerator g = new SmilesGenerator();
	                	smiles = g.createSMILES(newMol);
		                g = null;
	            	}   
	            	newMol = null;

            	} catch (Exception x) {
            		x.printStackTrace(out);
            		smiles = "";
            		
            	}

            }            
            if (!smiles.equals("")) {
            	condition.append(" SMILES='");
            	condition.append(smiles);
            	condition.append("' and ");
            	conditionTitle.append("SMILES=");
            	conditionTitle.append(smiles);
            	conditionTitle.append('\t');
            }
            
            formula = request.getParameter("formula").trim();
            if ((formula != null) && (!formula.equals(""))) {
            	formula = getCanonicFormula(formula);
            	condition.append(" FORMULA='");
            	condition.append(formula);
            	condition.append("' and ");
            	conditionTitle.append("Formula=");
            	conditionTitle.append(formula);
            	conditionTitle.append('\t');
            }

            name = request.getParameter("name");
            if ((name != null)  && (!name.equals(""))) {
            	String sl = request.getParameter("soundsLike");
            	if ((sl != null) && (sl.equals("on"))) {
	            	condition.append(" name.name sounds like '");
	            	condition.append(name);
	            	conditionTitle.append("Name sounds like ");
	            	conditionTitle.append(name);	            	
            	} else {
	            	condition.append(" name.name='");
	            	condition.append(name);
	            	conditionTitle.append("Name is ");
	            	conditionTitle.append(name);
            	}
            	conditionTitle.append('\t');	            	
            	condition.append("' and ");
            }
            
            identifier = request.getParameter("identifier");
            if ((identifier != null)  && (!identifier.equals(""))) {
            	String idType = request.getParameter("AliasType");
            	if (idType != null) {
	            	condition.append(" (alias.alias = '");
	            	condition.append(identifier);
	            	condition.append("') and (alias_type = '");
	            	condition.append(idType);
	            	condition.append("') ");
	            	conditionTitle.append(idType);
	            	conditionTitle.append("=");
	            	conditionTitle.append(identifier);
	            	conditionTitle.append('\t');	            	
	            	condition.append(" and ");
            	}
            }
            
            cas = request.getParameter("cas");
            if ((cas != null) &&  (!cas.equals(""))) {
            	condition.append(" casno='");
            	condition.append(cas);    
            	condition.append("' and ");
            	conditionTitle.append("CAS RN is ");
            	conditionTitle.append(cas);
            	conditionTitle.append('\t');
            } 
                     
        } catch (Exception e)  { 
            e.printStackTrace(out);
        }
        conditionTitle.append("</center>");
    	//out.println(condition.toString());        
        if (condition.toString().trim().equals("")) {
            out.println("<p>Search condition not specified");

        } else {
            condition.append(" substance.idsubstance=c.idsubstance group by idsubstance order by type_structure desc limit 500");
	        out.println(conditionTitle);
	        dbSearch(connection,condition.toString(), out);
        }
        printFooters(out);
        
	}
	
	protected String getCanonicSmiles(String smiles) throws InvalidSmilesException {
		SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
		IMolecule m = sp.parseSmiles(smiles);
		sp = null;
		if (sg == null) sg = new SmilesGenerator();
		smiles = sg.createSMILES(m);
		m = null;
		return smiles;
	}
	/**
	 * this is to make the formula "canonical" (i.e. sorted in the right order)
	 * @param formula
	 * @return
	 */
	protected String getCanonicFormula(String formula) {
		MFAnalyser mfa = new MFAnalyser(formula, DefaultChemObjectBuilder.getInstance().newMolecule());
		mfa = new MFAnalyser(mfa.getAtomContainer());
		return mfa.getMolecularFormula();
	}
	protected void printHeaders(PrintWriter out, String styleSheetURL, String targetPage) {
	
        out.println("<html>");
        out.println("<head>");
        out.println("<meta name=\"description\" content=\"AMBIT Project : Building blocks for a future QSAR Decision support system\">");
        out.println("<meta name=\"keywords\" content=\"ambit,qsar, applicability domain, decision support\">");
        out.println("<meta name=\"robots\" content=\"index,follow\">");
        out.println("<meta name=\"copyright\" content=\"Copyright 2005,2006 Nina Jeliazkova nina@acad.bg\">");
        out.println("<meta name=\"author\" content=\"Nina Jeliazkova\">");
        out.println("<meta name=\"language\" content=\"English\">");
        out.println("<meta name=\"revisit-after\" content=\"7\">");
        
        out.println("</head>");
        out.print("<link href=\"");
        out.print(styleSheetURL);
        out.print("\" rel=\"stylesheet\" type=\"text/css\">");
        out.println("<title>Ambit project - QSAR DSS building blocks</title>");
        out.print("<base target=\"");
        out.print(targetPage);
        out.println("\">");
        out.println("<body>");
	}
	protected void printFooters(PrintWriter out) {
		out.println("<hr>");
        out.println("<a href=\"http://ambit2.acad.bg\">AMBIT</a>");		
        out.println("</body>");
        out.println("</html>");        
	}
	
	protected void dbSearch(Connection connection,String condition, PrintWriter out) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT substance.idsubstance,casno as CAS_RN,formula as Formula,molweight as MolWeight,name.name as ChemName,smiles as SMILES,type_structure,c.idstructure ");
		sql.append("FROM  structure as c, substance left join cas on (cas.idstructure=c.idstructure) ");
		sql.append("LEFT JOIN name on (name.idstructure=c.idstructure) ");
		sql.append("LEFT JOIN alias on (alias.idstructure=c.idstructure) WHERE ");
		sql.append(condition);


		Statement st = null;
		ResultSet rs = null;
	
		try {
			st = connection.createStatement();
			rs = st.executeQuery(sql.toString());
			//out.println(st);

			out.println("<table width=100% border=0 bgcolor='#428EFF' class='table'>\t<th>");
			out.println("\t<td><a href=http://www.cas.org/EO/regsys.html TARGET='_blank'>CAS RN</a></td>");
			out.println("\t<td>Chemical Name</td>");   
			out.println("\t<td>Formula</td>");      
			out.println("\t<td>Molecular Weight</td>");            
			out.println("\t<td><a href=\"http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html\" TARGET=\"_blank\">SMILES</a></td></th>");         
			
			out.println(printTableRows(rs,new String[]{"CAS_RN","ChemName","idsubstance","Formula","MolWeight","SMILES"}));
			out.println("</table>");		

		} catch (SQLException x) {
			out.write(sql.toString());
			x.printStackTrace(out);
				
		} finally {
			try {
			if (rs != null) rs.close();
			if (st != null) st.close();
			} catch (SQLException ignored) {}
		}
	
	}
	protected StringBuffer printRow(int row,ResultSet rs,String[] columns) throws SQLException {
		   StringBuffer out = new StringBuffer();		
		   out.append("\t<tr bgcolor='#FFFFFF'>"); 
		   
		   out.append("<td>");  out.append(row); out.append("</td>");
		   int i = 0;
		   String idsubstance = rs.getString("idsubstance");
		   while ( i < columns.length) {
			   out.append("<td>");
			   
			   if (columns[i].equals("idsubstance")) {
                   i++;
				   out.append(getDisplayURL(idsubstance,displayField(rs.getObject(columns[i]))));
         
			   } else if (columns[i].toLowerCase().equals("smiles")) {
				  out.append(
						  getImageURL(displayField(rs.getObject(columns[i])),idsubstance.toString(),""));
			   } else out.append(displayField(rs.getObject(columns[i]))); 
			   out.append("</td>\n");
			   i++;
		   }	   
		   out.append("</tr>\n");
		   return out;
		
	}
	protected StringBuffer printTableRows(ResultSet rs,String[] columns) throws SQLException {
		StringBuffer out = new StringBuffer();
		int nline = 0;
		while(rs.next()) {
			   nline++;
			   out.append("\t<tr bgcolor='#FFFFFF'>"); 
			   
			   out.append("<td>");  out.append(nline); out.append("</td>");
			   String idsubstance = rs.getString("idsubstance");
			   int i = 0;
			   while ( i < columns.length) {
				   out.append("<td>");
				   if (columns[i].equals("idsubstance")) {
					   
                       Object id = rs.getObject("idsubstance");
                       out.append(getDisplayURL(id,displayField(id)));
					   
					   i++;
					   out.append(displayField(rs.getObject(columns[i])));
					   out.append("</a>");         
				   } else if (columns[i].toLowerCase().equals("smiles")) {
					      out.append(displayField(rs.getObject(columns[i])));
					      out.append("<br>");
						  out.append(getImageURL(displayField(rs.getObject(columns[i])),idsubstance,""));
					   } else out.append(displayField(rs.getObject(columns[i]))); 

				   out.append("</td>\n");
				   i++;
			   }	   
			   out.append("</tr>\n");
		}
		return out;
	}
	protected String displayField(Object field) {
		if (field != null) { 
				return field.toString();
		} else return "";
	}
	protected StringBuffer printTableHeader(String[] columns) {
		StringBuffer out = new StringBuffer();		
		out.append("<table width=100% border=0 bgcolor='#428EFF' class='table'>\t<th>");
		
		for (int i=0; i < columns.length;i++) {
			if (columns[i].equals("idsubstance")) continue;
			out.append("\t<td>");
			out.append(columns[i]);
			out.append("</td>");
		}
		out.append("</th>");
		return out;
//		out.println("\t<td><a href=http://www.daylight.com/dayhtml/smiles/ TARGET=\"_blank\">SMILES</a></td></th>");         
	}
	public String getImageURL(String smiles, String idsubstance, String idstructure) {
		String imageurl = imageURL;
		
		if ((data != null) && (data.get("imageurl") != null)) imageurl = data.get("imageurl").toString();
		if ((smiles ==null) || "".equals(smiles))
			return "<img src='" + imageurl + "&id=" + idsubstance + "' ALT='" + idsubstance + "' />";
		else	
			return "<img src='" + imageurl + "&smiles=" + smiles + "' ALT='" + smiles + "' />";   
	}
    public String getDisplayURL(Object idsubstance,Object field) {
		String displayurl = displayURL;
		if ((data != null) && (data.get("displayurl") != null)) displayurl = data.get("displayurl").toString();
		
        StringBuffer b = new StringBuffer();
        b.append("<a href ='");
        b.append(displayurl);
        b.append("&id=");
        b.append(idsubstance.toString());
        b.append("' >");
        b.append(field);
        b.append("</a>");
        return b.toString();
    }
	public String getDisplayURL() {
		return displayURL;
	}
	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}    
}
/*
http://access1.sun.com/techarticles/simple.WAR.html

WAR file structure - 
The static HTML files and JSPs are stored in the top level directory. 
The Servlet and related Java[tm] technology class files must be stored in the WEB-INF/classes directory. 
Any auxiliary library JAR files must be stored in the WEB-INF/lib directory. 
The deployment descriptor is stored as a file named web.xml in the WEB-INF directory. 
Creating the web.xml deployment descriptor - 
The following is the mandatory header of the web.xml document. This defines the document as an XML file and relates the syntax of the file to the DOCTYPE resource specified.

<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE web-app
    PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN"
    "http://java.sun.com/j2ee/dtds/web-app_2_2.dtd">


Servlets 

The simplest form of deployment descriptor that is needed to deploy a Java[tm] Servlet is as follows (assumes the header is preceding):

<web-app>
    <servlet>
        <servlet-name>HelloServlet</servlet-name>
        <servlet-class>mypackage.HelloServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>HelloServlet</servlet-name>
        <url-pattern>/HelloServlet</url-pattern>
    </servlet-mapping>
</web-app>

The <servlet-name> element is how the Servlet is known within the XML file, the <servlet-class> is the fully qualified Java programming language class name of the Servlet; this Java programming language class needs to be located in the WEB-INF/classes directory. Thus, for the previous example this would be reflected in the directory structure: WEB-INF/classes/mypackage/HelloServlet.class.


The <url-pattern> is how the Servlet is referenced from a Universal Resource Indicator(URI) - see the "Accessing the Content" section of this article.  According to the Servlet specification it is recommended that you include a mapping to avoid any potential issues. All of these elements are documented in the Servlet specifications.

Note: The structure of the elements must follow the order in the DTD definition. For example all <servlet>s must be defined before any <servlet-mapping>s can be specified.

JSPs 

JSPs can be deployed in a WAR file in three ways:


Without reference in web.xml


You can put your JSPs directly in the root of the WAR file; the container will recognise the .jsp extension.


Precompiling


By precompiling the JSP, you have a Servlet.  You can reference the JSP's compiled class (Servlet) and also locate the class as in the example from the "Servlets" section of this article. However, the <url-pattern> could be something like Foo.jsp, to signify that it is/was a JSP. For more information, see the JSP Specification note in the References section of this article.


Referencing

You can reference the JSP file in web.xml; by using the example in the "Servlets" section and replacing <servlet-class> with <jsp-file>.  In this case the <jsp-file> root is the root of the WAR file, unlike the Servlet scenario.


Creating the WAR File

After you have created your WEB-INF directory, the deployment descriptor, and have put the relevant files in the correct directories, you can use the "jar" utility from the Java[tm] Development Kit distribution to create the WAR file.  Check the tools documentation for the full syntax.  The command you could use is (assumes you are at the top level of the directory structure in which you assembled the WAR contents):

jar cvf mywar.war WEB-INF {related top-level files or directories}

You can then deploy mywar.war using, for example, iPlanet[tm] Application Server, iPlanet[tm] Web Server or any J2EE compliant application server or Web container.


Accessing the Content

The content is generally accessed as follows:

http://host:port/context/{maping or file}



*/