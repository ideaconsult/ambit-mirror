package ambit2.db.processors;

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.db.processors.FP1024Writer.FPTable;

/**
 * Given {@link IStructureRecord} generates fingerprint and assigns it to the relevant properties of IStructureRecord
 * @author nina
 *
 */
public class BitSetGenerator extends AbstractPropertyGenerator<BitSet> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1046795418089401843L;
	protected IProcessor<IAtomContainer,BitSet> processor;
	protected FPTable fpmode = FPTable.fp1024;
	
	public FPTable getFpmode() {
		return fpmode;
	}
	public BitSetGenerator() {
		this(FPTable.fp1024);
	}
	public BitSetGenerator(FPTable fpmode) {
		super();
		setFpmode(fpmode);
	}
	public void setFpmode(FPTable fpmode) {
		this.fpmode = fpmode;
		processor = fpmode.getGenerator();
	}
	
    @Override
    protected Property getProperty() {
    	return Property.getInstance(fpmode.getProperty(),fpmode.getProperty());
    }
    @Override
    protected Property getTimeProperty() {
    	return Property.getInstance(fpmode.getTimeProperty(),fpmode.getTimeProperty());
    }
	@Override
	protected BitSet generateProperty(IAtomContainer atomContainer)
			throws AmbitException {
		return processor.process(atomContainer);
	}
}
