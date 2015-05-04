package ambit2.user.rest.resource;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.i.tools.JSONUtils;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

public class SimpleUserJSONReporter <Q extends IQueryRetrieval<DBUser>> extends QueryReporter<DBUser, Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4566136103208284105L;
    protected String comma = "";
    protected Request request;
    protected QueryURIReporter uriReporter;

    public Request getRequest() {
	return request;
    }

    public void setRequest(Request request) {
	this.request = request;
    }

    protected Reference baseReference;

    public Reference getBaseReference() {
	return baseReference;
    }
    protected boolean addPublicGroup = true;

    public SimpleUserJSONReporter(Request request, String usersdbname, boolean addPublicGroup) {
	this.baseReference = (request == null ? null : request.getRootRef());
	setRequest(request);
	uriReporter = new UserURIReporter(request, "");
	this.addPublicGroup = addPublicGroup;
    }

    @Override
    public Object processItem(DBUser user) throws Exception {
	try {
	    getOutput().write(comma);
	    comma = ",";
	    getOutput()
		    .write(String.format(
			    format,
			    JSONUtils.jsonEscape(user.getUserName()),
			    JSONUtils.jsonEscape((user.getTitle()==null?"":user.getTitle()+" ") + user.getFirstname() + " " + user.getLastname())
			    ));
	    
	} catch (IOException x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return null;
    }

    private static String format = "{\"id\":\"%s\",\"name\":\"%s\"}";

    // output.write("Title,First name,Last name,user name,email,Keywords,Reviewer\n");

    @Override
    public void footer(Writer output, Q query) {
	try {
	    output.write("\n]");
	} catch (Exception x) {
	}
    };

    @Override
    public void header(Writer output, Q query) {
	try {
	    output.write("[");
	    if (addPublicGroup) {
		output.write("{\"id\":\"g_ambit_users\",\"name\":\"Public\"}");
		comma = ",";
	    }
	} catch (Exception x) {
	}

    };

    public void open() throws DbAmbitException {

    }

    @Override
    public void close() throws Exception {
	setRequest(null);
	super.close();
    }

    @Override
    public String getFileExtension() {
	return null;
    }
}