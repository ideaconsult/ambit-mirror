package ambit2.rest.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.iterator.DefaultIteratingChemObjectReader;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.util.Template;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.io.IRawReader;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OT;
import ambit2.rest.property.RDFPropertyParser;
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
 * Reading RDF representation of a dataset. This is a copy paste from {@link RDFStructuresReader}
 * TODO refactor to avoid duplicate code
 * @author nina
 *
 */
public class RDFIteratingReader extends DefaultIteratingChemObjectReader
		implements IRawReader<IStructureRecord> {
	protected Template compoundTemplate;
	protected Template conformerTemplate;
	protected Template featureTemplate;	
	protected StmtIterator recordIterator;
	protected OntModel jenaModel;
	protected IStructureRecord record;
	protected String rdfFormat;
	protected Hashtable<String, Property> lookup;
	protected String basereference;
	
	public RDFIteratingReader() {

	}
	
	public RDFIteratingReader(InputStream in, IChemObjectBuilder builder,String baseReference, String rdfFormat) throws Exception {
		super();
		this.basereference = baseReference;
		this.rdfFormat = rdfFormat;
		jenaModel = OT.createModel();
		jenaModel.read(in,null,rdfFormat==null?"RDF/XML":rdfFormat);
		compoundTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,CompoundResource.compoundID));
		conformerTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,ConformerResource.conformerID));


    }	
	
	private StmtIterator initIterator() {
		if (recordIterator == null) {
			Resource s = OT.OTClass.DataEntry.getOntClass(jenaModel);
			if (s==null) return null;
			recordIterator =  jenaModel.listStatements(new SimpleSelector(null,null,s));
		}
		return recordIterator;
	}	

	public IStructureRecord nextRecord() {
		return record;
	}

	public void close() throws IOException {
		
	}

	public IResourceFormat getFormat() {
		 return MDLV2000Format.getInstance();
	}

	public boolean hasNext() {
		recordIterator = initIterator();
		if ((recordIterator!=null) && recordIterator.hasNext()) {
				Statement st = recordIterator.next();
				Resource newEntry = (Resource) st.getSubject();
				record = parseRecord( newEntry,createRecord());
				return true;
		} else return false;
	}
	protected IStructureRecord createRecord() {
		if (record == null) return new StructureRecord();
		else record.clear(); return record;
	}
	public Object next() {
		return  record;
	}
	protected IStructureRecord parseRecord(Resource newEntry, IStructureRecord record) {
		//get the compound
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			parseCompoundURI(st.getObject().toString(),record);
			if (readStructure(st.getObject(), record))
				break;

		}	
		//get feature values
		parseFeatureValues( newEntry,record);
		return record;
	}
	
	
	protected void parseFeatureValues(Resource dataEntry,IStructureRecord record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel)).getObject();
				
				RDFNode feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject();
				
				Property key = null;
				if (lookup!= null) key = lookup.get(feature.toString());
				if (key==null) key = readFeature(feature,null);		
				
				if (value.isLiteral()) {
					RDFDatatype datatype = ((Literal)value).getDatatype();
					if (XSDDatatype.XSDdouble.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getDouble());
					else if (XSDDatatype.XSDfloat.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getFloat());
					else if (XSDDatatype.XSDinteger.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getInt());		
					else if (XSDDatatype.XSDstring.equals(datatype)) 
						record.setProperty(key, ((Literal)value).getString());
				}
	
			}
		}
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
	
	public boolean readStructure(RDFNode target,IStructureRecord record) {
		try {
			ClientResource client = new ClientResource(target.toString());
			Representation r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF);
			if (client.getStatus().equals(Status.SUCCESS_OK)) {
				record.setContent(r.getText());
				record.setFormat(MOL_TYPE.SDF.toString());
			}
			return true;
		} catch (Exception x) {
			record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
			record.setContent(target.toString());	
			return false;
		}

	}
	
	public Property readFeature(final RDFNode target,Property property) {
		try {
			RDFPropertyParser parser = new RDFPropertyParser(basereference) {
				@Override
				protected Property parseRecord(Resource newEntry,
						Property record) {
					Property p = super.parseRecord(newEntry, record);
					if (lookup == null) lookup = new Hashtable<String, Property>();
					lookup.put(target.toString(), p);
					return p;
				}
			};
			parser.process(new Reference(target.toString()));
		} catch (Exception x) {
			String uri = target.toString();
			if (lookup == null) lookup = new Hashtable<String, Property>();
			lookup.put(uri, new Property(uri,new LiteratureEntry(uri,uri)));
		}
		return lookup.get(target.toString());
	}	
	
	protected void setFeatureValue(IStructureRecord record, Property key, RDFNode value) {
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
	
	protected Property createFeature(RDFNode feature) {
		Property key = Property.getInstance(feature.toString(),feature.toString());
		key.setClazz(URI.class);
		return key;
	}	
}
