package ambit2.descriptors;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.core.data.ArrayResult;
import ambit2.core.data.StringArrayResult;

public abstract class AbstractKekulizationVerifier implements
		IMolecularDescriptor {
	protected static final String version = "1.4.9";
	protected static final String OK = "OK";

	protected enum names_index {
		NumSingleBondOriginal, NumDoubleBondOriginal, NumAromaticBondOriginal,

		NumSingleBondKekule, NumDoubleBondKekule, NumAromaticBondKekule,

		time_ms, status; // error reporting
		@Override
		public String toString() {
			return String.format("%s_%s", super.toString(), version);
		}
	};

	public AbstractKekulizationVerifier() {
		super();
		// typer.setTimeout(30*60*1000); //15 min
	}

	protected static String[] names = {
			names_index.NumSingleBondOriginal.toString(),
			names_index.NumDoubleBondOriginal.toString(),
			names_index.NumAromaticBondOriginal.toString(),

			names_index.NumSingleBondKekule.toString(),
			names_index.NumDoubleBondKekule.toString(),
			names_index.NumAromaticBondKekule.toString(),

			names_index.time_ms.toString(), names_index.status.toString() };

	protected abstract IAtomContainer transform2Kekule(IAtomContainer mol)
			throws CDKException;

	public DescriptorValue calculate(IAtomContainer mol) {
		long now = System.currentTimeMillis();
		Object[] results = new Object[names.length];
		for (int i = 0; i < results.length; i++)
			results[i] = 0;
		try {

			for (IBond bond : mol.bonds()) {
				if (bond.getFlag(CDKConstants.ISAROMATIC))
					results[names_index.NumAromaticBondOriginal.ordinal()] = (Integer) results[names_index.NumAromaticBondOriginal
							.ordinal()] + 1;
				if (bond.getOrder().equals(IBond.Order.SINGLE))
					results[names_index.NumSingleBondOriginal.ordinal()] = (Integer) results[names_index.NumSingleBondOriginal
							.ordinal()] + 1;
				if (bond.getOrder().equals(IBond.Order.DOUBLE))
					results[names_index.NumDoubleBondOriginal.ordinal()] = (Integer) results[names_index.NumDoubleBondOriginal
							.ordinal()] + 1;

			}

			now = System.currentTimeMillis();
			// now clear the double bonds
			for (IBond bond : mol.bonds())
				if (bond.getFlag(CDKConstants.ISAROMATIC))
					bond.setOrder(Order.SINGLE);

			IAtomContainer kekuleMol = transform2Kekule(mol);
			results[names_index.time_ms.ordinal()] = System.currentTimeMillis()
					- now;

			for (IBond bond : kekuleMol.bonds()) {
				if (bond.getFlag(CDKConstants.ISAROMATIC))
					results[names_index.NumAromaticBondKekule.ordinal()] = (Integer) results[names_index.NumAromaticBondKekule
							.ordinal()] + 1;
				if (bond.getOrder().equals(IBond.Order.SINGLE))
					results[names_index.NumSingleBondKekule.ordinal()] = (Integer) results[names_index.NumSingleBondKekule
							.ordinal()] + 1;
				if (bond.getOrder().equals(IBond.Order.DOUBLE))
					results[names_index.NumDoubleBondKekule.ordinal()] = (Integer) results[names_index.NumDoubleBondKekule
							.ordinal()] + 1;

			}
			results[names_index.status.ordinal()] = "OK";
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), new ArrayResult(results),
					getDescriptorNames());
		} catch (Exception x) {
			for (IBond bond : mol.bonds()) {
				if (bond.getFlag(CDKConstants.ISAROMATIC))
					results[names_index.NumAromaticBondKekule.ordinal()] = -1;
				if (bond.getOrder().equals(IBond.Order.SINGLE))
					results[names_index.NumSingleBondKekule.ordinal()] = -1;
				if (bond.getOrder().equals(IBond.Order.DOUBLE))
					results[names_index.NumDoubleBondKekule.ordinal()] = -1;

			}
			results[names_index.time_ms.ordinal()] = System.currentTimeMillis()
					- now;
			results[names_index.status.ordinal()] = x.getMessage();
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), new ArrayResult(results),
					getDescriptorNames());
		} finally {

		}
	}

	public IDescriptorResult getDescriptorResultType() {
		return new StringArrayResult();
	}

	public String[] getDescriptorNames() {
		return names;
	}

	public String[] getParameterNames() {
		return null;
	}

	public Object getParameterType(String arg0) {
		return null;
	}

	public Object[] getParameters() {
		return null;
	}

	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				String.format("http://www.opentox.org/algorithmTypes#%s",
						getTyperClass().toLowerCase()),
				this.getClass().getName(),
				String.format(
						"$Id: %s.java,v 1.0 2010/08/05 8:12:00 Nina Jeliazkova Exp $",
						getTyperClass()), "http:///ambit.sourceforge.net");
	}

	public void setParameters(Object[] arg0) throws CDKException {

	}

	public abstract String getTyperClass();

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}
}