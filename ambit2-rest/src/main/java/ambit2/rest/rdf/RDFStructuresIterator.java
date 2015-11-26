package ambit2.rest.rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.rdf.ns.OT;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
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
	protected Hashtable<String, Property> lookup;
	protected RDFPropertyIterator propertyIterator;
	
	public RDFStructuresIterator(Representation representation, MediaType mediaType) throws ResourceException {
		this(OT.createModel(null,representation,mediaType));
	}
	
	public RDFStructuresIterator(Reference reference) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, MediaType.APPLICATION_RDF_XML));
	}
	
	public RDFStructuresIterator(Reference reference,MediaType mediaType) throws ResourceException,MalformedURLException,IOException {
		this(OT.createModel(null,reference, mediaType));
	}
	
	public RDFStructuresIterator(InputStream in,MediaType mediaType) throws ResourceException ,MalformedURLException,IOException{
		this(OT.createModel(null,in, mediaType));
	}	

	public RDFStructuresIterator(Model model, StmtIterator recordIterator) {
		this(model);
		this.recordIterator = recordIterator;
	}
	public RDFStructuresIterator(Model model) {
		super(model);
		lookup = new Hashtable<String, Property>();
		propertyIterator = new RDFPropertyIterator(model);
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
		InputStream in = null;
		HttpURLConnection uc = null;
		try {
			if (target.isURIResource()) {
				uc = ClientResourceWrapper.getHttpURLConnection(((Resource)target).getURI(), "GET", ChemicalMediaType.CHEMICAL_MDLSDF.toString());
				uc.setFollowRedirects(true);
				if (HttpURLConnection.HTTP_OK== uc.getResponseCode()) {
					in = uc.getInputStream();
					StringBuilder b = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = null;
					final String newline = System.getProperty("line.separator");
					while ((line = reader.readLine())!=null) {
						b.append(line);
						b.append(newline);
					}	
					record.setContent(b.toString());
					record.setFormat(MOL_TYPE.SDF.toString());
				}
	
				return true;
			} else {
				record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
				record.setContent(target.toString());	
				return false;					
			}
		} catch (Exception x) {
			record.setFormat(IStructureRecord.MOL_TYPE.URI.toString());
			record.setContent(target.toString());	
			return false;
		} finally {
			try {if (in != null) in.close();} catch (Exception x) {}
			try {if (uc != null) uc.disconnect();} catch (Exception x) {}
		}
	}

	@Override
	protected void setFeatureValue(IStructureRecord record, Property key,
			RDFNode value) {
		if (value.isLiteral()) {
			RDFDatatype datatype = ((Literal)value).getDatatype();
			if (XSDDatatype.XSDdouble.equals(datatype)) 
				record.setRecordProperty(key, ((Literal)value).getDouble());
			else if (XSDDatatype.XSDfloat.equals(datatype)) 
				record.setRecordProperty(key, ((Literal)value).getFloat());
			else if (XSDDatatype.XSDinteger.equals(datatype)) 
				record.setRecordProperty(key, ((Literal)value).getInt());		
			else if (XSDDatatype.XSDstring.equals(datatype)) 
				record.setRecordProperty(key, value.toString());
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
				
				RDFNode feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject();
				
				Property key = null;
				String uri = null;
				if (feature.isURIResource() && (lookup!= null)) {
					uri = ((Resource)feature).getURI();
					key = lookup.get(uri);
				}
				
				if (key == null) {
					key = propertyIterator.parseRecord(feature,key);
					if (uri != null) {
						if (lookup == null) lookup = new Hashtable<String, Property>();
						lookup.put(uri, key);
					}
				}	
				
				if (value.isLiteral()) {
					RDFDatatype datatype = ((Literal)value).getDatatype();
					if (XSDDatatype.XSDdouble.equals(datatype)) 
						record.setRecordProperty(key, ((Literal)value).getDouble());
					else if (XSDDatatype.XSDfloat.equals(datatype)) 
						record.setRecordProperty(key, ((Literal)value).getFloat());
					else if (XSDDatatype.XSDinteger.equals(datatype)) 
						record.setRecordProperty(key, ((Literal)value).getInt());		
					else if (XSDDatatype.XSDstring.equals(datatype)) 
						record.setRecordProperty(key,  ((Literal)value).getString());
				}
	
			}
		}
	}
}
