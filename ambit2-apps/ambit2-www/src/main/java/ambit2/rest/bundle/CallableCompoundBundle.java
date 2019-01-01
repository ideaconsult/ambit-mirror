package ambit2.rest.bundle;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.bundle.chemicals.AddChemicalToBundle;
import ambit2.db.update.bundle.chemicals.DeleteChemicalsFromBundle;
import ambit2.rest.OpenTox;
import ambit2.rest.structure.CompoundURIReporter;

public class CallableCompoundBundle  extends CallableDBUpdateTask<IStructureRecord, Form, String> {
	protected CompoundURIReporter<IQueryRetrieval<IStructureRecord>> reporter;
	protected SubstanceEndpointsBundle bundle;
	private Template compoundURITemplate;
	private Template conformerURITemplate;
	private update_command command = update_command.add;
	
	public CallableCompoundBundle(SubstanceEndpointsBundle item,
			CompoundURIReporter<IQueryRetrieval<IStructureRecord>> reporter,
			Method method, Form input,Connection connection, String token) {
		super(method, input, connection, token);
		this.bundle = item;
		this.reporter = reporter;
		
		Reference ref = new Reference(reporter.getBaseReference());
		compoundURITemplate = OpenTox.URI.compound.getTemplate(ref);
		conformerURITemplate = OpenTox.URI.conformer.getTemplate(ref);
	}

	@Override
	protected IStructureRecord getTarget(Form input) throws Exception {
		if (Method.POST.equals(method)) {
			IStructureRecord record = new StructureRecord();
			parseForm(input, record);
			command = update_command.add; 			
			return record;
		} else if (Method.PUT.equals(method)) {
			command = null;
			IStructureRecord record = new StructureRecord();
			parseForm(input, record);
			if (command != null) return record;
			else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected IQueryUpdate<? extends Object, IStructureRecord> createUpdate(IStructureRecord target) throws Exception {
		if (Method.POST.equals(method)) {
			return bundle!=null?new AddChemicalToBundle(bundle,target):null; 
		}
		else if (Method.PUT.equals(method)) {
			switch (command) {
			case add: {
				return bundle!=null?new AddChemicalToBundle(bundle,target):null; 
			}
			case delete: {
				return bundle!=null?new DeleteChemicalsFromBundle(bundle,target):null; 
			}
			}
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(IStructureRecord target) throws Exception {
		return reporter.getURI(target);
	}
	protected void parseForm(Form input, IStructureRecord record) throws ResourceException {
		try {
			//add/delete should be specified on PUT 
			command = update_command.valueOf(input.getFirstValue("command"));
		} catch (Exception x) { command = null;	}

		String tag = null;
		String remarks = null;
		try {
			tag = input.getFirstValue("tag");
		} catch (Exception x) { tag = null;	}
		try {
			remarks = input.getFirstValue("remarks");
		} catch (Exception x) { remarks = null;	}
		
		LiteratureEntry ref = LiteratureEntry.getBundleReference(bundle);
		if (tag!=null) record.setRecordProperty(new Property("tag",ref) ,tag);
		if (remarks!=null) record.setRecordProperty(new Property("remarks",ref) ,remarks);
		
		String uri = input.getFirstValue("compound_uri");
		if (uri==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		
		Object id = OpenTox.URI.compound.getId(uri, compoundURITemplate);
		if (id != null) record.setIdchemical((Integer)id);
		else {
			Object[] ids = OpenTox.URI.conformer.getIds(uri, conformerURITemplate);
			if (ids[0]!=null) record.setIdchemical((Integer)ids[0]);
			if (ids[1]!=null) record.setIdstructure((Integer)ids[1]);
		}
	}		
}
