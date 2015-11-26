package ambit2.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.config.Preferences;
import ambit2.core.data.StringArrayResult;
import ambit2.core.processors.structure.AtomConfigurator;

public class AtomTypeVerifierDescriptor implements IMolecularDescriptor {
	protected AtomConfigurator typer = new AtomConfigurator();
	protected static final String OK = "OK";

	public DescriptorValue calculate(IAtomContainer mol) {

		String pref = Preferences
				.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES);
		Preferences.setProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES, "true");
		try {
			typer.process(mol);
			StringArrayResult value = new StringArrayResult(new String[] { OK });

			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), value, getDescriptorNames());
		} catch (Exception x) {
			StringArrayResult value = new StringArrayResult(
					new String[] { x.getMessage() });

			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), value, getDescriptorNames());
		} finally {
			Preferences.setProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES,
					pref == null ? "false" : pref);
		}
	}

	public IDescriptorResult getDescriptorResultType() {
		return new StringArrayResult();
	}

	public String[] getDescriptorNames() {
		return new String[] { "AtomTypeERRORS" };
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
						"AtomTypeVerifier"),
				this.getClass().getName(),
				"$Id: AtomTypeVerifierDescriptor.java,v 1.0 2010/08/204 13:00:00 Nina Jeliazkova Exp $",
				"http:///ambit.sourceforge.net");
	}

	public void setParameters(Object[] arg0) throws CDKException {

	}

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}

}
