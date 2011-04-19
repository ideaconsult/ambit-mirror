package ambit2.descriptors.processors;

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.processors.StructureKeysBitSetGenerator;

/**
 * Given {@link IStructureRecord} generates fingerprint and assigns it to the relevant properties of IStructureRecord
 * @author nina
 *
 */
public class BitSetGenerator extends AbstractPropertyGenerator<BitSet> {
	public enum FPTable {
		fp1024 {
			@Override
			public String getProperty() {
				return AmbitCONSTANTS.Fingerprint;
			}
			@Override
			public String getStatusProperty() {
				return AmbitCONSTANTS.FingerprintSTATUS;
			}
			@Override
			public String getTimeProperty() {
				return AmbitCONSTANTS.FingerprintTIME;
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() throws Exception {
				return new FingerprintGenerator();
			}
			@Override
			public String getTable() {
				return "fp1024";
			}
			@Override
			public String toString() {
				return "Fingerprints (hashed 1024 fingerprnts used for similarity search and prescreening)";
			}
			
		},
		fp1024_struc {
			@Override
			public String getProperty() {
				return AmbitCONSTANTS.Fingerprint;
			}
			@Override
			public String getStatusProperty() {
				return AmbitCONSTANTS.FingerprintSTATUS;
			}
			@Override
			public String getTimeProperty() {
				return AmbitCONSTANTS.FingerprintTIME;
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() throws Exception {
				return new FingerprintGenerator();
			}
			@Override
			public String getTable() {
				return "fp1024_struc";
			}
			@Override
			public String toString() {
				return "Fingerprints (hashed 1024 fingerprnts used for similarity search and prescreening)";
			}
			
		},			
		sk1024 {
			@Override
			public String getProperty() {
				return AmbitCONSTANTS.StructuralKey;
			}
			@Override
			public String getTimeProperty() {
				return AmbitCONSTANTS.StructuralKey_TIME;
			}
			@Override
			public String getStatusProperty() {
				return AmbitCONSTANTS.StructuralKey_STATUS;
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() throws Exception{
				return new StructureKeysBitSetGenerator(); 
			}
			@Override
			public String getTable() {
				return "sk1024";
			}
			@Override
			public String toString() {
				return "Structural keys (1024 structural fragments used to speed up SMARTS search)";
			}				
		},
		smarts_accelerator {
			@Override
			public String getProperty() {
				return CMLUtilities.SMARTSProp;
			}
			@Override
			public String getStatusProperty() {
				return null;
			}
			@Override
			public String getTimeProperty() {
				return null;
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() throws Exception{
				return null;
			}
			@Override
			public String getTable() {
				return "structure";
			}
			@Override
			public String toString() {
				return "SMARTS data";
			}
			
		},	
		atomenvironments {
			@Override
			public String getProperty() {
				return AmbitCONSTANTS.AtomEnvironment;
			}
			@Override
			public String getStatusProperty() {
				return null;
			}
			@Override
			public String getTimeProperty() {
				return null;
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() {
				return null;
			}
			@Override
			public String getTable() {
				return "fpaechemicals";
			}
			@Override
			public String toString() {
				return "Atom Environments";
			}
			
		};					
		abstract public String getProperty();
		abstract public String getTimeProperty();
		abstract public String getStatusProperty();
		abstract public IProcessor<IAtomContainer,BitSet> getGenerator() throws Exception;
		abstract public String getTable();
};
	/**
	 * 
	 */
	private static final long serialVersionUID = -1046795418089401843L;
	protected IProcessor<IAtomContainer,BitSet> processor;
	protected FPTable fpmode = FPTable.fp1024;
	
	public FPTable getFpmode() {
		return fpmode;
	}
	public BitSetGenerator() throws Exception{
		this(FPTable.fp1024);
	}
	public BitSetGenerator(FPTable fpmode) throws Exception {
		super();
		setFpmode(fpmode);
	}
	public void setFpmode(FPTable fpmode) throws Exception{
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
