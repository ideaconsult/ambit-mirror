package ambit2.rest.dataset;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.json.JSONUtils;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class MetadatasetJSONReporter<Q extends IQueryRetrieval<M>,M extends ISourceDataset> extends DatasetURIReporter<Q,M> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	
	enum jsonFeature {
		URI,
		title,
		stars,
		source,
		rights,
		rightsHolder,
		maintainer,
		seeAlso
		;
		
		public String jsonname() {
			return name();
		}
	}
	
	public MetadatasetJSONReporter(Request baseRef) {
		super(baseRef);
	}
	@Override
	public Object processItem(M item) throws AmbitException {
		try {
			String uri = getURI(item);
			String rights = "rights";

			if (item.getLicenseURI()!=null) {
				for (ISourceDataset.license l : ISourceDataset.license.values())
					if (l.getURI().equals(item.getLicenseURI())) {
						rights = "license";
						break;
					}
				if (!item.getLicenseURI().startsWith("http")) {
					item.setLicenseURI(String.format("http://ambit.sf.net/resolver/rights/%s",Reference.encode(item.getLicenseURI())));
				}
			}

			String type = "Dataset";
			String url = null;
			if (item instanceof SourceDataset) url = ((SourceDataset)item).getURL();
			else if (item instanceof SubstanceEndpointsBundle) 	{
				url = ((SubstanceEndpointsBundle)item).getURL();
				type = "Bundle";
			}
			
			
			if (comma!=null) getOutput().write(comma);
			getOutput().write("\n{");
			getOutput().write(String.format(
					"\n\t\"%s\":\"%s\"," + //uri
					"\n\t\"type\":\"%s\"," + //uri
					"\n\t\"%s\":%s," + //title
					"\n\t\"%s\":%d," + //stars
					"\n\t\"%s\":%s," + //source
					"\n\t\"%s\":%s," + //rightsHolder
					"\n\t\"%s\":%s," + //maintainer
					"\n\t\"%s\":%s," + //seeAlso
					"\n\t\"%s\":{\n\t\t\"URI\":%s,\n\t\t\"type\":%s\n\t}", 					//source
					jsonFeature.URI.jsonname(),uri,type,
					jsonFeature.title.jsonname(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getName())),
					jsonFeature.stars.jsonname(),item.getStars(),
					jsonFeature.source.jsonname(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getSource())),
					jsonFeature.rightsHolder.jsonname(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getrightsHolder())),
					jsonFeature.maintainer.jsonname(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getMaintainer())),
					jsonFeature.seeAlso.jsonname(),
						(url==null)?"null":JSONUtils.jsonQuote(JSONUtils.jsonEscape(url)),
					jsonFeature.rights.jsonname(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getLicenseURI())),
							JSONUtils.jsonQuote(JSONUtils.jsonEscape(rights))

					));
			if (item instanceof SubstanceEndpointsBundle) {
			    	SubstanceEndpointsBundle bundle = (SubstanceEndpointsBundle) item;
			    	getOutput().write(String.format(",\n\t\"id\":%s",bundle.getID()));
			    	getOutput().write(String.format(",\n\t\"description\":%s",JSONUtils.jsonQuote(JSONUtils.jsonEscape(bundle.getDescription()))));
			    	getOutput().write(String.format(",\n\t\"created\":%s",bundle.getCreated()));
				getOutput().write(String.format(",\n\t\"summary\":\"%s/summary\"",uri));
				getOutput().write(String.format(",\n\t\"compound\":\"%s/compound\"",uri));
				getOutput().write(String.format(",\n\t\"substance\":\"%s/substance\"",uri));
				getOutput().write(String.format(",\n\t\"property\":\"%s/property\"",uri));
				getOutput().write(String.format(",\n\t\"dataset\":\"%s/dataset\"",uri));
				getOutput().write(String.format(",\n\t\"matrix\":\"%s/matrix\"",uri));
				
			}
			getOutput().write("\n}");			
			comma = ",";
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}
	/*
	protected String annotation2json(PropertyAnnotation annotation) {
		if (annotation!=null)
			
				PropertyAnnotationRDFReporter.annotation2RDF(a, jenaModel, feature,uriReporter.getBaseReference().toString());
			else return null;
	}
	*/
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	};
	
	
	@Override
	public void header(Writer output, Q query) {
		try {
			output.write("{\"dataset\": [");
		} catch (Exception x) {}
	};

	@Override
	public String getFileExtension() {
		return null;
	}

}
