package ambit2.rest.task;

import java.sql.Connection;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.processors.RepositoryWriter;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.task.dbpreprocessing.CallableDBProcessing;

public class CallableStructureEntry<USERID> extends CallableDBProcessing<USERID> {
	protected IStructureRecord record;
	protected CompoundURIReporter cmpreporter;
	
	public CallableStructureEntry(Form form,
			Reference applicationRootReference, Context context,
			Algorithm algorithm, USERID token) {
		super(form, applicationRootReference, context, algorithm, token);
		cmpreporter = new CompoundURIReporter(applicationRootReference,null);
	}
	private static String customidname = "customidname";
	private static String customid = "customid";
	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		record = new StructureRecord();
		record.setFormat(null);
		record.setContent(null);
		String[] tags = new String[] {
				Property.opentox_CAS,Property.opentox_EC,
				Property.opentox_Name,Property.opentox_TradeName,Property.opentox_IupacName,
				Property.opentox_SMILES,
				Property.opentox_InChI,Property.opentox_InChIKey,
				Property.opentox_InChI_std,Property.opentox_InChIKey_std,
				Property.opentox_IUCLID5_UUID
				};
		for (String tag : tags) {
			String localtag = tag.replace("http://www.opentox.org/api/1.1#","");
			String value = form.getFirstValue(localtag);
			if (value==null) continue;
			value = value.trim();
			if ("".equals(value)) continue;
			if (Property.opentox_CAS.equals(tag)) record.setProperty(Property.getCASInstance(), value);
			else if (Property.opentox_EC.equals(tag)) record.setProperty(Property.getEINECSInstance(), value);
			else if (Property.opentox_Name.equals(tag)) record.setProperty(Property.getNameInstance(), value);
			else if (Property.opentox_TradeName.equals(tag)) record.setProperty(Property.getNameInstance(), value);
			else if (Property.opentox_IupacName.equals(tag)) record.setProperty(Property.getNameInstance(), value);
			else if (Property.opentox_IUCLID5_UUID.equals(tag)) record.setProperty(Property.getI5UUIDInstance(), value);
			else if (Property.opentox_SMILES.equals(tag)) {record.setSmiles(value);}
			else if (Property.opentox_InChI_std.equals(tag)) record.setInchi(value);
			else if (Property.opentox_InChI.equals(tag)) record.setInchi(value);
			else if (Property.opentox_InChIKey.equals(tag)) record.setInchiKey(value);
			else if (Property.opentox_InChIKey_std.equals(tag)) record.setInchiKey(value);
		}
		String molfile = form.getFirstValue("molfile");
		if (molfile!=null) {
			record.setFormat("SDF");
			record.setContent(molfile);
		} else if (record.getInchi()!=null) {
			record.setContent(record.getInchi());
			record.setFormat(MOL_TYPE.INC.name());
		}
		String id = form.getFirstValue(customidname);
		if ((id!=null) && !"".equals(id.trim())) {
			String value = form.getFirstValue(customid);
			if ((value!=null) && !"".equals(value.trim())) {
				record.setProperty(Property.getInstance(id, LiteratureEntry.getInstance()), value);
			}
		}
	}

	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		return null;
	}
	
	
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		return null;
	}

	@Override
	protected TaskResult createReference(Connection connection)	throws Exception {
		final RepositoryWriter writer = new RepositoryWriter();
		//writer.setPropertiesOnly(isPropertyOnly());
		//writer.setPropertyKey(getMatcher());
		//writer.setDataset(dataset);
		writer.setConnection(connection);
		writer.setCloseConnection(true);
		try {
			List<IStructureRecord> records = writer.write(record);
			for (IStructureRecord result: records) {
				//what about policies
				return new TaskResult(cmpreporter.getURI(result),false);
			}
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (Exception x) {
			throw x;
		} finally {
			writer.close();
		}
	}

}
