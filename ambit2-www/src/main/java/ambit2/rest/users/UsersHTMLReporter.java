package ambit2.rest.users;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.structure.DisplayMode;

public class UsersHTMLReporter extends QueryHTMLReporter<AmbitUser, IQueryRetrieval<AmbitUser>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	
	public UsersHTMLReporter() {
		this(null,DisplayMode.table,null);
	}
	public UsersHTMLReporter(Request baseRef, DisplayMode _dmode, ResourceDoc doc) {
		super(baseRef,_dmode,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new UsersURIReporter<IQueryRetrieval<AmbitUser>>(request,doc);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<AmbitUser> query) {
		super.header(w, query);
		
		try {w.write(String.format("<h3>User%s</h3>",_dmode.isCollapsed()?"s":""));} catch (Exception x) {}
	}
	@Override
	public Object processItem(AmbitUser item) throws AmbitException {
		try {
			
			output.write("<div>");
			output.write("Name: ");
			output.write(String.format(
						"<a href=\"%s\">%s</a> %s&nbsp;%s&nbsp;%s",
						uriReporter.getURI(item),
						item.getName(),
						item.getFirstName(),
						item.getLastName(),
						item.getEmail()));
			if (!_dmode.isCollapsed()) {
				output.write(
				"<form id='modifyUser' method='post' action=''>\n"+
				"<p>Change your password:\n"+
				"<input class='password' name='password' />\n"+
				"<input type='submit' /></p>\n"+
				"</form>");
			}
			
			output.write("</div>");	
		} catch (Exception x) {
			
		}
		return null;
	}

}