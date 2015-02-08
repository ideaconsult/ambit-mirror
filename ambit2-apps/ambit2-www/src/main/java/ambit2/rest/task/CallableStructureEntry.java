package ambit2.rest.task;

import java.sql.Connection;
import java.util.List;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.data.model.Algorithm;
import ambit2.core.processors.structure.key.CASKey;
import ambit2.db.processors.RepositoryWriter;
import ambit2.rest.OpenTox;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.task.dbpreprocessing.CallableDBProcessing;

public class CallableStructureEntry<USERID> extends CallableDBProcessing<USERID> {
	protected IStructureRecord record;
	protected CompoundURIReporter cmpreporter;
	protected boolean propertyOnly = false;
	private Template compoundURITemplate;
	private Template conformerURITemplate;
	
	public boolean isPropertyOnly() {
		return propertyOnly;
	}

	public void setPropertyOnly(boolean propertyOnly) {
		this.propertyOnly = propertyOnly;
	}
	
	public CallableStructureEntry(Form form,
			Reference applicationRootReference,
			IStructureRecord record,
			 Context context,
			Algorithm algorithm, USERID token) {
		super(form, applicationRootReference, context, algorithm, token);
		cmpreporter = new CompoundURIReporter(applicationRootReference);
		//overwrite the compound_uri parameter
		if (record.getIdchemical()>0) {
			this.record.setIdchemical(record.getIdchemical());
			this.record.setIdstructure(record.getIdstructure());
		}
	}
	private static String customidname = "customidname";
	private static String customid = "customid";
	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		compoundURITemplate = OpenTox.URI.compound.getTemplate(applicationRootReference);
		conformerURITemplate = OpenTox.URI.conformer.getTemplate(applicationRootReference);
		record = new StructureRecord();
		record.setFormat(null);
		record.setContent(null);
		String[] tags = new String[] {
				Property.opentox_CAS,Property.opentox_EC,
				Property.opentox_Name,Property.opentox_TradeName,Property.opentox_IupacName,
				Property.opentox_SMILES,
				Property.opentox_InChI,Property.opentox_InChIKey,
				Property.opentox_InChI_std,Property.opentox_InChIKey_std,
				Property.opentox_IUCLID5_UUID,
				"compound_uri"
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
			else if ("compound_uri".equals(tag)) try {
				extractRecordID(value,record);
			} catch (Exception x) {logger.warning(tag + x.getMessage());}
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
	
	protected void extractRecordID(String url,IStructureRecord record) throws AmbitException {
		String cleanURI = org.opentox.rdf.OpenTox.removeDatasetFragment(url);
		Object id = OpenTox.URI.compound.getId(cleanURI, compoundURITemplate);
		if (id != null) record.setIdchemical((Integer)id);
		else {
			Object[] ids = OpenTox.URI.conformer.getIds(cleanURI, conformerURITemplate);
			if (ids[0]!=null) record.setIdchemical((Integer)ids[0]);
			if (ids[1]!=null) record.setIdstructure((Integer)ids[1]);
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
		writer.setUseExistingStructure(isPropertyOnly());
		writer.setPropertyKey(new CASKey());
		SourceDataset dataset;
		if (isPropertyOnly()) {
			dataset = new SourceDataset("Properties registration",LiteratureEntry.getInstance());	
		} else
			dataset = new SourceDataset("Chemical structure registration",LiteratureEntry.getInstance());
		
		dataset.setStars(6);
		writer.setDataset(dataset);
		writer.setConnection(connection);
		writer.setCloseConnection(true);
		try {
			List<IStructureRecord> records = writer.write(record);
			for (IStructureRecord result: records) {
				//what about policies
				if (isPropertyOnly()) {
					return new TaskResult(String.format("%s?feature_uris[]=%s/dataset/Properties+registration/feature",
							cmpreporter.getURI(result),applicationRootReference),false);
				} else {
					return new TaskResult(String.format("%s?feature_uris[]=%s/dataset/Chemical+structure+registration/feature",
							cmpreporter.getURI(result),applicationRootReference),false);
				}
			}
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		} catch (Exception x) {
			throw x;
		} finally {
			writer.close();
		}
	}

}
