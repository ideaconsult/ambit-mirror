package ambit2.rest.similarity.space;

import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.pairwise.ChemSpaceCell;
import ambit2.db.search.structure.pairwise.ChemicalSpaceQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.structure.ConformerURIReporter;

/**
 * JSON
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class ChemicalSpaceJSONReporter<Q extends IQueryRetrieval<ChemSpaceCell>> extends
	QueryReporter<ChemSpaceCell, Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 410930501401847402L;
    protected Property property;
    protected long maxPairSerialized = 0;
    protected Map<Integer, Double> ranges = new TreeMap<Integer, Double>();

    public long getMaxPairSerialized() {
	return maxPairSerialized;
    }

    public void setMaxPairSerialized(long maxPairSerialized) {
	this.maxPairSerialized = maxPairSerialized;
    }

    protected String comma = "";
    protected final Reference baseReference;
    protected final Request request;
    protected final ConformerURIReporter<IQueryRetrieval<IStructureRecord>> cmpReporter;

    public Reference getBaseReference() {
	return baseReference;
    }

    enum jsonFeature {
	datasetURI, featureURI, source, target, value, bin1, property1, bin2, property2;

	public String jsonname() {
	    return name();
	}
    }

    public ChemicalSpaceJSONReporter(Request request, Property property) {
	super();
	this.request = request;
	this.baseReference = request.getRootRef();
	cmpReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(request);
	this.property = property;
    }

    public ConformerURIReporter<IQueryRetrieval<IStructureRecord>> getCmpReporter() {
	return cmpReporter;
    }

    @Override
    public Object processItem(ChemSpaceCell item) throws AmbitException {
	try {
	    if (item == null)
		return null;
	    try {
		if (ranges.get(item.getIndex()[0]) == null)
		    ranges.put(item.getIndex()[0], item.getLeft()[0]); // to put
								       // ranges
								       // here
		if (ranges.get(item.getIndex()[1]) == null)
		    ranges.put(item.getIndex()[1], item.getRight()[0]); // to
									// put
									// ranges
									// here
		if (ranges.get(item.getIndex()[1] + 1) == null)
		    ranges.put(item.getIndex()[1] + 1, item.getRight()[1]); // to
									    // put
									    // ranges
									    // here
		output.write(String.format("%s\n[%d,%d,%6.4E]", comma, item.getIndex()[0], item.getIndex()[1],
			item.getDistance()));

	    } catch (Exception x) {
		x.printStackTrace();
	    } finally {
		comma = ",";
	    }
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
    public void footer(Writer output, Q query) {
	try {
	    output.write("\n],");// matrix
	    output.write(String.format("\"%s\":\"%s\",\n", "elapsed2", System.currentTimeMillis() - getStartTime()));
	    output.write("\n\"range\":[");
	    comma = "";
	    for (Double range : ranges.values()) {
		output.write(String.format("%s%6.4e", comma, range.doubleValue()));
		comma = ",";
	    }
	    ;
	    output.write("]");

	    output.write("\n}");
	} catch (Exception x) {
	    x.printStackTrace();
	}
    };

    @Override
    public void header(Writer output, Q query) {
	try {
	    output.write("{\n");
	    if (query instanceof ChemicalSpaceQuery) {
		ChemicalSpaceQuery csq = (ChemicalSpaceQuery) query;
		Reference datasetURI = baseReference.clone();
		datasetURI.addSegment(OpenTox.URI.dataset.name());
		if (csq.getFieldname() instanceof IStoredQuery)
		    datasetURI.addSegment(String.format("R%d", csq.getFieldname().getID()));
		else
		    datasetURI.addSegment(Integer.toString(csq.getFieldname().getID()));
		output.write(String.format("\"%s\":\"%s\",\n", jsonFeature.datasetURI.jsonname(), datasetURI));

		Reference featureURI = baseReference.clone();
		featureURI.addSegment(OpenTox.URI.feature.name());
		featureURI.addSegment(Integer.toString(csq.getValue().getId()));
		output.write(String.format("\"%s\":\"%s\",\n", jsonFeature.featureURI.jsonname(), featureURI));

		output.write(String.format("\"%s\":\"%s\",\n", "elapsed1", System.currentTimeMillis() - getStartTime()));

		output.write(String.format("\"%s\":\"%s\",\n", "method", csq.getMethod()));
		output.write(String.format("\"%s\":\"%s\",\n", "threshold_similiarity", csq.getThreshold_similarity()));
		output.write(String.format("\"%s\":\"%s\",\n", "threshold_activity", csq.getThreshold_dactivity()));
		output.write(String.format("\"%s\":\"%s\",\n", "resolution", csq.getMaxPoints()));

	    }
	    output.write("\"matrix\":[");
	} catch (Exception x) {
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
