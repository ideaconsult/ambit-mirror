package ambit2.mopac;

import org.openscience.cdk.qsar.DescriptorSpecification;

import ambit2.base.data.Property;
import ambit2.base.external.ShellException;

/**
 * Same as {@link DescriptorMopacShell}, but with default options to use the original structure and skip optimisation.
 * @author nina
 *
 */
public class MopacOriginalStructure extends DescriptorMopacShell {

	public MopacOriginalStructure() throws ShellException {
		super();
		mopac_shell.setOptimize(false);
		mopac_shell.setUseOriginalStructure(true);
	}
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            //"http://qsar.sourceforge.net/dicts/qsar-descriptors:MOPAC7.1",
        	String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"MOPAC"),
            this.getClass().getName(),
            "$Id: MopacOriginalStructure.java,v 0.2 2009/11/04 9:51:00 nina@acad.bg$",
            "http://openmopac.net/Downloads/MOPAC_7.1executable.zip");
    };
}
