package ambit.io.batch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit.io.MDLWriter;
import ambit.misc.AmbitCONSTANTS;

public class LoggedBatchStatistics extends DefaultBatchStatistics {
	protected MDLWriter writer = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6792026252573353749L;

	public void increment(int recordType, Object o) {
		if (recordType == RECORDS_ERROR)		
			incrementError(recordType,o,null);
	}
	@Override
		
	public void incrementError(int recordType, Object o, Exception x) {
		super.increment(recordType, o);
		if (o instanceof IAtomContainer) 
		try {
			if (writer == null) writer = new MDLWriter(new FileWriter("batch_errors.sdf"));
    	    Hashtable props = (Hashtable)((IAtomContainer)o).getProperties().clone();
    	    props.remove(CDKConstants.ALL_RINGS);
    	    props.remove(CDKConstants.SMALLEST_RINGS);
    	    props.remove(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
    	    props.remove(AmbitCONSTANTS.AMBIT_IDSUBSTANCE);
    	    if (x != null)
    	    	props.put(recordType,x);
    	    else
    	    	props.put("ERROR",recordType);
    		((MDLWriter) writer).setSdFields(props);
			
			writer.write((IAtomContainer)o);
		} catch (Exception e) {
			writer = null;
			e.printStackTrace();
		}
			
	}
	@Override
	public void completed() {
		super.completed();
		if (writer != null)
			try {
				writer.close();
			} catch (IOException x) {
				x.printStackTrace();
			}
	}
}
