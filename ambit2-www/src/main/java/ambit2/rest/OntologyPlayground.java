package ambit2.rest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.OTClass;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Same as parent, but loads the ontology on every request. For testing purposes.
 * @author nina
 *
 * @param <T>
 */
public class OntologyPlayground<T extends Serializable> extends RDFGraphResource<T> {
	public OntologyPlayground() {
		super(false);
	}

	protected void parseDataset(OntModel jenaModel, OutputStreamWriter out, OntResource dataset) throws IOException {
		out.write(String.format("<h3>%s&nbsp;%s</h3>","Dataset", dataset==null?"":dataset));
		StmtIterator iter =  jenaModel.listStatements(new SimpleSelector(dataset,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (iter.hasNext()) {
			Statement st = iter.next();
			if (!st.getObject().isResource()) continue;
			Resource dataEntry = (Resource) st.getObject();
			out.write(String.format("<h4>DataEntry&nbsp;%s</h4>",dataEntry));
			//get the compound
			parseDataEntry(jenaModel, out, dataEntry);
		}		
	}
	protected void parseDataEntry(OntModel jenaModel, OutputStreamWriter out, Resource dataEntry) throws IOException {
		StmtIterator compound =  jenaModel.listStatements(new SimpleSelector(dataEntry,OT.OTProperty.compound.createProperty(jenaModel),(RDFNode)null));
		while (compound.hasNext()) {
			Statement st = compound.next();
			out.write(String.format("<h5>%s&nbsp;%s</h5>","Compound", st.getObject()));
		}	
		//get feature values
		parseFeatureValues(jenaModel, out, dataEntry);
	}		
	protected void parseFeatureValues(OntModel jenaModel, OutputStreamWriter out, Resource dataEntry) throws IOException {
		StmtIterator values =  jenaModel.listStatements(new SimpleSelector(dataEntry,
				OT.OTProperty.values.createProperty(jenaModel),(RDFNode)null));
		out.write(String.format("<h5>%s</h5>","Values"));
		while (values.hasNext()) {
			Statement st = values.next();
			if (st.getObject().isResource()) {
				Resource fv = (Resource)st.getObject();
				RDFNode value = fv.getProperty(OT.DataProperty.value.createProperty(jenaModel)).getObject();
				out.write(String.format("%s&nbsp;=&nbsp;%s<br>",
						//Feature
						fv.getProperty(OT.OTProperty.feature.createProperty(jenaModel)).getObject().toString(),
						//Value
						value.isLiteral()?((Literal)value).getString():value
						));
			}
		}
	}
	
	protected void more(OutputStreamWriter out) throws IOException {
		
		print(OT.OTClass.Algorithm,out);
		print(OT.OTClass.Model,out);
		print(OT.OTClass.Parameter,out);
		print(OT.OTClass.Dataset,out);
		print(OT.OTClass.Compound,out);
		print(OT.OTClass.Feature,out);
		print(OT.OTClass.NominalFeature,out);
		print(OT.OTClass.NumericFeature,out);
		print(OT.OTClass.StringFeature,out);
		print(OT.OTClass.TupleFeature,out);
		print(OT.OTClass.FeatureValue,out);
		print(OT.OTClass.DataEntry,out);
		print(OT.OTClass.Validation,out);
		print(OT.OTClass.ValidationInfo,out);
		print(OT.OTClass.Task,out);
		
		
		OntClass aClass = OT.OTClass.Dataset.getOntClass(queryObject);
		if (aClass != null) {
			ExtendedIterator<? extends OntResource>  a = aClass.listInstances();	
			if (a!=null)
				while (a.hasNext()) {
					parseDataset(queryObject, out,a.next());
				}
			
		}

	}	
	
	protected void print(OTClass otobject, OutputStreamWriter out) throws IOException {
		OntClass aClass = otobject.getOntClass(queryObject);
		out.write(String.format("<h4>%s [%s]</h4>",otobject.toString(),otobject.getNS()));
		
		if (aClass != null) {
			ExtendedIterator<? extends OntResource>  a = aClass.listInstances();	
			
			if (a!=null)
			while (a.hasNext()) {
					OntResource model = a.next();
					out.write(model.toString());
					out.write("<br>");
			}	
			else 	out.write("N/A");		
		}		
	}
}
