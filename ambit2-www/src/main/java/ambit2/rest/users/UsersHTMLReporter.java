package ambit2.rest.users;

import java.io.Writer;

import org.restlet.data.Request;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

public class UsersHTMLReporter extends QueryHTMLReporter<AmbitUser, IQueryRetrieval<AmbitUser>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	
	public UsersHTMLReporter() {
		this(null,true);
	}
	public UsersHTMLReporter(Request baseRef, boolean collapsed) {
		super(baseRef,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new UsersURIReporter<IQueryRetrieval<AmbitUser>>(request);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<AmbitUser> query) {
		super.header(w, query);
		
		try {w.write(String.format("<h3>User%s</h3>",collapsed?"s":""));} catch (Exception x) {}
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
			if (!collapsed) {
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