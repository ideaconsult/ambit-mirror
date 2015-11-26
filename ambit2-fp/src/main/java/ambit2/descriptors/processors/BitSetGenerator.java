package ambit2.descriptors.processors;

import java.util.BitSet;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.config.Preferences;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.processors.SMARTSPropertiesReader;

/**
 * Given {@link IStructureRecord} generates fingerprint and assigns it to the relevant properties of IStructureRecord
 * @author nina
 *
 */
public class BitSetGenerator extends AbstractPropertyGenerator<BitSet> {
	protected transient SMARTSPropertiesReader bondPropertiesReader;
	protected transient AtomConfigurator configurator;
	protected Property smartsProperty = Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	
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
				Object smartsdata = target.getRecordProperty(smartsProperty);
	
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
