package ambit2.rest.model;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Request;

import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.model.predictor.ModelPredictor;

public class ModelTextReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends ModelURIReporter<Q> {

    /**
     * 
     */
    private static final long serialVersionUID = 2056426784976424290L;

    public ModelTextReporter(Request baseRef) {
	super(baseRef);
    }

    @Override
    public Object processItem(ModelQueryResults model) throws AmbitException {
	try {
	    ModelPredictor predictor = ModelPredictor.getPredictor(model, request);
	    getOutput().write(predictor.toString());
	    return null;

	} catch (AmbitException x) {
	    throw x;
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }
}
