package ambit2.vega;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.data.Property;
import ambit2.base.external.ShellException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.rest.model.predictor.ExternalModel;
import net.idea.modbcum.i.exceptions.AmbitException;

public class ExternalModelVega extends ExternalModel<VegaWrapper,IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6049936082079982901L;
	protected MoleculeReader reader = new MoleculeReader();
	public ExternalModelVega() throws ShellException {
		super();
		model = createVegaWrapper();
	}
	protected VegaWrapper createVegaWrapper() throws ShellException {
	    try {
            AMBITConfigProperties properties = new AMBITConfigProperties();
            String near_the_zenith = properties.getPropertyWithDefault(AMBITConfigProperties.ambitProperties,"VEGA","VEGA_REMOTE");
            if ("VEGA_REMOTE".equals(near_the_zenith))
                return new VegaRemote();
            else
                return new VegaShell();
	    } catch (Exception x) {
	        return new VegaRemote();
	    }
	    
	}
	@Override
	public List<Property> process(ExternalModel<VegaWrapper,IAtomContainer> target) throws AmbitException {
		return target.getModel().createProperties();
	}
	@Override
	public IAtomContainer predict(IStructureRecord record) throws Exception {
		IAtomContainer a = reader.process(record);
		return model.process(a);
	}

}
