package ambit2.vega;

import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Property;
import ambit2.base.external.ShellException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.rest.model.predictor.ExternalModel;
import net.idea.modbcum.i.exceptions.AmbitException;

public class RemoteModelVega  extends ExternalModel<VegaRemote,IAtomContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6512349994622326539L;
	protected MoleculeReader reader = new MoleculeReader();
	
	public RemoteModelVega() throws ShellException {
		super();
		model = new VegaRemote();
	}
	@Override
	public List<Property> process(ExternalModel<VegaRemote,IAtomContainer> target) throws AmbitException {
		return target.getModel().createProperties();
	}
	@Override
	public IAtomContainer predict(IStructureRecord record) throws Exception {
		IAtomContainer a = reader.process(record);
		return model.process(a);
	}


}
