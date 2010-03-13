package ambit2.model.numeric;

import Jama.Matrix;

/**
 * Leverage
 * @author nina
 *
 */
public class DataCoverageLeverage extends DataCoverage<Matrix> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8841985800478607411L;
	protected Matrix hat = null;
	protected Matrix XTX1 = null;
	protected double threshold;
	
	public DataCoverageLeverage() {
		super();
		setAppDomainMethodType(ADomainMethodType._modeLEVERAGE);
	}
	public boolean build(Matrix data) {
		if (data ==null) return false;
		if (data.getRowDimension() == 0) return false;
		if (data.getColumnDimension() == 0) return false;
		threshold = 3* data.getRowDimension() * (data.getColumnDimension()+1);
		Matrix X = data;
		Matrix XT = X.transpose();
		//(Xtransposed * X)^-1 , will be needed for estimation
		XTX1 = XT.times(X).inverse();
		hat = X.times(XTX1).times(XT);
		return true;
	}

	public double[] predict(Matrix data) {
		if (hat == null) return null;
		if (XTX1 == null) return null;
		Matrix x = data;
		Matrix xt = x.transpose();
		Matrix h = x.times(XTX1).times(xt);
		double[] leverage = new double[data.getRowDimension()];
		for (int i=0; i < leverage.length;i++)
			leverage[i] = h.get(i,i);
		return leverage;
	}
	
	@Override
	public int getDomain(double coverage) {
		return coverage>threshold?1:0;
	}
	@Override
	public int[] getDomain(double[] coverage) {
		int[] results = new int[coverage.length];
		for (int i=0; i < coverage.length;i++)
			results[i] = coverage[i]>threshold?1:0;
		return results;
	}

}
