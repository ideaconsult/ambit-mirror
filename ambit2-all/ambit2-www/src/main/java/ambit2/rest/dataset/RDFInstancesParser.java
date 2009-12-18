package ambit2.rest.dataset;

import java.util.Hashtable;

import org.restlet.data.Reference;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.OT;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * Reads dataset into weka instances
 * @author nina
 *
 */
public class RDFInstancesParser extends RDFDatasetParser<Instance, Attribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3428562745619186853L;
	protected FastVector attributes;
	protected Hashtable<String, Attribute> urilookup;
	protected Instances instances;
	protected int maxNominalValues = 20;
	public Instances getInstances() {
		return instances;
	}

	public void setInstances(Instances instances) {
		this.instances = instances;
	}

	public RDFInstancesParser(String baseReference) {
		super(baseReference);
		attributes = new FastVector();
		urilookup = new Hashtable<String, Attribute>();
	}
	/**
	 * Analyzes features and values to create WEKA attributes of numeric, string or nominal type
	 * @return
	 */
	protected FastVector parseFeatures() {
		FastVector attributes = new FastVector();
		Resource s = OT.OTClass.Feature.getOntClass(jenaModel);
		if (s==null) return null;
		
		Property valueProperty = OT.DataProperty.value.createProperty(jenaModel);
		StmtIterator features =  jenaModel.listStatements(new SimpleSelector(null,null,s));
		while (features.hasNext()) {
			
			Statement feature = features.next();
			
			int ndouble=0;
			int nstring = 0;
			
			FastVector nominal = new FastVector();
			StmtIterator entries =  jenaModel.listStatements(
					new SimpleSelector(null,OT.OTProperty.feature.createProperty(jenaModel),feature.getSubject()));
			while (entries.hasNext()) {
				
				Resource entry = entries.next().getSubject();
				try {
					
					Statement values = entry.getProperty(valueProperty);
					if (values.getObject().isLiteral()) {
						Class clazz = ((Literal)values.getObject()).getDatatype().getJavaClass();
						if (clazz == Double.class) ndouble++;
						else if (clazz == Float.class) ndouble++;
						else if (clazz == Integer.class) ndouble++;
						else if (clazz == Long.class) ndouble++;
						else if (clazz == Short.class) ndouble++;
						else {
							String value = ((Literal)values.getObject()).getString();
							if ((nominal.size()<(maxNominalValues+1)) && !nominal.contains(value))
								nominal.addElement(value);
							
							nstring++;
						}
					}

				} catch (Exception x) {
					x.printStackTrace();
				}
				
			}
			
			if ((ndouble+nstring)==0) continue;
			Attribute a = null;
			
			if (ndouble > nstring) //numeric feature
				a = new Attribute(feature.getSubject().toString());
			else if (nominal.size()>maxNominalValues) //string attribute
				a = new Attribute(feature.getSubject().toString(),(FastVector)null);
			else
				a = new Attribute(feature.getSubject().toString(),nominal);
			urilookup.put(feature.getSubject().toString(),a);
			attributes.addElement(a);
		}
		return attributes;
	}

	
	@Override
	public void beforeProcessing(Reference target) throws AmbitException {
		super.beforeProcessing(target);
		attributes = parseFeatures();
		instances = null; 			
		
		StmtIterator dataset =  jenaModel.listStatements(new SimpleSelector(null,
				RDF.type,
				OT.OTClass.Dataset.getOntClass(jenaModel)));
		while (dataset.hasNext()) {
			instances = new Instances(
					dataset.next().getSubject().toString()
					, attributes, 0);
			break;
		}
	}
	
	@Override
	protected Instance parseRecord(Resource newEntry, Instance record) {
		/*
		//get the compound
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			//record.setValue(urilookup.get("ID"), st.getObject().toString());
			break;
		}
		*/	
		//get feature values
		
		parseFeatureValues( newEntry,record);
		record.setDataset(instances);
		instances.add(record);
		return record;
	}

	protected void parseFeatureValues(Resource dataEntry,Instance record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel)).getObject();
				RDFNode feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject();

				Attribute key = urilookup.get( feature.toString());
				if (key != null) 
					setFeatureValue(record, key,  value);
	
			}
		}
	}		
	@Override
	protected Attribute createFeature(RDFNode feature) {
		Attribute a = null;
		try {
			if("http://www.w3.org/2001/XMLSchema#double".equals(
					((Resource)feature).getProperty(DC.type).getObject().toString()))
				a = new Attribute(feature.toString());
		} catch(Exception x) {
			a = null;
		}
		if (a == null)	a = new Attribute(feature.toString(),(FastVector)null);
		urilookup.put(feature.toString(),a);
		return a;
	}


	@Override
	protected void parseCompoundURI(String uri, Instance record) {
	
	}

	@Override
	protected void parseFeatureURI(String uri, Attribute property) {
	}

	@Override
	public boolean readStructure(RDFNode target, Instance record) {
		return true;
	}
	@Override
	protected void setFeatureValue(Instance record, Attribute key, RDFNode value) {
		if (value.isLiteral())  {
	 		//
			if (key.isNumeric()) try {
				record.setValue(key, ((Literal)value).getDouble());
			} catch (Exception x) {  }
			else try {
				//key.addStringValue(((Literal)value).getString());
				record.setValue(key, ((Literal)value).getString() );
			} catch (Exception x) { }
		}
	}

	@Override
	protected Instance createRecord() {
		if (instances != null) {
			return new Instance(attributes.size());
		} else return null;
	}


}
