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
import ambit2.rest.substance.SubstanceURIReporter;

public class CallableSubstanceBundle extends CallableDBUpdateTask<SubstanceRecord, Form, String> {
	protected SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter;
	protected SubstanceEndpointsBundle bundle;
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
			return record;
		}
		/*
		else if (Method.DELETE.equals(method)) { 
				return bundle;
		} else if (Method.PUT.equals(method)) {
			parseForm(input, bundle);
			return bundle;
		}
		*/
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected IQueryUpdate<? extends Object, SubstanceRecord> createUpdate(
			SubstanceRecord target) throws Exception {
		if (Method.POST.equals(method)) {
			return bundle!=null?new AddSubstanceToBundle(bundle,target):null; 
		}
		/*
		else if (Method.DELETE.equals(method)) {
			return item!=null?new DeleteBundle(item):null; //new DeleteSubstancesPerBundle(user,group);
		}
		else if (Method.PUT.equals(method)) 
			return item!=null?new UpdateBundle(item):null;
			*/ 
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(SubstanceRecord target) throws Exception {
		return reporter.getURI(target);
	}
	protected void parseForm(Form input, SubstanceRecord bundle) throws ResourceException {
		String uri = input.getFirstValue("substance_uri");
		if (uri==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		uri = uri.replaceAll(reporter.getBaseReference().toString(),"");
		bundle.setCompanyUUID(uri.replace("/substance/", ""));
	}
}
