package ambit2.rest.model.predictor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.Iterator;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.rest.OpenTox;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

/**
 * Abstract class for all predictive models 
 * @author nina
 *
 * @param <NativeTypeItem>
 */
public abstract class ModelPredictor<Predictor,NativeTypeItem> extends AbstractDBProcessor<NativeTypeItem, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8267727629802363087L;
	protected ModelQueryResults model;
	public ModelQueryResults getModel() {
		return model;
	}
	protected boolean structureRequired = false;
	protected boolean valuesRequired = false;
	public boolean isValuesRequired() {
		return valuesRequired;
	}

	public void setValuesRequired(boolean valuesRequired) {
		this.valuesRequired = valuesRequired;
	}

	public boolean isStructureRequired() {
		return structureRequired;
	}

	public void setStructureRequired(boolean structureRequired) {
		this.structureRequired = structureRequired;
	}
	protected Predictor predictor;
	protected int classIndex;
	protected Instances header;
	protected Filter filter;
	protected String[] targetURI;
	protected ModelURIReporter modelReporter;
	protected PropertyURIReporter propertyReporter;
	
	public PropertyURIReporter getPropertyReporter() {
		return propertyReporter;
	}

	public void setPropertyReporter(PropertyURIReporter propertyReporter) {
		this.propertyReporter = propertyReporter;
	}

	public ModelURIReporter getModelReporter() {
		return modelReporter;
	}

	public void setModelReporter(ModelURIReporter modelReporter) {
		this.modelReporter = modelReporter;
	}

	protected Reference applicationRootReference;
	
	private Template compoundURITemplate;
	private Template conformerURITemplate;
	
	protected IStructureRecord record = new StructureRecord();
	
	public ModelPredictor(
			Reference applicationRootReference,
			ModelQueryResults model, 
			ModelURIReporter modelReporter, 
			PropertyURIReporter propertyReporter,
			String[] targetURI) throws ResourceException {
		super();
		this.applicationRootReference = applicationRootReference;
		this.model = model;
		predictor = createPredictor(model);
		this.targetURI = targetURI;
		this.modelReporter = modelReporter;
		this.propertyReporter = propertyReporter;
		compoundURITemplate = OpenTox.URI.compound.getTemplate(applicationRootReference);
		conformerURITemplate = OpenTox.URI.conformer.getTemplate(applicationRootReference);
		//read properties
		//createProfileFromReference(new Reference(modelReporter.getURI(model)+"/dependent"),null,model.getDependent());
		//createProfileFromReference(new Reference(modelReporter.getURI(model)+"/independent"),null,model.getPredictors());
		//createProfileFromReference(new Reference(modelReporter.getURI(model)+"/predicted"),null,model.getPredicted());

	}
	
	protected Instances getHeader(Form form) throws IOException {
		return new Instances(new StringReader(form.getFirstValue("header")));
	}
	protected int getClassIndex(Form form) throws Exception {
		String ci = form.getFirstValue("classIndex");
		try {
			return ci==null?-1:Integer.parseInt(ci);
		} catch (Exception x) { return -1;}
	}	
	/**
	 * @param predictor
	 * @return true if this is the expected object type, 
	 * @throws ResourceException
	 */
	protected boolean isSupported(Object predictor) throws  ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	protected void extractRecordID(String url,IStructureRecord record) throws AmbitException {
		Object id = OpenTox.URI.compound.getId(url, compoundURITemplate);
		if (id != null) record.setIdchemical((Integer)id);
		else {
			Object[] ids = OpenTox.URI.conformer.getIds(url, conformerURITemplate);
			if (ids[0]!=null) record.setIdchemical((Integer)ids[0]);
			if (ids[1]!=null) record.setIdstructure((Integer)ids[1]);
		}
	}	
	public Predictor createPredictor(ModelQueryResults model) throws ResourceException {
		 this.model = model;
		 ObjectInputStream ois = null;
		 try {
			Form form = new Form(model.getContent());
			 	
			InputStream in = new ByteArrayInputStream(Base64.decode(form.getFirstValue("model")));
			ois =  new ObjectInputStream(in);
		 	Object o = ois.readObject();
		 	if (o==null)  throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,String.format("Error when reading model %s",model.getName()));
		 	if (!isSupported(o))  throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Wrong model type %s",model.getName()));
	 	
		 	header = getHeader(form);
		 	classIndex = getClassIndex(form);
		 	if ((header != null) &&  (classIndex>=0) && (classIndex<header.numAttributes()))	
		 			header.setClassIndex(classIndex);
		 	String[] options = new String[2];
			options[0] = "-R";                                   
			options[1] = "1";                                   
			Remove remove  = new Remove();   
			try {                    
				remove.setOptions(options);                          
			} catch (Exception x) {};
		 	filter = remove;
		 	return (Predictor)o;
		 } catch (ResourceException x) {
			 throw x;
		 } catch (NumberFormatException x) { 
			 throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);		 	
		 } catch (IOException x) { 
			 throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE,x.getMessage(),x);
		 } catch (ClassNotFoundException x) {
			 throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE,x.getMessage(),x);
		 } catch (Exception x) {
			 throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);			 
		 } finally {
			 try {ois.close();} catch (Exception x) {} 
		 }
	}
	/*
	protected Profile createProfileFromReference(Reference ref, LiteratureEntry le) {
		return createProfileFromReference(ref, le,new Profile<Property>());
	}
	protected Profile createProfileFromReference(Reference ref, LiteratureEntry le, Profile profile) {
		
		RDFPropertyIterator reader = null;
		OntModel jenaModel = null;
		try {
			reader = new RDFPropertyIterator(ref);
			jenaModel = reader.getJenaModel();
			reader.setBaseReference(applicationRootReference);
			while (reader.hasNext()) {
				Property p = reader.next();
				p.setEnabled(true);
				profile.add(p);
			}
			
		} catch (Exception x) {
			Property p = new Property(ref.toString(),le);
			p.setEnabled(true);
			profile.add(p);
		} finally {
			try {reader.close(); } catch (Exception x) {}
			try {jenaModel.close(); } catch (Exception x) {}
		}
		return profile;	
	}		
	*/
	public abstract String getCompoundURL(NativeTypeItem target) throws AmbitException;
	public abstract Object predict(NativeTypeItem target) throws AmbitException;
	
	public IStructureRecord process(NativeTypeItem target) throws AmbitException {
		record.clear();
		extractRecordID(getCompoundURL(target),record);
		Object value = predict(target);
		assignResults(record, value);
		return record;
	}	
	
	public void assignResults(IStructureRecord record, Object value) throws AmbitException {
		Iterator<Property> predicted = model.getPredicted().getProperties(true);
		int count = 0;
		while (predicted.hasNext()) {
			record.setProperty(predicted.next(),value);
			count++;
		}
		if (count==0) throw new AmbitException("No property to assign results!!!");
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	
	public String createResultReference() throws Exception {
		
		if ((model.getId()==null) || (model.getId()<=0)) {

			if (model.getPredicted().size()==0) throw new Exception("No result features!");
			StringBuilder b = new StringBuilder();
			Iterator<Property> it = model.getPredicted().getProperties(true);
			String delimiter = "";
			int i=0;
			while (it.hasNext()) {
				Property p = it.next();
				b.append(String.format("%s%s=%s",
						delimiter,
						OpenTox.params.feature_uris.toString(),
						Reference.encode(propertyReporter.getURI(p))
						));
				delimiter = "&";
				i++;
			}
			return b.toString();
		}
		else {
			return String.format("%s=%s",
					OpenTox.params.feature_uris.toString(),
					Reference.encode(
						String.format("%s/predicted", getModelReporter().getURI(getModel()))
							)
					);
		}
		
	}
}
