package ambit2.rest.task;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Template;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OT;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Reads RDF representation of a dataset and converts it into iterator of IStructureRecord
 * @author nina
 *
 */
public class RDFReader extends AbstractBatchProcessor<Reference, IStructureRecord>	{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8763491584837871656L;
	protected MediaType mediaType = MediaType.APPLICATION_RDF_XML;
	protected OntModel jenaModel;
	protected IStructureRecord record;
	protected StmtIterator dataEntryIterator;
	protected String baseReference;
	protected Template compoundTemplate;
	protected Template conformerTemplate;
	protected Template featureTemplate;

	public RDFReader(String baseReference) {
		super();
		this.baseReference = baseReference;
		compoundTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,CompoundResource.compoundID));
		conformerTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,ConformerResource.conformerID));
		featureTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,PropertyResource.featuredefID));
		
	}
	public String getBaseReference() {
		return baseReference;
	}
	public void setBaseReference(String baseReference) {
		this.baseReference = baseReference;
	}
	@Override
	public void beforeProcessing(Reference target) throws AmbitException {
		super.beforeProcessing(target);
		try {
			ClientResource client = new ClientResource(target);
			Representation r = client.get(mediaType);
			if (client.getStatus().equals(Status.SUCCESS_OK)) {
				record = new StructureRecord();
				jenaModel = OT.createModel();
				jenaModel.read(r.getStream(),null);
			}
			
		} catch (Exception x) {
			throw new AmbitException(x);
		}		
	}
	@Override
	public void afterProcessing(Reference target,
			Iterator<IStructureRecord> iterator) throws AmbitException {
		try {dataEntryIterator.close();} catch (Exception x){};
		super.afterProcessing(target, iterator);
	}
	protected void parseDataEntry(Resource dataEntry,IStructureRecord record) {
		//get the compound
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.compound,(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			parseCompoundURI(st.getObject().toString(),record);
			try {
				readStructure(st.getObject().toString(), record);
				break;
			} catch (Exception x) {
				record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
				record.setContent(st.getObject().toString());				
			}
		}	
		//get feature values
		parseFeatureValues( dataEntry,record);
	}
	protected void parseFeatureValues(Resource dataEntry,IStructureRecord record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.values,(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.value).getObject();
				
				String feature = fv.getProperty(OT.feature).getObject().toString();
				Property key = Property.getInstance(feature,feature);
				parseFeatureURI(feature, key);
				key.setClazz(URI.class);
				
				if (value.isLiteral()) {
					RDFDatatype datatype = ((Literal)value).getDatatype();
					if (XSDDatatype.XSDdouble.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getDouble());
					else if (XSDDatatype.XSDfloat.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getFloat());
					else if (XSDDatatype.XSDinteger.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getInt());		
					else if (XSDDatatype.XSDstring.equals(datatype)) 
						record.setProperty(key, value.toString());
				}
	
			}
		}
	}

/*
 * (non-Javadoc)
 * @see ambit2.base.interfaces.IBatchProcessor#getIterator(java.lang.Object)
 */
	public Iterator<IStructureRecord> getIterator(Reference target)
			throws AmbitException {
			
		return new Iterator<IStructureRecord>() {
			public boolean hasNext() {
				if (dataEntryIterator == null) {
					Resource s = OT.OTClass.DataEntry.getOntClass(jenaModel);
					if (s==null) return false;
					dataEntryIterator =  jenaModel.listStatements(new SimpleSelector(null,null,s));
				}
				if ((dataEntryIterator!=null) && dataEntryIterator.hasNext()) {
						Statement st = dataEntryIterator.next();
						Resource dataEntry = (Resource) st.getSubject();
						record.clear();
						parseDataEntry( dataEntry,record);
						return true;
				}
				return false;
			}
			public IStructureRecord next() {
				return record;
			}
			public void remove() {
			}
		};
	}

	protected void parseFeatureURI(String uri,Property property) {
		Map<String, Object> vars = new HashMap<String, Object>();
		featureTemplate.parse(uri, vars);
		try {property.setId(Integer.parseInt(vars.get(PropertyResource.idfeaturedef).toString())); } 
		catch (Exception x) {property.setId(-1);};

	}	
	protected void parseCompoundURI(String uri,IStructureRecord record) {
		Map<String, Object> vars = new HashMap<String, Object>();
		conformerTemplate.parse(uri, vars);
		try {record.setIdchemical(Integer.parseInt(vars.get(CompoundResource.idcompound).toString())); } 
		catch (Exception x) {};
		
		try {record.setIdstructure(Integer.parseInt(vars.get(ConformerResource.idconformer).toString())); } 
		catch (Exception x) {};
		
		if (record.getIdchemical()<=0) {
			try {
			compoundTemplate.parse(uri, vars);
			record.setIdchemical(Integer.parseInt(vars.get(CompoundResource.idcompound).toString())); } 
			catch (Exception x) {};
		}
	}
	
	public void readStructure(String target,IStructureRecord record) throws ResourceException,IOException {
			ClientResource client = new ClientResource(target);
			Representation r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF);
			if (client.getStatus().equals(Status.SUCCESS_OK)) {
				record.setContent(r.getText());
				record.setFormat(MOL_TYPE.SDF.toString());
			}

	}
}
