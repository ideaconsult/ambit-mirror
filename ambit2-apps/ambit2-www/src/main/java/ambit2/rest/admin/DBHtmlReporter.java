package ambit2.rest.admin;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;

import ambit2.db.version.AmbitDBVersion;
import ambit2.db.version.DBVersionQuery;
import ambit2.rest.AmbitResource;

public class DBHtmlReporter extends QueryReporter<AmbitDBVersion,DBVersionQuery,Writer> {
	protected boolean create = false;
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	protected Request request;
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5343958374065342370L;

	public DBHtmlReporter(Request baseRef) {
		super();
		setRequest(baseRef);
	}
	
	@Override
	public void open() throws DbAmbitException {
	}


	@Override
	public void footer(Writer output, DBVersionQuery query) {
		try {
			
			AmbitResource.writeHTMLFooter(output, "OpenSSO User", getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
	public Object processItem(AmbitDBVersion item) throws AmbitException {
		StringBuilder content = new StringBuilder();
		try {
			if (isCreate()) {
				content.append("<form method='post' action='?method=post'>");
				content.append(String.format("<h4>The database %s does not exist. Please use the form below to create the database.</h4>",item.getDbname()));
				content.append(String.format("<h5>Do not create the database via MySQL console.</h5>",item.getDbname()));
			}
			content.append("<table>");
			

			if (isCreate()) {
				
				content.append(String.format("<tr><th>DB name</th><td><input type='text' size='40' name='dbname' value='%s' title='The database name has to match with the \"Database\" property, defined in {servlet-home}/WEB-INF/classes/ambit2/rest/config/ambit2.pref file'></td></tr>",item.getDbname()));
				content.append(String.format("<tr><th>Admin user name</th><td title='existing MySQL user with sufficient privileges to create a database'><input type='text' size='40' name='user' value='%s'></td></tr>",""));
				content.append(String.format("<tr><th>Password</th><td title='MySQL user password'><input type='password' size='40' name='pass' value='%s'></td></tr>",""));
				content.append("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Create database\"></td></tr>");
			} else {
				content.append(String.format("<tr><th>DB name</th><td>%s</td></tr>",item.getDbname()));
				content.append(String.format("<tr><th>Version</th><td>%d.%d</td></tr>",item.getMajor(),item.getMinor()));
				content.append(String.format("<tr><th>Created</th><td>%s</td></tr>",item.getCreated()));
				content.append(String.format("<tr><th>Note</th><td>%s</td></tr>",item.getComments()));
			}
			content.append("</table>");
			if (isCreate())
				content.append("</form>");
			
			output.write(AmbitResource.printWidget("Database configuration", content.toString()));
			return item;
	} catch (Exception x) { logger.log(Level.WARNING,x.getMessage(),x); return null;}
	}
	
	@Override
	public void header(Writer output, DBVersionQuery query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(),
					getRequest().getOriginalRef()==null?getRequest().getResourceRef():getRequest().getOriginalRef()
					,null
					);//,"<meta http-equiv=\"refresh\" content=\"10\">");
		} catch (Exception x) {
			
		}
	}
}