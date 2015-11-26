package ambit2.core.processors.structure;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Transforms {@link IStructureRecord} into {@link IAtomContainer} , calculates
 * some property and assigns it to {@link IStructureRecord}
 * 
 * @author nina
 * 
 */
public abstract class AbstractPropertyGenerator<P> extends
		DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
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
			target.setRecordProperty(getProperty(), p);
		} catch (Exception x) {
			target.setRecordProperty(getProperty(), null);
		} finally {
			Property tp = getTimeProperty();
			if (tp != null)
				target.setRecordProperty(getTimeProperty(),
						System.currentTimeMillis() - mark);
		}
		return target;
	}

	protected abstract Property getProperty();

	protected abstract Property getTimeProperty();

	protected abstract P generateProperty(IAtomContainer atomContainer)
			throws AmbitException;

	protected IAtomContainer getAtomContainer(IStructureRecord target)
			throws AmbitException {
		return reader.process(target);
	}

	@Override
	public void open() throws Exception {
		super.open();
	}

	@Override
	public void close() throws Exception {
		super.close();
	}
}
