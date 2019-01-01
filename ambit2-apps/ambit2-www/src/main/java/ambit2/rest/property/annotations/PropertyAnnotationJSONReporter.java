package ambit2.rest.property.annotations;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Request;

import ambit2.base.data.PropertyAnnotation;

public class PropertyAnnotationJSONReporter extends PropertyAnnotationURIReporter {

    /**
     * 
     */
    private static final long serialVersionUID = -1417830647484784888L;
    protected String comma = "";

    public PropertyAnnotationJSONReporter(Request baseRef) {
	super(baseRef);
    }

    public PropertyAnnotationJSONReporter(Request baseRef, String jsoncallback) {
	this(baseRef);
    }

    @Override
    public Object processItem(PropertyAnnotation item) throws Exception {
	getOutput().append(comma);
	getOutput().append(item.toJSON());
	comma = ",";
        return item;
    }

    public void header(java.io.Writer output, IQueryRetrieval<PropertyAnnotation> query) {
	try {
	    output.write("{");
	    output.write("\n\"annotation\":[\n");
	} catch (Exception x) {
	}
    };

    @Override
    public void footer(Writer output, IQueryRetrieval<PropertyAnnotation> query) {
	try {
	    output.write("\t\n]");
	    output.write("\n}");
	} catch (Exception x) {
	}
    };

}
