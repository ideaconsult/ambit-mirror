package ambit2.db.processors;

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.FP1024Writer.FPTable;

/**
 * Given {@link IStructureRecord} generates fingerprint and assigns it to the relevant properties of IStructureRecord
 * @author nina
 *
 */
public class BitSetGenerator extends DefaultAmbitProcessor<IStructureRecord,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1046795418089401843L;
	protected IProcessor<IAtomContainer,BitSet> processor;
	protected MoleculeReader reader = new MoleculeReader();
	protected AtomConfigurator c = new AtomConfigurator();
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
    public IStructureRecord process(IStructureRecord target)
    		throws AmbitException {
    	IAtomContainer a = reader.process(target);
    	//CDKHueckelAromaticityDetector d = new CDKHueckelAromaticityDetector();
    	long mark = System.currentTimeMillis();    	
    	try {
    	//d.detectAromaticity(a);
        	BitSet bitset = processor.process(a);
        	target.setProperty(Property.getInstance(fpmode.getProperty(),fpmode.getProperty()),bitset);	
    	} catch (Exception x) {
        	target.setProperty(Property.getInstance(fpmode.getProperty(),fpmode.getProperty()),null);	
    	} finally {
        	target.setProperty(Property.getInstance(fpmode.getTimeProperty(),fpmode.getProperty()),System.currentTimeMillis()-mark);
    	}

    	return target;

    }	

}
