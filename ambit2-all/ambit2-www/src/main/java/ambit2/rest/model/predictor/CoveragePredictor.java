package ambit2.rest.model.predictor;

import java.util.Iterator;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import weka.core.Attribute;
import weka.core.Instance;
import Jama.Matrix;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.model.numeric.DataCoverage;
import ambit2.model.numeric.DataCoverageDescriptors;
import ambit2.model.numeric.DataCoverageLeverage;
import ambit2.rest.model.ModelURIReporter;

public class CoveragePredictor extends	ModelPredictor<DataCoverage,Instance> {

	public CoveragePredictor(Reference applicationRootReference,
			ModelQueryResults model, ModelURIReporter modelReporter,
			String[] targetURI) throws ResourceException {
		super(applicationRootReference, model, modelReporter, null,targetURI);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4940602547639105140L;

	@Override
	protected boolean isSupported(Object predictor) throws ResourceException {
		return (predictor instanceof DataCoverageLeverage) || (predictor instanceof DataCoverageDescriptors) ;
	}
	@Override
	public Object predict(Instance target) throws AmbitException {
		double[] d = null;
		if (predictor instanceof DataCoverage) {
			d = ((DataCoverage)predictor).predict(predictionInstanceAsMatrix(target));
		} else throw new AmbitException("Not supported "+predictor.getClass().getName());
		
		if (d==null) throw new AmbitException();
		return new double[] {d[0],predictor.getDomain(d[0])};
	}
	
	public void assignResults(IStructureRecord record, Object value) throws AmbitException {
		Iterator<Property> predicted = model.getPredicted().getProperties(true);
		int count = 0;
		while (predicted.hasNext()) {
			Property p = predicted.next();
			if (p.getName().startsWith("AppDomain_"))
				record.setProperty(p,((double[])value)[1]);
			else
				record.setProperty(p,((double[])value)[0]);
			count++;
		}
		if (count==0) throw new AmbitException("No property to assign results!!!");
	}
	protected Matrix predictionInstanceAsMatrix(Instance target) throws AmbitException {
		Matrix matrix = new Matrix(1,header.numAttributes()-1);
		for (int i=1; i < header.numAttributes();i++) {
			Attribute attr = target.dataset().attribute(header.attribute(i).name());
			if (attr.isNumeric())
				matrix.set(0,i-1,target.value(attr));
			else throw new AmbitException(String.format("%s not numeric!",attr.name()));
		}
		return matrix;
	}
	
	protected double[][] predictionInstanceAsArray(Instance target) throws AmbitException {
		double[][] matrix = new double[1][header.numAttributes()-1];
		for (int i=1; i < header.numAttributes();i++) {
			Attribute attr = target.dataset().attribute(header.attribute(i).name());
			if (attr.isNumeric())
				matrix[0][i-1]= target.value(attr);
			else throw new AmbitException(String.format("%s not numeric!",attr.name()));
		}
		return matrix;
	}	
	@Override
	public String getCompoundURL(Instance target) throws AmbitException {
		return target.stringValue(0);
	}
}
