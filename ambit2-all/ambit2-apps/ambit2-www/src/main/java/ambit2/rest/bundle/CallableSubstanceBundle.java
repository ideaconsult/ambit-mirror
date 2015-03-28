package ambit2.rest.bundle;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.substance.AddSubstanceToBundle;
import ambit2.db.update.bundle.substance.DeleteSubstanceFromBundle;
import ambit2.rest.substance.SubstanceURIReporter;

public class CallableSubstanceBundle extends CallableDBUpdateTask<SubstanceRecord, Form, String> {
	protected SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter;
	protected SubstanceEndpointsBundle bundle;
	private update_command command = update_command.add;
	
	public CallableSubstanceBundle(SubstanceEndpointsBundle item,
			SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter,
			Method method, Form input,Connection connection, String token) {
		super(method, input, connection, token);
		this.bundle = item;
		this.reporter = reporter;
	}

	@Override
	protected SubstanceRecord getTarget(Form input) throws Exception {
		if (Method.POST.equals(method)) {
			SubstanceRecord record = new SubstanceRecord();
			parseForm(input, record);
			command = update_command.add; 
			return record;
		} else if (Method.PUT.equals(method)) {
			command = null;
			SubstanceRecord record = new SubstanceRecord();
			parseForm(input, record);
			if (command != null) return record;
			else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected IQueryUpdate<? extends Object, SubstanceRecord> createUpdate(
			SubstanceRecord target) throws Exception {
		if (Method.POST.equals(method)) {
			return bundle!=null?new AddSubstanceToBundle(bundle,target):null; 
		}
		else if (Method.PUT.equals(method)) {
			switch (command) {
			case add: {
				return bundle!=null?new AddSubstanceToBundle(bundle,target):null; 
			}
			case delete: {
				return bundle!=null?new DeleteSubstanceFromBundle(bundle,target):null; 
			}
			}
		}		
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(SubstanceRecord target) throws Exception {
		return reporter.getURI(target);
	}
	protected void parseForm(Form input, SubstanceRecord bundle) throws ResourceException {
		try {
			//add/delete should be specified on PUT 
			command = update_command.valueOf(input.getFirstValue("command"));
		} catch (Exception x) { command = null;	}
		
		String uri = input.getFirstValue("substance_uri");
		if (uri==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		uri = uri.replaceAll(reporter.getBaseReference().toString(),"");
		bundle.setSubstanceUUID(uri.replace("/substance/", ""));
	}
}
