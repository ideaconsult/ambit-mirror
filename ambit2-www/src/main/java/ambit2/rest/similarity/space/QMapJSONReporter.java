package ambit2.rest.similarity.space;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.db.simiparity.space.QMap;
import ambit2.rest.dataset.MetadatasetJSONReporter;
import ambit2.rest.property.PropertyJSONReporter;

public class QMapJSONReporter<Q extends IQueryRetrieval<QMap>> extends QueryReporter<QMap,Q,Writer> {
	protected String comma = null;
	protected QMapURIReporter qmapReporter;
	protected PropertyJSONReporter propertyReporter;
	protected MetadatasetJSONReporter datasetReporter;
	protected StringWriter propertyWriter;
	protected StringWriter datasetWriter;
	protected List<Integer> cacheProperty = new ArrayList<Integer>();
	protected List<Integer> cacheDataset = new ArrayList<Integer>();
	
	enum jsonFeature {
		URI,
		metadata,
		dataset,
		featureURI,
		activity,
		similarity,
		threshold
		;
		
		public String jsonname() {
			return name();
		}
	}

	public QMapJSONReporter(Request request) {
		super();
		qmapReporter = new QMapURIReporter(request,null);
		propertyWriter = new StringWriter();
		datasetWriter = new StringWriter();
		datasetReporter = new MetadatasetJSONReporter(request);
		propertyReporter = new PropertyJSONReporter(request);
		try {
			datasetReporter.setOutput(datasetWriter);
			propertyReporter.setOutput(propertyWriter);
		} catch (Exception x) {}
		
	}

	@Override
	public Object processItem(QMap item) throws AmbitException {
		try {
			if (cacheProperty.indexOf(item.getProperty().getId())<0) {
				cacheProperty.add(item.getProperty().getId());
				propertyReporter.processItem(item.getProperty());	
			}
			if (cacheDataset.indexOf(item.getDataset().getID())<0) {
				cacheDataset.add(item.getDataset().getID());
				datasetReporter.processItem(item.getDataset());	
			}			
			String uri = qmapReporter.getURI(item);
			String dataseturi = datasetReporter.getURI(item.getDataset());
			String propertyuri = propertyReporter.getURI(item.getProperty());
			if (comma!=null) getOutput().write(comma);
			getOutput().write(String.format(
					"\n{"+
					"\n\t\"%s\":\"%s\"," + //uri
					"\n\t\"%s\":\"%s/metadata\"," + //dataseturi
					"\n\t\"%s\":{\n\t\t\"%s\":\"%s\"\n\t}," +
					"\n\t\"activity\":{\n\t\t\"%s\":\"%s\",\n\t\t\"%s\":%f\n\t}," + 					
					"\n\t\"similarity\":{\n\t\t\"%s\":%f\n\t}" + 					
					"\n}",
					jsonFeature.URI.jsonname(),uri,
					jsonFeature.metadata.jsonname(),uri,
					jsonFeature.dataset.jsonname(),jsonFeature.URI.jsonname(),dataseturi,
					jsonFeature.featureURI.jsonname(),propertyuri,
					jsonFeature.threshold,
					item.getActivityThreshold(),
					jsonFeature.threshold,
					item.getSimilarityThreshold()
					));
			comma = ",";
		} catch (Exception x) {
			x.printStackTrace();
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
	public void header(Writer output, Q query) {
		try {
			output.write("{\n");
			output.write("\"qmap\": [\n");
		} catch (Exception x) {}
	};
	
	
	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\t\n],");
			output.write("\"dataset\": [\n");
			output.write(datasetWriter.toString());
			output.write("\t\n],\n");
			output.write("\n\"feature\":{\n");
			output.write(propertyWriter.toString());
			output.write("\t\n}");
			output.write("\n}");
		} catch (Exception x) {
			x.printStackTrace();
		}
	};

	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	@Override
	public void open() throws DbAmbitException {
		
	}
}
