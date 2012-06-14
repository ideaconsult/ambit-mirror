package ambit2.rest.property;

import java.io.Writer;
import java.util.Hashtable;

import org.opentox.rdf.OT;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class PropertyJSONReporter extends PropertyURIReporter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;

	enum jsonFeature {
		URI,
		title,
		units,
		isNominal,
		isNumeric,
		sameAs,
		isModelPredictionFeature,
		source,
		creator,
		annotation
		;
		
		public String jsonname() {
			return name();
		}
	}
	
	enum jsonAnnotation {
		URI,
		annotation
		;
		
		public String jsonname() {
			return name();
		}
	}
	public PropertyJSONReporter(Request baseRef) {
		super(baseRef,null);
	}
	@Override
	public Object processItem(Property feature) throws AmbitException {
		try {
			String uri = getURI(feature);
			boolean numeric = (feature.getClazz()==Number.class) || 
				(feature.getClazz()==Double.class) ||
				(feature.getClazz()==Float.class) ||
				(feature.getClazz()==Integer.class) ||
				(feature.getClazz()==Long.class);
			//tuple
			//else if (item.getClazz()==Dictionary.class) feature.addOntClass(OTClass.TupleFeature.getOntClass(jenaModel));
			
			//sameas
			String uriSameAs = feature.getLabel();
			if(uriSameAs==null) uriSameAs  = Property.guessLabel(feature.getName());
			if ((uriSameAs!=null) && (uri.indexOf("http://")<0)  && (uri.indexOf("https://")<0)) {
				uriSameAs = String.format("%s%s",OT.NS,Reference.encode(uriSameAs));
			}
			//source, etc
			String uriSource = feature.getTitle();
			String typeSource = "Source";
			boolean isModelPredictionFeature = feature.getTitle().indexOf("/model/")>0;
			if ((uriSource.indexOf("http://")<0) && (uriSource.indexOf("https://")<0)) {
				if (_type.Algorithm.equals(feature.getReference().getType())) {
					uriSource = String.format("%s/algorithm/%s",getBaseReference(),Reference.encode(uriSource));
					typeSource = "Algorithm";
				} else if (_type.Model.equals(feature.getReference().getType())) {
					uriSource = String.format("%s/model/%s",getBaseReference(),Reference.encode(uriSource));
					typeSource = "Model";
					isModelPredictionFeature = true;
				} else if (_type.Feature.equals(feature.getReference().getType())) {
					uriSource = String.format("%s/feature/%s",getBaseReference(),Reference.encode(uriSource));
					typeSource = "Feature";		
				} else if (_type.Dataset.equals(feature.getReference().getType())) {
					uriSource = String.format("%s/dataset/%s",getBaseReference(),Reference.encode(uriSource));
					typeSource = "Dataset";	
				} else {

				}
			//	feature.addProperty(DC.creator, item.getReference().getURL());
			}	
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n\"%s\":{\n" + //uri
					"\n\ttype:\"Feature\"," + //uri
					"\n\t%s:\"%s\"," + //title
					"\n\t%s:\"%s\"," + //units
					"\n\t%s:\"%s\"," + //nominal
					"\n\t%s:\"%s\"," + //numeric
					"\n\t%s:\"%s\"," + //sameAs
					"\n\t%s:\"%s\"," + //isModelPredictionFeature
					"\n\t%s:\"%s\"," + //creator
					"\n\t%s:{\n\t\t\"URI\":\"%s\",\n\t\t\"type\":\"%s\"\n\t}" + 					//source
					"\n}",
					uri,
					jsonFeature.title.jsonname(),feature.getName(),
					jsonFeature.units.jsonname(),feature.getUnits()==null?"":feature.getUnits(),
					jsonFeature.isNominal.jsonname(),feature.isNominal(),
					jsonFeature.isNumeric.jsonname(),numeric,
					jsonFeature.sameAs.jsonname(),uriSameAs==null?null:uriSameAs.replace("\"","'").replace("\n"," "),
					jsonFeature.isModelPredictionFeature.jsonname(),isModelPredictionFeature,
					jsonFeature.creator.jsonname(),feature.getReference().getURL(),
					jsonFeature.source.jsonname(),
						uriSource==null?null:uriSource.replace("\"","'"),
						typeSource==null?null:typeSource.replace("\"","'").replace("\n"," ")
					
					));
			comma = ",";

		} catch (Exception x) {
			
		}
		return feature;
	}
	/*
	protected String annotation2json(PropertyAnnotation annotation) {
		if (annotation!=null)
			
				PropertyAnnotationRDFReporter.annotation2RDF(a, jenaModel, feature,uriReporter.getBaseReference().toString());
			else return null;
	}
	*/
	public void header(java.io.Writer output, IQueryRetrieval<Property> query) {
		try {
			output.write("{");
			output.write("\nfeature:{\n");
		} catch (Exception x) {}
	};
	@Override
	public void footer(Writer output, IQueryRetrieval<Property> query) {
		try {
			output.write("\t\n}");
			output.write("\n}");
		} catch (Exception x) {}
	};
	

}
