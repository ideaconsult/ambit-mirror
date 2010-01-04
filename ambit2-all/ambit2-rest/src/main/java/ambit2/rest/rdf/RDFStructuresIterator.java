package ambit2.rest.rdf;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;

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
 * Parses RDF representation of OpenTox dataset into {@link IStructureRecord}
 * @author nina
 *
 */
public class RDFStructuresIterator extends RDFDataEntryIterator<IStructureRecord, Property> {


	public RDFStructuresIterator(Representation representation, MediaType mediaType) throws ResourceException {
		this(OT.createModel(representation,mediaType));
	}
	
	public RDFStructuresIterator(Reference reference) throws ResourceException {
		this(OT.createModel(reference, MediaType.APPLICATION_RDF_XML));
	}
	
	public RDFStructuresIterator(Reference reference,MediaType mediaType) throws ResourceException {
		this(OT.createModel(reference, mediaType));
	}
	
	public RDFStructuresIterator(InputStream in,MediaType mediaType) throws ResourceException {
		this(OT.createModel(in, mediaType));
	}	

	public RDFStructuresIterator(OntModel model, StmtIterator recordIterator) {
		this(model);
		this.recordIterator = recordIterator;
	}
	public RDFStructuresIterator(OntModel model) {
		super(model);
	}
	@Override
	protected Property createFeature(RDFNode feature) {
		Property key = Property.getInstance(feature.toString(),feature.toString());
		key.setClazz(URI.class);
		return key;
	}

	@Override
	protected void parseFeatureURI(String uri, Property property) {
		Map<String, Object> vars = new HashMap<String, Object>();
		featureTemplate.parse(uri, vars);
		try {property.setId(Integer.parseInt(vars.get(OpenTox.URI.feature.getKey()).toString())); } 
		catch (Exception x) {property.setId(-1);};
	}

	@Override
	public boolean readStructure(RDFNode target, IStructureRecord record) {
		Representation r = null;
		try {
			ClientResource client = new ClientResource(target.toString());
			r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF);
			if (client.getStatus().equals(Status.SUCCESS_OK)) {
				record.setContent(r.getText());
				record.setFormat(MOL_TYPE.SDF.toString());
			}

			return true;
		} catch (Exception x) {
			record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
			record.setContent(target.toString());	
			return false;
		} finally {
			try {r.release();} catch (Exception x) {}
		}
	}

	@Override
	protected void setFeatureValue(IStructureRecord record, Property key,
			RDFNode value) {
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

	@Override
	protected void setIDChemical(int idchemical) {
		record.setIdchemical(idchemical);
	}

	@Override
	protected void setIDConformer(int idstructure) {
		record.setIdstructure(idstructure);
	}

	@Override
	protected IStructureRecord createRecord() {
		if (record == null) return new StructureRecord();
		else record.clear(); return record;
	}
	@Override
	protected void parseFeatureValues(Resource dataEntry,IStructureRecord record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel)).getObject();
				
				String feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject().toString();
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
}
