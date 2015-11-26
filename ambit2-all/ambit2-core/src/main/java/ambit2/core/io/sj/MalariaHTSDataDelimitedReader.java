package ambit2.core.io.sj;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.io.IteratingDelimitedFileReader;

/**
 * XLS file
SAMPLE	batch
SJ000001054	1
SJ000001111	1
SJ000001119	1

SDF file
SJ000001054-1
 * @author nina
 *
 */
public class MalariaHTSDataDelimitedReader extends IteratingDelimitedFileReader {
	protected static final String col_SAMPLE="SAMPLE";
	protected static final String col_batch="batch";
	public MalariaHTSDataDelimitedReader(InputStream in) throws CDKException {
		super(new InputStreamReader(in));
	}

	@Override
	public Object next() {
		IAtomContainer mol = (IAtomContainer) super.next();
		Object sample = mol.getProperty(col_SAMPLE);
		Object batch = mol.getProperty(col_batch);
		if (sample==null) return mol;
		if (sample.toString().indexOf("-")<0) {
			batch = batch==null?"1":batch;
			mol.setProperty(col_SAMPLE,String.format("%s-%s",sample,batch));
		}
		return mol;
	}
	



}
