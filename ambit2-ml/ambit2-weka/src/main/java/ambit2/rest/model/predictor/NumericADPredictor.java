package ambit2.rest.model.predictor;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import weka.core.Attribute;
import Jama.Matrix;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.model.numeric.DataCoverageDescriptors;
import ambit2.model.numeric.DataCoverageLeverage;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class NumericADPredictor extends	CoveragePredictor<IStructureRecord,Matrix> {
	protected PropertyURIReporter propertyReporter;
	public NumericADPredictor(Reference applicationRootReference,
			ModelQueryResults model, ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter,
			String[] targetURI) throws ResourceException {
		super(applicationRootReference, model, modelReporter, null,targetURI);
		this.propertyReporter = propertyReporter;
		setValuesRequired(true);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4940602547639105140L;

	@Override
	protected boolean isSupported(Object predictor) throws ResourceException {
		return (predictor instanceof DataCoverageLeverage) || (predictor instanceof DataCoverageDescriptors) ;
	}
	
	protected Matrix getAsMatrix(IStructureRecord target) throws AmbitException {
		Matrix matrix = new Matrix(1,header.numAttributes()-1);
		for (int j=0; j < matrix.getColumnDimension();j++) matrix.set(0,j,Double.NaN);
		
		for (Property p : target.getRecordProperties()) {
			
			String url = propertyReporter.getURI(p);
			Attribute attr = header.attribute(url);
			if (attr!=null) 
				if (attr.isNumeric()) {
					Number number = ((Number)target.getRecordProperty(p));
					matrix.set(0,attr.index()-1,number.doubleValue());
				}
				else throw new AmbitException(String.format("%s not numeric!",attr.name()));
		}
		for (int j=0; j < matrix.getColumnDimension();j++)
				if (Double.isNaN(matrix.get(0,j)))
						throw new AmbitException(
								String.format("Missing value %s at /compound/%d/conformer/%d",
										header.attribute(j),target.getIdchemical(),target.getIdstructure()));
				
		return matrix;
	}	
	@Override
	protected Matrix transform(IStructureRecord target) throws AmbitException {
		/*
		for (int i=1; i < header.numAttributes();i++) {
			Attribute attr = target.dataset().attribute(header.attribute(i).name());
			if (attr.isNumeric())
				matrix.set(0,i-1,target.value(attr));
			else throw new AmbitException(String.format("%s not numeric!",attr.name()));
		}
		*/
		return getAsMatrix(target);
	}
	
	protected double[][] predictionInstanceAsArray(IStructureRecord target) throws AmbitException {
		double[][] matrix = new double[1][header.numAttributes()-1];
		
		for (Property p : target.getRecordProperties()) {
			String url = propertyReporter.getURI(p);
			Attribute attr = header.attribute(url);
			if (attr!=null) 
				if (attr.isNumeric()) 
					matrix[0][attr.index()-1] = ((Number)target.getRecordProperty(p)).doubleValue();
					else throw new AmbitException(String.format("%s not numeric!",attr.name()));
		}	
		
		for (int j=0; j < matrix[0].length;j++)
			if (Double.isNaN(matrix[0][j]))
					throw new AmbitException(
								String.format("Missing value %s at /compound/%d/conformer/%d",
										header.attribute(j),target.getIdchemical(),target.getIdstructure()));
		/*
		for (int i=1; i < header.numAttributes();i++) {
			Attribute attr = target.dataset().attribute(header.attribute(i).name());
			if (attr.isNumeric())
				matrix[0][i-1]= target.value(attr);
			else throw new AmbitException(String.format("%s not numeric!",attr.name()));
		}
		*/
		return matrix;
	}	
	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		return null;
	}
	@Override
	protected void extractRecordID(IStructureRecord target, String url, IStructureRecord record)
			throws AmbitException {
		record.setIdchemical(target.getIdchemical());
		record.setIdstructure(target.getIdstructure());
	}


}
