package ambit2.model;

import net.idea.modbcum.i.exceptions.AmbitException;


public interface IDataset<ID,Features,ResultType extends Comparable<?>> {
	ID getID();
	Features getFeatures();
	ResultType getObserved();
	ResultType getPredicted();
	void setPredicted(ResultType result);	
	boolean first() throws AmbitException, UnsupportedOperationException;
	boolean last() throws AmbitException, UnsupportedOperationException;	
	boolean next() throws AmbitException, UnsupportedOperationException;
	boolean prev() throws AmbitException, UnsupportedOperationException;	
	int size() throws AmbitException, UnsupportedOperationException;
	void handleError(Exception x) throws AmbitException;
}