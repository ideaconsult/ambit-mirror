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

import ambit2.db.simiparity.space.QMapSpace;
import ambit2.rest.structure.CompoundURIReporter;

public class QMapSpaceJSONReporter<Q extends IQueryRetrieval<QMapSpace>> extends QueryReporter<QMapSpace, Q, Writer> {
    /**
     * 
     */
    private static final long serialVersionUID = 1215876192032192561L;
    protected String comma = null;
    protected QMapJSONReporter qmapReporter;
    protected CompoundURIReporter cmpReporter;
    protected StringWriter qmapWriter;
    protected List<Integer> cacheQMap = new ArrayList<Integer>();

    public QMapSpaceJSONReporter(Request request) {
	super();
	cmpReporter = new CompoundURIReporter(request, null);
	qmapReporter = new QMapJSONReporter(request);
	qmapWriter = new StringWriter();
	try {
	    qmapReporter.setOutput(qmapWriter);
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }

    @Override
    public Object processItem(QMapSpace item) throws AmbitException {
	try {
	    if (cacheQMap.indexOf(item.getQmap().getId()) < 0) {
		cacheQMap.add(item.getQmap().getId());
		qmapReporter.processItem(item.getQmap());
	    }
	    String uri = qmapReporter.getURI(item.getQmap());
	    String cmpuri = cmpReporter.getURI(item.getRecord());
	    if (comma != null)
		getOutput().write(comma);
	    getOutput().write(
		    String.format("\n{" + "\n\t\"%s\":\"%s\","
			    + // compound uri
			    "\n\t\"%s\":\"%s\","
			    + // qmap uri
			    "\n\t\"%s\":%f," + "\n\t\"%s\":%f," + "\n\t\"%s\":%f," + "\n\t\"%s\":%f,"
			    + "\n\t\"%s\":%f," + "\n\t\"%s\":%f," + "\n\t\"%s\":%f" + "\n}",
			    QMapJSONReporter.qmapJSON.URI.jsonname(), cmpuri,
			    QMapJSONReporter.qmapJSON.qmap.jsonname(), uri,
			    QMapJSONReporter.qmapJSON.activity.jsonname(),
			    item.getRecord().getProperty(item.getQmap().getProperty()), QMapJSONReporter.qmapJSON.g2,
			    item.getG2(), QMapJSONReporter.qmapJSON.fisher, item.getFisher(),
			    QMapJSONReporter.qmapJSON.a, item.getA(), QMapJSONReporter.qmapJSON.b, item.getB(),
			    QMapJSONReporter.qmapJSON.c, item.getC(), QMapJSONReporter.qmapJSON.d, item.getD()));

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

    @Override
    public void header(Writer output, Q query) {
	try {
	    output.write("{\n");
	    output.write("\"nodes\": [\n");
	    qmapReporter.qmapheader(qmapWriter, null);
	} catch (Exception x) {
	}
    };

    @Override
    public void footer(Writer output, Q query) {
	qmapReporter.qmapfooter(qmapWriter, null);
	try {
	    output.write("\t\n],");
	    output.write(qmapWriter.toString());
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
}