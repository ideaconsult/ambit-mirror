package ambit2.rest.dataset;

import org.restlet.util.Template;

import ambit2.rest.OT;
import ambit2.rest.RDFBatchParser;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public abstract class RDFDatasetParser<Item, Feature> extends RDFBatchParser<Item> {

	protected Template compoundTemplate;
	protected Template conformerTemplate;
	protected Template featureTemplate;

	public RDFDatasetParser(String baseReference) {
		super(baseReference,OT.OTClass.DataEntry);
		compoundTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,CompoundResource.compoundID));
		conformerTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,ConformerResource.conformerID));
		featureTemplate = new Template(String.format("%s%s",baseReference==null?"":baseReference,PropertyResource.featuredefID));
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3706916949251807233L;

	@Override
	protected void parseRecord(Resource newEntry, Item record) {
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
	}
	protected void parseFeatureValues(Resource dataEntry,Item record)  {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.value).getObject();
				
				String feature = fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject().toString();
				Feature key = createFeature(fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject());
				parseFeatureURI(feature, key);
				setFeatureValue(record, key,  value);
	
			}
		}
	}	
	protected abstract void parseCompoundURI(String uri,Item record) ;
	public abstract  boolean readStructure(RDFNode target,Item record) ;
	

	protected abstract Feature createFeature(RDFNode feature);
	protected abstract void setFeatureValue(Item record, Feature key, RDFNode value) ;
	protected abstract void parseFeatureURI(String uri,Feature property) ;
}
