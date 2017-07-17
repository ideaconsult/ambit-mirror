package ambit2.rest.model.predictor;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.IObject2Properties;

public abstract class ExternalModel<M,OUTPUT> implements IObject2Properties<ExternalModel<M,OUTPUT>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9072575012057496754L;
	protected M model;

	public M getModel() {
		return model;
	}

	public void setModel(M model) {
		this.model = model;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public long getID() {
		return 0;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void open() throws Exception {
	}

	@Override
	public void setEnabled(boolean arg0) {
	}

	public abstract OUTPUT predict(IStructureRecord record) throws Exception;
		
}
