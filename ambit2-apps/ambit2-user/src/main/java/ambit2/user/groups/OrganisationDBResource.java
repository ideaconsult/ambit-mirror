package ambit2.user.groups;

import java.sql.Connection;

import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.groups.CallableGroupCreator;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.GroupType;
import net.idea.restnet.groups.db.ReadGroup;
import net.idea.restnet.groups.db.ReadOrganisation;
import net.idea.restnet.groups.resource.GroupQueryURIReporter;
import net.idea.restnet.user.DBUser;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.user.rest.resource.Resources;
import ambit2.user.rest.resource.UserDBResource;

public class OrganisationDBResource extends GroupDBResource<DBOrganisation> {

	@Override
	public ReadGroup<DBOrganisation> createGroupQuery(Integer key, String search, String groupName) {
		DBOrganisation p = new DBOrganisation();
		p.setTitle(search);
		p.setGroupName(groupName);
		if (key!=null) p.setID(key);
		ReadOrganisation q = new ReadOrganisation(p);
		return q;
	}
	@Override
	public String getGroupBackLink() {
		return  Resources.organisation;
	}
	@Override
	public String getGroupTitle() {
		return GroupType.ORGANISATION.toString();
	}

	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, DBOrganisation item) throws ResourceException {
		Connection conn = null;
		try {
			DBUser user = null;
			Object userKey = getRequest().getAttributes().get(UserDBResource.resourceKey);		
			if ((userKey!=null) && userKey.toString().startsWith("U")) try {
				user = new DBUser(new Integer(Reference.decode(userKey.toString().substring(1))));
			} catch (Exception x) {}
			
			GroupQueryURIReporter r = new GroupQueryURIReporter(getRequest(),"");
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection();
			return new CallableGroupCreator(method,item,GroupType.ORGANISATION,user,r,form,getRequest().getRootRef().toString(),conn,getToken());
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	};

}
