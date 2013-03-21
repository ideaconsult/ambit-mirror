package ambit2.rest.dataset;

import java.io.Writer;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.json.JSONUtils;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class MetadatasetJSONReporter<Q extends IQueryRetrieval<ISourceDataset>> extends DatasetURIReporter<Q> {
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
		seeAlso,
		;
		
		public String jsonname() {
			return name();
		}
	}
	
	public MetadatasetJSONReporter(Request baseRef) {
		super(baseRef,null);
	}
	@Override
	public Object processItem(ISourceDataset item) throws AmbitException {
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

			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\t\"%s\":\"%s\"," + //uri
					"\n\t\"type\":\"Dataset\"," + //uri
					"\n\t\"%s\":\"%s\"," + //title
					"\n\t\"%s\":%d," + //stars
					"\n\t\"%s\":\"%s\"," + //rightsHolder
					"\n\t\"%s\":\"%s\"," + //seeAlso
					"\n\t\"%s\":{\n\t\t\"URI\":\"%s\",\n\t\t\"type\":\"%s\"\n\t}" + 					//source
					"\n}",
					jsonFeature.URI.jsonname(),uri,
					jsonFeature.title.jsonname(),JSONUtils.jsonEscape(item.getName()),
					jsonFeature.stars.jsonname(),item.getStars(),
					jsonFeature.rightsHolder.jsonname(),item.getrightsHolder()==null?"":JSONUtils.jsonEscape(item.getrightsHolder()),
					jsonFeature.seeAlso.jsonname(),item instanceof SourceDataset?JSONUtils.jsonEscape(((SourceDataset) item).getURL()):"",
					jsonFeature.rights.jsonname(),item.getLicenseURI()==null?"":JSONUtils.jsonEscape(item.getLicenseURI()),
							JSONUtils.jsonEscape(rights)
					));
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
