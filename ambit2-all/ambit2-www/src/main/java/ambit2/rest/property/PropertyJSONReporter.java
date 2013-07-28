package ambit2.rest.property;

import java.io.Writer;

import org.opentox.rdf.OT;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.ICategory;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.json.JSONUtils;

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
		order,
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
					uriSource = String.format("%s/dataset/%s",getBaseReference(),Reference.encode(uriSource));
					typeSource = "Dataset";	
				}
			//	feature.addProperty(DC.creator, item.getReference().getURL());
			}	
			
			String annotation = null;
			if (feature.getAnnotations()!=null) annotation = annotation2JSON(feature);


			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n\"%s\":{\n" + //uri
					"\n\t\"type\":\"Feature\"," + //uri
					"\n\t\"%s\":\"%s\"," + //title
					"\n\t\"%s\":\"%s\"," + //units
					"\n\t\"%s\":\"%s\"," + //nominal
					"\n\t\"%s\":\"%s\"," + //numeric
					"\n\t\"%s\":\"%s\"," + //sameAs
					"\n\t\"%s\":\"%s\"," + //isModelPredictionFeature
					"\n\t\"%s\":\"%s\"," + //creator
					"\n\t\"%s\":%d," + //order
					"\n\t\"%s\":{\n\t\t\"URI\":\"%s\",\n\t\t\"type\":\"%s\"\n\t}," + 					//source
					"\n\t\"%s\":[%s]\n\n}",
					uri,
					jsonFeature.title.jsonname(),JSONUtils.jsonEscape(feature.getName()),
					jsonFeature.units.jsonname(),feature.getUnits()==null?"":feature.getUnits(),
					jsonFeature.isNominal.jsonname(),feature.isNominal(),
					jsonFeature.isNumeric.jsonname(),numeric,
					jsonFeature.sameAs.jsonname(),uriSameAs==null?null:JSONUtils.jsonEscape(uriSameAs),
					jsonFeature.isModelPredictionFeature.jsonname(),isModelPredictionFeature,
					jsonFeature.creator.jsonname(),JSONUtils.jsonEscape(feature.getReference().getURL()),
					jsonFeature.order.jsonname(),feature.getOrder(),
					jsonFeature.source.jsonname(),
						uriSource==null?null:JSONUtils.jsonEscape(uriSource),
						typeSource==null?null:JSONUtils.jsonEscape(typeSource),
					jsonFeature.annotation.jsonname(),
					annotation==null?"":annotation
					
					));

			comma = ",";

		} catch (Exception x) {
			x.printStackTrace();
		}
		return feature;
	}

	public static String annotation2JSON(Property feature) {
		StringBuilder b = new StringBuilder();
		String acomma = "";
		for (PropertyAnnotation annotation : feature.getAnnotations()) try {
			b.append(acomma);
			
			b.append("\n\t{");
			
			if (annotation.getType()!=null && !"".equals(annotation.getType())) {
				b.append("\t\"type\" : \"");
				b.append(JSONUtils.jsonEscape(annotation.getType()));
				b.append("\",");
			}
			
			b.append("\t\"p\" : \"");
			b.append(JSONUtils.jsonEscape(annotation.getPredicate()));
			b.append("\",");
			
			b.append("\t\"o\" : \"");
			b.append(JSONUtils.jsonEscape(annotation.getObject().toString()));
			b.append("\"");
			
			b.append("}");
			acomma = ",";
		} catch (Exception x) {
			
			return null;
		}
		return b.toString();
	}
	public void header(java.io.Writer output, IQueryRetrieval<Property> query) {
		try {
			output.write("{");
			output.write("\n\"feature\":{\n");
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
