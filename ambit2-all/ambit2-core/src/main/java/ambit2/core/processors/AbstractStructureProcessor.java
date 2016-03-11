package ambit2.core.processors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import net.idea.modbcum.i.processors.IProcessor;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import com.google.common.collect.Sets.SetView;

/**
 * Parent class for {@Ilink IAtomContainer} processors
 * 
 * @author nina
 * 
 */
public abstract class AbstractStructureProcessor implements
		IProcessor<IAtomContainer, IAtomContainer> {
	protected Logger logger;
	protected static final String at_property = "AtomTypes";
	protected static final String at_property_added = "AtomTypes.added";
	protected static final String at_property_removed = "AtomTypes.removed";
	/**
	 * 
	 */
	private static final long serialVersionUID = -4672277305265874844L;
	protected boolean sparseproperties = true;
	protected boolean atomtypeasproperties = false;
	protected boolean enabled = true;

	public AbstractStructureProcessor(Logger logger) {
		super();
		if (logger != null)
			this.logger = logger;
		else
			Logger.getLogger(getClass().getName());
	}

	public boolean isSparseproperties() {
		return sparseproperties;
	}

	/**
	 * if true assigns empty properties
	 * 
	 * @param sparseproperties
	 */
	public void setSparseproperties(boolean sparseproperties) {
		this.sparseproperties = sparseproperties;
	}

	public boolean isAtomtypeasproperties() {
		return atomtypeasproperties;
	}

	/**
	 * Assign atomtypes as atomcontainer properties
	 * 
	 * @param atomtypeasproperties
	 */
	public void setAtomtypeasproperties(boolean atomtypeasproperties) {
		this.atomtypeasproperties = atomtypeasproperties;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean value) {
		this.enabled = value;
	}

	@Override
	public long getID() {
		return serialVersionUID;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public void open() throws Exception {
	}

	public IAtomContainer atomtypes2property(IAtomContainer mol,
			Object compareWith, boolean sparseProperties) throws Exception {
		return atomtypes2property(at_property, mol, compareWith,
				sparseProperties);
	}

	/**
	 * Used as criteria whether to assign atom types as properties e.g. assign
	 * only if charged atoms. Subclass to define your own.
	 * 
	 * @param atom
	 * @return
	 */
	protected boolean applicable(IAtom atom) {
		return atom.getFormalCharge() != 0;
	}

	public IAtomContainer atomtypes2property(String property,
			IAtomContainer mol, Object compareWith, boolean sparseProperties)
			throws Exception {

		Set<String> b = null;
		try {
			/*
			 * if (mol.getAtomCount() == 1) { b = new HashSet<String>();
			 * b.add("Single atom"); }
			 */
			for (IAtom atom : mol.atoms()) {
				if (!applicable(atom))
					continue;
				if (b == null)
					b = new HashSet<String>();
				b.add(atom.getAtomTypeName());

			}

			if (compareWith != null && (compareWith instanceof Set)) {
				Set set = b == null ? Collections.emptySet() : b;
				SetView<String> difference = com.google.common.collect.Sets
						.difference(set, (Set<String>) compareWith);
				mol.setProperty(property + ".added", difference);
				difference = com.google.common.collect.Sets.difference(
						(Set<String>) compareWith, set);
				mol.setProperty(property + ".removed", difference);
			} else {
				if (b != null)
					mol.setProperty(property, b);
				else if (!sparseProperties)
					mol.setProperty(property, null);
			}
		} catch (Exception x) {
			throw x;
		} finally {
			return mol;
		}

	}

	public static IAtomContainer atomtypes2properties(IAtomContainer mol)
			throws Exception {
		if (mol == null)
			return mol;

		try {
			for (IAtom atom : mol.atoms()) {
				if (atom.getFormalCharge() == 0)
					continue;
				String pname = String.format("AtomType.%s",
						atom.getAtomTypeName());
				if (mol.getProperty(pname) == null)
					mol.setProperty(pname, 1);
				else
					mol.setProperty(pname, (int) mol.getProperty(pname) + 1);

			}
		} catch (Exception x) {
			mol.setProperty("ERROR.", x.getMessage());
		}
		return mol;
	}

	public static IAtomContainer atomtypes2properties_init(IAtomContainer mol)
			throws Exception {
		if (mol == null)
			return mol;

		try {
			for (IAtom atom : mol.atoms()) {
				if (atom.getFormalCharge() == 0)
					continue;
				String pname = String.format("AtomType.%s",
						atom.getAtomTypeName());
				if (mol.getProperty(pname) == null)
					mol.setProperty(pname, 1);
				else
					mol.setProperty(pname, (int) mol.getProperty(pname) + 1);

			}
		} catch (Exception x) {
			mol.setProperty("ERROR.", x.getMessage());
		}
		return mol;
	}

}
