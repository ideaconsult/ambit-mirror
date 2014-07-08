package ambit2.model.numeric;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import Jama.Matrix;

/**
 * Leverage
 * @author nina
 *
 */
public class DataCoverageLeverageHatMatrix extends DataCoverage<Matrix> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8841985800478607411L;
	protected transient Matrix hat = null;
	protected transient Matrix XTX1 = null;
	protected transient NumberFormat format = DecimalFormat.getNumberInstance(Locale.US);
	protected Matrix X;
	protected static final String protocol = "WRITE_TRAINING_DATA";
	
	public DataCoverageLeverageHatMatrix() {
		super();
		setAppDomainMethodType(ADomainMethodType._modeLEVERAGE);
		format.setMaximumFractionDigits(6);
	}
	public boolean build(Matrix data) {
		
		if (data ==null) return false;
		if (data.getRowDimension() == 0) return false;
		if (data.getColumnDimension() == 0) return false;
		threshold = 3.0 * data.getRowDimension() * (data.getColumnDimension()+1);
		X = data;
		
		Matrix XT = X.transpose();
		
		Matrix tmp = XT.times(X);
		//(Xtransposed * X)^-1 , will be needed for estimation
		XTX1 = tmp.inverse();
		tmp = null;
		tmp = X.times(XTX1);
		hat = tmp.times(XT);
		tmp = null;
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
	

	protected void writeMatrix(Matrix matrix,ObjectOutputStream stream) throws IOException {
		stream.writeInt(matrix.getRowDimension());
		stream.writeInt(matrix.getColumnDimension());

		for (int i=0; i < matrix.getRowDimension();i++) {
			for (int j=0; j < matrix.getColumnDimension();j++) {
				stream.writeDouble(matrix.get(i,j));
				stream.flush();
				
			}
		}
		
	}
	protected Matrix readMatrix(ObjectInputStream stream) throws IOException {
		int rows = stream.readInt();
		int cols = stream.readInt();
		Matrix matrix = new Matrix(rows,cols);
		for (int i=0; i < rows;i++)
			for (int j=0; j < cols;j++) 
				matrix.set(i, j,stream.readDouble());
		
		return matrix;
	}	
    private void readObject(ObjectInputStream stream) throws IOException,    ClassNotFoundException {
    	Object o = stream.readObject();
    	if (!protocol.equals(o.toString())) throw new IOException(String.format("Expected '%s' instead of '%s'",protocol,o));
		o = stream.readObject();
		appDomainMethodType = ADomainMethodType.valueOf(o.toString());

	    threshold = stream.readDouble();		
	    pThreshold = stream.readDouble();		

		Matrix data = readMatrix(stream);
		build(data);

	}	
	private void writeObject(ObjectOutputStream stream) throws IOException {
		 //   stream.defaultWriteObject();
			stream.writeObject(protocol);
		    stream.writeObject(appDomainMethodType.name());
		    stream.writeDouble(threshold);
		    stream.writeDouble(pThreshold);
		    //seems to be more efficient to keep the original data (row*col) , instead of hat matrix (row*row)
		    writeMatrix(X,stream);
		    /*
		    writeMatrix(hat,stream);
		    writeMatrix(XTX1,stream);
		    */
		}    
}
