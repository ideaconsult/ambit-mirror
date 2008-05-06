package ambit2.data.model;


public interface IPredictedValue {
	Object getResult();
	void setResult(Object value);
	Model getModel();
	void setModel(Model model);
}
