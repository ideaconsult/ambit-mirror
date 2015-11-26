package ambit2.core.io.dx;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.interfaces.IStructureRecord;

public class DXParser extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1912095670898750150L;
	private ObjectMapper dx;
	protected JsonNode dxRoot;
	protected PropertyAnnotations categories = new PropertyAnnotations();
	private enum json_fields {
		likelihood,
		ChemicalPerception,
		StructureProperties,
		Report,
		species,
		endpoints,
		reference,
		referenceFor,
		ot,
		category
	};
	
	private enum importas {
		reference,
		annotation
	};
	
	public final static String[] DXChemicalPerception = {
		"Perceive tautomers",
		"perceive mixtures",
		"match alerts without rules"
	};
	
	public final static String[] DXStructureProperties = {
		"LogP",
		"LogKp",
		"Average Molecular Mass"
	};
	
	public DXParser() {
		super();
		dx = new ObjectMapper();
		InputStream in= null;
		try {
			in = this.getClass().getClassLoader().getResourceAsStream("ambit2/core/io/dx/dx.json");
			dxRoot = dx.readTree(in);
			JsonNode likelihood = dxRoot.get(json_fields.likelihood.name());
			Iterator<Entry<String,JsonNode>> fields = likelihood.getFields();
			while (fields.hasNext()) {
				Entry<String,JsonNode> field = fields.next();
				String category = field.getValue().get(json_fields.category.name()).getTextValue();
				PropertyAnnotation a = new PropertyAnnotation();
				a.setType("^^"+category);
				a.setPredicate("acceptValue");
				a.setObject(field.getKey());
				categories.add(a);
			}
		} catch (Exception x) {
			dxRoot = null;
			logger.log(Level.SEVERE,x.getMessage(),x);
		} finally {
			try { if (in!=null) in.close();} catch (Exception x) {}
		}
	}
	protected void cleanPropertyRefs() {
		JsonNode strucProperty = dxRoot.get(json_fields.StructureProperties.name());
		Iterator<Entry<String,JsonNode>> fields =strucProperty.getFields();
		while (fields.hasNext()) {
			Entry<String,JsonNode> field = fields.next();
			try {((ObjectNode)field.getValue()).remove(json_fields.reference.name());} catch (Exception x) {}
		}
	}
	@Override
	public IStructureRecord process(IStructureRecord record)
			throws AmbitException {
		cleanPropertyRefs();
		ILiteratureEntry reference = null;
		Map<Property,Object> properties = new Hashtable<Property, Object>();
		PropertyAnnotations pa = new PropertyAnnotations();
		for (Property p : record.getRecordProperties()) {
			Object value = record.getRecordProperty(p);
			JsonNode strucProperty = dxRoot.get(json_fields.StructureProperties.name()).get(p.getName());
			if (strucProperty!=null) {
				JsonNode propertyRef = strucProperty.get(json_fields.referenceFor.name());
				if (propertyRef!=null) {
					JsonNode prop = dxRoot.get(json_fields.StructureProperties.name()).get(propertyRef.getTextValue());
					if (prop !=null && (prop instanceof ObjectNode))
						((ObjectNode) prop).put(json_fields.reference.name(),value.toString());
				}
				continue;
			}
			JsonNode reportProperty = dxRoot.get(json_fields.Report.name()).get(p.getName());
			if (reportProperty==null) continue; 	
			if (reportProperty.get("import").getBooleanValue()) 
				try {
					importas ias = importas.valueOf(reportProperty.get("importas").getTextValue());
					switch (ias) {
					case reference: {
						reference = LiteratureEntry.getDXReference(value.toString());
						break;
					}
					case annotation : {
						PropertyAnnotation a = new PropertyAnnotation();
						a.setPredicate(p.getName());	a.setObject(value.toString()); pa.add(a);
						break;
					}
					default : {	}
					}
				} catch (Exception x) {
				}
		}
		if (reference == null)  reference = LiteratureEntry.getDXReference();
		for (Property p : record.getRecordProperties()) {
			Object value = record.getRecordProperty(p);
			JsonNode reportProperty = dxRoot.get(json_fields.Report.name()).get(p.getName());
			JsonNode strucProperty = dxRoot.get(json_fields.StructureProperties.name()).get(p.getName());
			if (reportProperty!=null) continue; 	
			if (strucProperty != null) {
				ILiteratureEntry propReference = reference;
				if (strucProperty.get(json_fields.referenceFor.name())!=null) continue;
				if (strucProperty.get(json_fields.reference.name())!=null) {
					try {
					propReference = LiteratureEntry.getDXReference(strucProperty.get(json_fields.reference.name()).getTextValue());
					} catch (Exception x) {}
				}
				if (strucProperty.get(json_fields.ot.name())!=null) try {
					p.setLabel(strucProperty.get(json_fields.ot.name()).getTextValue());
				} catch (Exception x) {}
				p.setReference(propReference);
				properties.put(p,value);
			} else if ("Superendpoints".equals(p.getName())) { 
				verifySuperEndpoints(record,p);
			} else if (p.getName().startsWith(DXPrefix)) {
				Property dx = verifyDXProperty(p,value,reference);
				if (dx!=null) {
					properties.put(dx,value);
					for (PropertyAnnotation a : pa) {
						dx.getAnnotations().add(a);
					}
				}
			} else properties.put(p,value);
		}
		record.clearProperties();
		record.addRecordProperties(properties);
		return record;
	}
	private final static String DXPrefix = "DX.";
	public final static String DXTAG = "> <DX.";
	
	public static boolean hasDxProperties(IStructureRecord record) {
		return (record !=null) && (record.getContent()!=null) && (record.getContent().indexOf(DXTAG)>0);
	}
	protected Property verifyDXProperty(Property property, Object value, ILiteratureEntry reference) throws AmbitException {
			property.setReference(reference);
			
			String[] split = property.getName().split("\\.");
			JsonNode endpoint = (split.length>1)?dxRoot.get(json_fields.endpoints.name()).get(split[1]):null;
			JsonNode superEndpoint = endpoint==null?null:endpoint.get("super");
			JsonNode species = (split.length>2)?dxRoot.get(json_fields.species.name()).get(split[2]):null;
			
			if (species == null) return null;
			JsonNode ontology = null;
			
			PropertyAnnotations pa = new PropertyAnnotations();
			
			if (species != null) {
				PropertyAnnotation a = new PropertyAnnotation();
				a.setPredicate("species");	a.setObject(split[2]); pa.add(a);
			}
			if (superEndpoint != null) {
				PropertyAnnotation a = new PropertyAnnotation();
				a.setPredicate("DX Superendpoint");	a.setObject(superEndpoint.getTextValue()); pa.add(a);
				try {
					ontology = dxRoot.get(json_fields.endpoints.name()).get(superEndpoint.getTextValue()).get(json_fields.ot.name());
				} catch (Exception x) { logger.log(Level.WARNING,x.getMessage() + " " + superEndpoint.getTextValue());}
			}			
			if (endpoint != null) {
				PropertyAnnotation a = new PropertyAnnotation();
				a.setPredicate("DX endpoint");	a.setObject(split[1]); pa.add(a);
				try {
					ontology = dxRoot.get(json_fields.endpoints.name()).get(endpoint.getTextValue()).get(json_fields.ot.name());
				} catch (Exception x) { logger.log(Level.WARNING,x.getMessage() + " " + endpoint.getTextValue());}
			}
			if (ontology!=null) property.setLabel(ontology.getTextValue());
			for (PropertyAnnotation a : categories) pa.add(a);
			property.setAnnotations(pa);
			
			return property;
	}
	public int verifySuperEndpoints(IStructureRecord record,Property p) throws AmbitException {
		int error = 0;
		if ("Superendpoints".equals(p.getName())) {
			String[] superendpoints = record.getRecordProperty(p).toString().split("\n");
			for (String superendpoint : superendpoints)
				if (dxRoot.get(json_fields.endpoints.name()).get(superendpoint)==null) {
					error ++;
					logger.log(Level.WARNING, superendpoint + " not found in confguration ambit2/core/io/dx/dx.json ");
				}
		}
		return error;
	}

}
