package ambit2.rest.similarity.space;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;

import ambit2.db.simiparity.space.QMap;
import ambit2.rest.dataset.MetadatasetJSONReporter;
import ambit2.rest.property.PropertyJSONReporter;

public class QMapJSONReporter<Q extends IQueryRetrieval<QMap>> extends QueryReporter<QMap, Q, Writer> {
    /**
     * 
     */
    private static final long serialVersionUID = -4678964261324837983L;
    protected String comma = null;
    protected QMapURIReporter qmapReporter;
    protected PropertyJSONReporter propertyReporter;
    protected MetadatasetJSONReporter datasetReporter;
    protected StringWriter propertyWriter;
    protected StringWriter datasetWriter;
    protected List<Integer> cacheProperty = new ArrayList<Integer>();
    protected List<Integer> cacheDataset = new ArrayList<Integer>();

    public enum qmapJSON {
	qmap, URI, metadata, dataset, featureURI, activity, similarity, threshold, fisher, g2, a, b, c, d;

	public String jsonname() {
	    return name();
	}
    }

    public QMapJSONReporter(Request request) {
	super();
	qmapReporter = new QMapURIReporter(request);
	propertyWriter = new StringWriter();
	datasetWriter = new StringWriter();
	datasetReporter = new MetadatasetJSONReporter(request);
	propertyReporter = new PropertyJSONReporter(request);
	try {
	    datasetReporter.setOutput(datasetWriter);
	    propertyReporter.setOutput(propertyWriter);
	} catch (Exception x) {
	}

    }

    @Override
    public Object processItem(QMap item) throws AmbitException {
	try {
	    if (cacheProperty.indexOf(item.getProperty().getId()) < 0) {
		cacheProperty.add(item.getProperty().getId());
		propertyReporter.processItem(item.getProperty());
	    }
	    if (cacheDataset.indexOf(item.getDataset().getID()) < 0) {
		cacheDataset.add(item.getDataset().getID());
		datasetReporter.processItem(item.getDataset());
	    }
	    String uri = qmapReporter.getURI(item);
	    String dataseturi = datasetReporter.getURI(item.getDataset());
	    String propertyuri = propertyReporter.getURI(item.getProperty());
	    if (comma != null)
		getOutput().write(comma);
	    getOutput().write(
		    String.format("\n{"
			    + "\n\t\"%s\":\"%s\","
			    + // uri
			    "\n\t\"%s\":\"%s/metadata\","
			    + // dataseturi
			    "\n\t\"%s\":{\n\t\t\"%s\":\"%s\"\n\t},"
			    + "\n\t\"activity\":{\n\t\t\"%s\":\"%s\",\n\t\t\"%s\":%f\n\t},"
			    + "\n\t\"similarity\":{\n\t\t\"%s\":%f\n\t}" + "\n}", qmapJSON.URI.jsonname(), uri,
			    qmapJSON.metadata.jsonname(), uri, qmapJSON.dataset.jsonname(), qmapJSON.URI.jsonname(),
			    dataseturi, qmapJSON.featureURI.jsonname(), propertyuri, qmapJSON.threshold,
			    item.getActivityThreshold(), qmapJSON.threshold, item.getSimilarityThreshold()));
	    comma = ",";
	} catch (Exception x) {
	    x.printStackTrace();
	}
	return item;
    }

    /*
     * protected String annotation2json(PropertyAnnotation annotation) { if
     * (annotation!=null)
     * 
     * PropertyAnnotationRDFReporter.annotation2RDF(a, jenaModel,
     * feature,uriReporter.getBaseReference().toString()); else return null; }
     */

    public void qmapheader(Writer output, Q query) {
	try {
	    output.write("\"qmap\": [\n");
	} catch (Exception x) {
	}
    };

    public void qmapfooter(Writer output, Q query) {
	try {
	    output.write("\t\n],");
	    output.write("\"dataset\": [\n");
	    output.write(datasetWriter.toString());
	    output.write("\t\n],\n");
	    output.write("\n\"feature\":{\n");
	    output.write(propertyWriter.toString());
	    output.write("\t\n}");
	} catch (Exception x) {
	    x.printStackTrace();
	}
    };

    @Override
    public void header(Writer output, Q query) {
	try {
	    output.write("{\n");
	    qmapheader(output, query);
	} catch (Exception x) {
	}
    };

    @Override
    public void footer(Writer output, Q query) {
	try {
	    qmapfooter(output, query);
	    output.write("\n}");
	} catch (Exception x) {
	    x.printStackTrace();
	}
    };

    @Override
    public String getFileExtension() {
	return null;// "json";
    }

    @Override
    public void open() throws DbAmbitException {

    }

    public String getURI(QMap item) {
	return qmapReporter.getURI(item);
    }
}
