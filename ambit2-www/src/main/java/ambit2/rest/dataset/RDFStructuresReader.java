package ambit2.rest.dataset;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OT;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
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
public class RDFStructuresReader extends RDFDatasetParser<IStructureRecord,Property>	{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8763491584837871656L;
	

	public RDFStructuresReader(String baseReference) {
		super(baseReference);
	}
	@Override
	protected IStructureRecord createRecord() {
		if (record == null) return new StructureRecord();
		else record.clear(); return record;
	}

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
	@Override
	protected Property createFeature(RDFNode feature) {
		Property key = Property.getInstance(feature.toString(),feature.toString());
		key.setClazz(URI.class);
		return key;
	}
}
