package ambit2.rest.model.predictor;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.IObject2Properties;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;
import net.idea.modbcum.i.exceptions.AmbitException;

public class ExternalModelPredictor<C extends ExternalModel> extends ModelPredictor<C, IStructureRecord> {
	protected List<Property> predictedProperties;

	public ExternalModelPredictor(Reference applicationRootReference, ModelQueryResults model,
			ModelURIReporter modelReporter, PropertyURIReporter propertyReporter, String[] targetURI)
			throws ResourceException {
		super(applicationRootReference, model, modelReporter, propertyReporter, targetURI);
		structureRequired = true;

		if (getPredictor() instanceof IObject2Properties)
			try {
				predictedProperties = ((IObject2Properties) getPredictor()).process(getPredictor());
			} catch (Exception x) {
				logger.log(Level.WARNING,x.getMessage());
			}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9183148308530951202L;

	@Override
	protected boolean isSupported(Object predictor) throws ResourceException {
		return true;

	}

	@Override
	protected void extractRecordID(IStructureRecord target, String url, IStructureRecord record) throws AmbitException {
		record.setIdchemical(target.getIdchemical());
		record.setIdstructure(target.getIdstructure());
	}

	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		return null;
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		try {
			return getPredictor().predict(target);
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public void assignResults(IStructureRecord record, Object value) throws AmbitException {
		IAtomContainer result = (IAtomContainer) value;
		int count = 0;
		if (predictedProperties != null)
			try {
				for (Property p : predictedProperties) {
					record.setRecordProperty(p, result.getProperty(p));
					count++;
				}

			} catch (Exception x) {

			}
		else {
			Iterator<Property> predicted = model.getPredicted().getProperties(true);
			while (predicted.hasNext()) {
				Property p = predicted.next();
				record.setRecordProperty(p, result.getProperty(p));
				count++;
			}
		}
		if (count == 0)
			throw new AmbitException("No property to assign results!!!");
	}

	@Override
	public IStructureRecord process(IStructureRecord target) throws AmbitException {
		record.clear();
		extractRecordID(target, getCompoundURL(target), record);
		try {
			Object value = predict(target);
			assignResults(record, value);
		} catch (Exception x) {
			try {
				logger.log(Level.WARNING,
						String.format("%s\t%s\t%s", x.getMessage(), target.toString(), model.toString()));
				assignResults(record, x.getMessage());
			} catch (Exception xx) {
			}
		}
		return record;
	}
}
