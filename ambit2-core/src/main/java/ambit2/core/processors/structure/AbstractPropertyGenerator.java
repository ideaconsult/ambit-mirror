package ambit2.core.processors.structure;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;

/**
 * Transforms {@link IStructureRecord} into {@link IAtomContainer} , calculates some property and assigns it to {@link IStructureRecord}
 * @author nina
 *
 */
public abstract class AbstractPropertyGenerator<P> extends	DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6923827535809255431L;
	protected MoleculeReader reader = new MoleculeReader();
	
	
	public IStructureRecord process(IStructureRecord target)
			throws AmbitException {
    	IAtomContainer atomContainer = getAtomContainer(target);
    	long mark = System.currentTimeMillis();    	
    	try {
        	P p = generateProperty(atomContainer);
        	target.setProperty(getProperty(),p);	
    	} catch (Exception x) {
        	target.setProperty(getProperty(),"");	
    	} finally {
    		Property tp = getTimeProperty();
    		if (tp!=null)
    			target.setProperty(getTimeProperty(),System.currentTimeMillis()-mark);
    	}
    	return target;
	}
	
	protected abstract Property getProperty();
	protected abstract Property getTimeProperty();
	protected abstract P generateProperty(IAtomContainer atomContainer) throws AmbitException ;
	protected IAtomContainer getAtomContainer(IStructureRecord target) throws AmbitException {
		return reader.process(target);
	}
}
