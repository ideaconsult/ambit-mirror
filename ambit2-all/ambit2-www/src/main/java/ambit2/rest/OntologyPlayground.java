package ambit2.rest;

import java.io.IOException;
import java.io.OutputStreamWriter;

import ambit2.rest.OT.OTClass;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Same as parent, but loads the ontology on every request. For testing purposes.
 * @author nina
 *
 * @param <T>
 */
public class OntologyPlayground<T> extends RDFGraphResource<T> {
	public OntologyPlayground() {
		super(false);
	}
	protected void more(OutputStreamWriter out) throws IOException {
		
		print(OT.OTClass.Algorithm,out);
		print(OT.OTClass.Model,out);
		print(OT.OTClass.Parameter,out);
		print(OT.OTClass.Dataset,out);
		print(OT.OTClass.Compound,out);
		print(OT.OTClass.Feature,out);
		print(OT.OTClass.FeatureValue,out);
		print(OT.OTClass.DataEntry,out);
		print(OT.OTClass.Validation,out);
		print(OT.OTClass.ValidationInfo,out);

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
