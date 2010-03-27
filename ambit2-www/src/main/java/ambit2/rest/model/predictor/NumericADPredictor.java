package ambit2.rest.model.predictor;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import weka.core.Attribute;
import weka.core.Instance;
import Jama.Matrix;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.model.numeric.DataCoverageDescriptors;
import ambit2.model.numeric.DataCoverageLeverage;
import ambit2.rest.model.ModelURIReporter;

public class NumericADPredictor extends	CoveragePredictor<Instance,Matrix> {

	public NumericADPredictor(Reference applicationRootReference,
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
	protected Matrix transform(Instance target) throws AmbitException {
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
