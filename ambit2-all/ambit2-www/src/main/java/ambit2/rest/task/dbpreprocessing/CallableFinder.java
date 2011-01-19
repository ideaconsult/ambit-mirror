package ambit2.rest.task.dbpreprocessing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.processors.AbstractRepositoryWriter.OP;
import ambit2.db.search.property.ValuesReader;
import ambit2.pubchem.CSLSRequest;
import ambit2.pubchem.NCISearchProcessor.METHODS;
import ambit2.rest.OpenTox;
import ambit2.rest.rdf.RDFPropertyIterator;
import ambit2.rest.task.CallableQueryProcessor;
import ambit2.rest.task.TaskResult;
import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTFeature;

public class CallableFinder<USERID> extends	CallableQueryProcessor<Object, IStructureRecord,USERID>  {
	protected Reference applicationRootReference;
	protected Template profile;
	
	public CallableFinder(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,USERID token) {
		super(form,context,token);
		this.applicationRootReference = applicationRootReference;
	}
	@Override
	protected void processForm(Form form) {
		Object dataset = OpenTox.params.dataset_uri.getFirstValue(form);
		String[] xvars = OpenTox.params.feature_uris.getValuesArray(form);
		if (xvars != null) try {
			profile = new Template();
			
			OTDataset ds = OTDataset.dataset(dataset.toString());
			for (String xvar :xvars) {
				String[] xx = xvar.split("\n");
				for (String x : xx )
					if (!x.trim().equals("")) {
						ds = ds.addColumns(OTFeature.feature(x));
						Property key = createPropertyFromReference(x,null);
						key.setEnabled(true);
						profile.add(key);
					}
			}
			dataset =  ds.getUri().toString();

		} catch (Exception x) {
			
		}

		this.sourceReference = dataset==null?null:new Reference(dataset.toString().trim());
	}
	
	@Override
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		IProcessor<IStructureRecord,IStructureRecord> valuesReader = getValuesReader();
		if (valuesReader!=null) p.add(valuesReader);
		p.add(new CSLSFinder(profile));
		
		RepositoryWriter writer = new RepositoryWriter() {
			@Override
			public List<IStructureRecord> process(IStructureRecord target)
					throws AmbitException {
				if (target == null) return null;
				return super.process(target);
			}
		};
		writer.setOperation(OP.UPDATE);
		writer.setDataset(new SourceDataset("CSLS",new LiteratureEntry(CSLSRequest.CSLS_URL,CSLSRequest.CSLS_URL)));
		p.add(writer);
		return p;
	}
	

	@Override
	protected TaskResult createReference(Connection connection)
			throws Exception {
		return new TaskResult(sourceReference.toString(),false);
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		if (profile.size()==0) throw new Exception("No columns to search!");
		try {
			Object q = getQueryObject(reference, applicationRootReference);
			return q==null?reference:q;
		} catch (Exception x) {
			return reference;
		}
	}
	protected Property createPropertyFromReference(String attribute, LiteratureEntry le) {
		RDFPropertyIterator reader = null;
		try {
			reader = new RDFPropertyIterator(new Reference(attribute));
			reader.setBaseReference(applicationRootReference);
			while (reader.hasNext()) {
				return reader.next();
			}
			return null;	
		} catch (Exception x) {
			return new Property(attribute.toString(),le);
		} finally {
			try {reader.close(); } catch (Exception x) {}
		}
	}	
	
	protected IProcessor<IStructureRecord,IStructureRecord> getValuesReader() {
		if  (profile.size()>0) {
			ValuesReader readProfile = new ValuesReader(null);  //no reader necessary
			readProfile.setProfile(profile);
			return readProfile;
		} else return null;
	}
}

class CSLSFinder extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7059985528732316425L;
	protected CSLSRequest<String> request;
	protected Template profile;
	public CSLSFinder(Template profile) {
		super();
		this.profile = profile;
		request =  new CSLSRequest<String>() {
			@Override
			protected String read(InputStream in) throws Exception {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringWriter w = new StringWriter();
				String line = null;
				while ((line = reader.readLine())!=null) {
					w.write(line);
					w.write("\n");
				}
				return w.toString();
			}
		};
		request.setRepresentation(METHODS.sdf);
	}
	@Override
	public IStructureRecord process(IStructureRecord target)
			throws AmbitException {
		try {
			Iterator<Property> keys = profile.getProperties(true);
			while (keys.hasNext()) {
				Property key = keys.next();
				String content = request.process(target.getProperty(key).toString());
				if (content!= null) { 
					
					target.setContent(content);
					target.setFormat(MOL_TYPE.SDF.toString());
					if (STRUC_TYPE.NA != target.getType()) target.setIdstructure(-1) ;
					return target;
				}
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}

}