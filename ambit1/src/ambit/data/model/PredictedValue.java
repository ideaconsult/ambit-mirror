package ambit.data.model;

import ambit.processors.QSARPointsProcessor;
import ambit.ui.actions.dbadmin.DbBatchImportQSARModels;

/**
 * This is mostly to pass prediction value from arbitrary input to {@link DbBatchImportQSARModels}
 * Up to now created by {@link QSARPointsProcessor}
 * @author Nina Jeliazkova
 *
 */
public class PredictedValue implements IPredictedValue {
	protected Object result;
	protected Model  predictionModel;
	public PredictedValue() {
		this(null,null);
	}
	public PredictedValue(Object value,Model model) {
		super();
		this.result = value;
		this.predictionModel = model;
	}	
	public Model getModel() {
		return predictionModel;
	}
	public Object getResult() {
		return result;
	}
	public void setModel(Model model) {
		if (model instanceof Model)
			predictionModel = (Model)model;
		
	}
	public void setResult(Object value) {
		result = value;
		
	}
	public boolean equals(Object arg0) {
		return predictionModel.equals(((IPredictedValue)arg0).getModel()) &&
			result.equals(((IPredictedValue) arg0).getResult());
	}
	public String toString() {
		if (result == null) return "";
		else return result.toString();
	}
	
}
