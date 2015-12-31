package ambit2.rest.model.predictor;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class ExpertModelpredictor<Predictor> extends ModelPredictor<Predictor, IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5270628252984937908L;
	protected Object value;
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public ExpertModelpredictor(
			Reference applicationRootReference,
			ModelQueryResults model, 
			ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, 
			String[] targetURI
			)
			throws ResourceException {
		super(applicationRootReference, model, modelReporter, propertyReporter,
				targetURI);
	}
	
	@Override
	public Predictor createPredictor(ModelQueryResults model)
			throws ResourceException {

		return null;
	}

	@Override
	protected void extractRecordID(IStructureRecord target, String url,
			IStructureRecord record) throws AmbitException {
		record.setIdchemical(target.getIdchemical());
		record.setIdstructure(target.getIdstructure());
	}
	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		return null;
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		return value;
		/*
		if (value != null) {
			Iterator<Property> p = model.getPredicted().getProperties(true);
			while (p.hasNext()) {
				target.setProperty(p.next(),value);
			}
		}
		return target;
		*/
	}


}
