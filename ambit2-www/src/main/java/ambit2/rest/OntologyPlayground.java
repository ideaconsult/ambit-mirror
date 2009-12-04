package ambit2.rest;

import java.io.IOException;
import java.io.OutputStreamWriter;

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
		out.write("<h3>OpenTox objects found:</h3>");
		OntClass modelClass = OT.OTClass.Model.getOntClass(queryObject);
		out.write("<h4>Models [http://www.opentox.org/api/1.1#Model]</h4>");
		if (modelClass!=null) {
			ExtendedIterator<? extends OntResource> models = modelClass.listInstances();
			if (models!=null)
			while (models.hasNext()) {
					OntResource model = models.next();
					out.write(model.toString());
					out.write("<br>");
			}	
			else 	out.write("N/A");
		}
		
		OntClass aClass = OT.OTClass.Algorithm.getOntClass(queryObject);
		out.write("<h4>Algorithms [http://www.opentox.org/api/1.1#Algorithm]</h4>");
		if (aClass != null) {
		ExtendedIterator<? extends OntResource> a = aClass.listInstances();		
	
		if (a!=null)
		while (a.hasNext()) {
				OntResource model = a.next();
				out.write(model.toString());
				out.write("<br>");
		}	
		else 	out.write("N/A");	
		}
		
		aClass = OT.OTClass.Dataset.getOntClass(queryObject);
		out.write("<h4>Datasets [http://www.opentox.org/api/1.1#Dataset]</h4>");
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
		
		aClass = OT.OTClass.Feature.getOntClass(queryObject);
		out.write("<h4>Feature [http://www.opentox.org/api/1.1#Feature]</h4>");
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
		
		aClass = OT.OTClass.Compound.getOntClass(queryObject);
		out.write("<h4>Compound [http://www.opentox.org/api/1.1#Compound]</h4>");
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
		
		aClass = OT.OTClass.FeatureValue.getOntClass(queryObject);
		out.write("<h4>Feature values http://www.opentox.org/api/1.1#Featurevalue</h4>");
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
		
		aClass = OT.OTClass.DataEntry.getOntClass(queryObject);
		out.write("<h4>Data entries [http://www.opentox.org/api/1.1#DataEntry]</h4>");
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
		
		aClass = OT.OTClass.Validation.getOntClass(queryObject);
		out.write("<h4>Validation [http://www.opentox.org/api/1.1#Validation]</h4>");
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
