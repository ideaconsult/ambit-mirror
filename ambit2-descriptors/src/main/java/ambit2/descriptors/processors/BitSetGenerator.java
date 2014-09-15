package ambit2.descriptors.processors;

import java.util.BitSet;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.config.Preferences;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.processors.SMARTSPropertiesReader;
import ambit2.smarts.processors.StructureKeysBitSetGenerator;

/**
 * Given {@link IStructureRecord} generates fingerprint and assigns it to the relevant properties of IStructureRecord
 * @author nina
 *
 */
public class BitSetGenerator extends AbstractPropertyGenerator<BitSet> {
	protected transient SMARTSPropertiesReader bondPropertiesReader;
	protected transient AtomConfigurator configurator;
	protected Property smartsProperty = Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	public enum FPTable {
		fp1024 {
			@Override
			public String getProperty() {
				return name();
			}
			@Override
			public String getStatusProperty() {
				return name()+".status";
			}
			@Override
			public String getTimeProperty() {
				return name()+".time";
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() throws Exception {
				return new FingerprintGenerator(new Fingerprinter(1024));
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
				return new FingerprintGenerator(new Fingerprinter(1024));
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
				return name();
			}
			@Override
			public String getTimeProperty() {
				return name()+".time";
			}
			@Override
			public String getStatusProperty() {
				return name()+".status";
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
			
		},
		inchi {
			@Override
			public String getProperty() {
				return Property.opentox_InChI;
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
				return "chemicals";
			}
			@Override
			public String toString() {
				return "InChI in chemicals table";
			}
		},
		pc1024 {
			@Override
			public String getProperty() {
				return name();
			}
			@Override
			public String getTimeProperty() {
				return name()+".time";
			}
			@Override
			public String getStatusProperty() {
				return name()+".status";
			}
			@Override
			public IProcessor<IAtomContainer, BitSet> getGenerator() throws Exception{
				return new FingerprintGenerator(new PubchemFingerprinter());
			}
			@Override
			public String getTable() {
				return "pc1024";
			}
			@Override
			public String toString() {
				return "PubChem fingerprints";
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
		try {
			return processor.process(atomContainer);
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	protected IAtomContainer getAtomContainer(IStructureRecord target)
			throws AmbitException {
		// TODO Auto-generated method stub
		IAtomContainer mol = super.getAtomContainer(target);
		if (mol==null) return null;
		try {
			
			if ("true".equals(Preferences
					.getProperty(Preferences.FASTSMARTS))) {
				Object smartsdata = target.getProperty(smartsProperty);
	
				if (smartsdata != null) {
					mol.setProperty(smartsProperty, smartsdata);
					if (bondPropertiesReader == null) bondPropertiesReader = new SMARTSPropertiesReader();
					mol = bondPropertiesReader.process(mol);
				} else {
					if ((smartsProperty!=null)&&(mol!=null)) mol.removeProperty(smartsProperty);
					if (configurator==null) configurator = new AtomConfigurator();
					mol = configurator.process(mol);
					
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
	
				}
			} else {
				mol.removeProperty(smartsProperty);
				if (configurator==null) configurator = new AtomConfigurator();
				mol = configurator.process(mol);
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
	
			}
  			

		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return mol;
	}
}
