package ambit2.rest.dataset;

import java.util.Hashtable;
import java.util.Iterator;

import org.restlet.data.Reference;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import ambit2.base.exceptions.AmbitException;
import ambit2.rest.OT;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


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
	
	protected FastVector parseFeatures() {
		FastVector attributes = new FastVector();
		Resource s = OT.OTClass.Feature.getOntClass(jenaModel);
		if (s==null) return null;
		Attribute feature = new Attribute("ID",(FastVector)null);
		attributes.addElement(feature);
		urilookup.put("ID", feature);
		StmtIterator features =  jenaModel.listStatements(new SimpleSelector(null,null,s));
		while (features.hasNext()) {
			feature = createFeature(features.next().getSubject());
			attributes.addElement(feature);
		}
		return attributes;
	}

	
	@Override
	public void beforeProcessing(Reference target) throws AmbitException {
		super.beforeProcessing(target);
		attributes = parseFeatures();
		instances = new Instances("Dataset URI TODO", attributes, 0); 				
	}
	@Override
	public void afterProcessing(Reference target, Iterator<Instance> iterator)
			throws AmbitException {
		// TODO Auto-generated method stub
		super.afterProcessing(target, iterator);
	}
	@Override
	protected void parseRecord(Resource newEntry, Instance record) {
		
		//get the compound
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(newEntry,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			record.setValue(urilookup.get("ID"), st.getObject().toString());
			break;
		}	
		//get feature values
		parseFeatureValues( newEntry,record);
	}

	protected void parseFeatureValues(Resource dataEntry,Instance record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.value).getObject();
				Attribute key = urilookup.get( fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject().toString());
				if (key != null) 
					setFeatureValue(record, key,  value);
	
			}
		}
	}		
	@Override
	protected Attribute createFeature(RDFNode feature) {
		Attribute a= new Attribute(feature.toString(),(FastVector) null);
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
	//TODO value type
	@Override
	protected void setFeatureValue(Instance record, Attribute key, RDFNode value) {
		if (value.isLiteral())  {
	 		//key.addStringValue(((Literal)value).getString());
			record.setValue(key, ((Literal)value).getString() );
		}
	}

	@Override
	protected Instance createRecord() {
		if (instances != null) {
			Instance instance = new Instance(attributes.size());
			instance.setDataset(instances);
			instances.add(instance);
			return instance;
		} else return null;
	}


}
