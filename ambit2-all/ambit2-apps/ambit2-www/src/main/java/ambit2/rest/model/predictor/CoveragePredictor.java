package ambit2.rest.model.predictor;

import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.model.numeric.DataCoverage;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public abstract class CoveragePredictor<Input, T> extends ModelPredictor<DataCoverage, Input> {

    /**
     * 
     */
    private static final long serialVersionUID = 2227441480460487334L;

    public CoveragePredictor(Reference applicationRootReference, ModelQueryResults model,
	    ModelURIReporter modelReporter, PropertyURIReporter propertyReporter, String[] targetURI)
	    throws ResourceException {
	super(applicationRootReference, model, modelReporter, propertyReporter, targetURI);
    }

    protected abstract T transform(Input target) throws AmbitException;

    @Override
    public Object predict(Input target) throws AmbitException {
	double[] d = null;
	if (predictor instanceof DataCoverage) {
	    d = ((DataCoverage) predictor).predict(transform(target));
	} else
	    throw new AmbitException("Not supported " + predictor.getClass().getName());

	if (d == null)
	    throw new AmbitException();
	return new double[] { d[0], predictor.getDomain(d[0]) };
    }

    public void assignResults(IStructureRecord record, Object value) throws AmbitException {
	Iterator<Property> predicted = model.getPredicted().getProperties(true);
	int count = 0;
	while (predicted.hasNext()) {
	    Property p = predicted.next();
	    if (value instanceof double[]) {
		if (p.getName().startsWith("AppDomain_"))
		    record.setProperty(p, ((double[]) value)[1]);
		else
		    record.setProperty(p, ((double[]) value)[0]);
	    } else {
		record.setProperty(p, value);
	    }
	    count++;
	}
	if (count == 0)
	    throw new AmbitException("No property to assign results!!!");
    }

    @Override
    public String toString() {
	StringBuilder b = new StringBuilder();
	b.append("\n-- AMBIT Coverage applicability domain assessment --\n");
	b.append(super.toString());

	return b.toString();

    }
}
