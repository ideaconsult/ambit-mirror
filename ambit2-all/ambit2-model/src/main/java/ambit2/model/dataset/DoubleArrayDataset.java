package ambit2.model.dataset;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.model.IDataset;

/**
 * Fixed length dataset, for testing purposes
 * @author nina
 *
 */
public class DoubleArrayDataset implements IDataset<Integer, double[], Double> {
	protected double[][] values;
	protected double[] observed;
	protected double[] predicted;	
	protected int record= 0;

	public DoubleArrayDataset(double[][] values,double[] observed) {
		this.values = values;
		this.observed = observed;
		predicted = new double[values.length];
		record = 0;
	}
	public boolean first() throws AmbitException, UnsupportedOperationException {
		this.record = 0;
		return (values != null)&& (values.length > 0);
	}

	public double[] getFeatures() {
		return values[record];
	}

	public Integer getID() {
		return record;
	}

	public Double getObserved() {
		return observed[record];
	}

	public Double getPredicted() {
		return predicted[record];
	}

	public void handleError(Exception x) throws AmbitException {
		throw new AmbitException(x);
		
	}

	public boolean last() throws AmbitException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean next() throws AmbitException, UnsupportedOperationException {
		record++;
		return (values != null)&& (record < values.length);
	}

	public boolean prev() throws AmbitException, UnsupportedOperationException {
		record--;
		return (values != null) && (record >= 0);
	}

	public void setPredicted(Double result) {
		if (result != null)
			predicted[record] = result;
		
	}

	public int size() throws AmbitException, UnsupportedOperationException {
		return values.length;
	}


}
