package ambit2.rest.model.predictor;

import java.awt.image.BufferedImage;
import java.sql.Connection;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.AbstractDBProcessor;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.IStructureDiagramHighlights;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.processors.AbstractDescriptorCalculator;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.property.PropertyURIReporter;

public class DescriptorPredictor<C extends AbstractDBProcessor<IStructureRecord,IStructureRecord>>  extends	ModelPredictor<C,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8106073411946010587L;
	protected String mopac_commands;
	public String getMopac_commands() {
		return mopac_commands;
	}

	public void setMopac_commands(String mopac_commands) {
		this.mopac_commands = mopac_commands;
	}
	
	protected C calculator;
	public DescriptorPredictor(
			Reference applicationRootReference,
			ModelQueryResults model, 
			ModelURIReporter modelReporter,
			PropertyURIReporter propertyReporter, 
			String[] targetURI) throws ResourceException {
		super(applicationRootReference,model,modelReporter,propertyReporter,targetURI);
		structureRequired = true;
		valuesRequired = model.getPredictors().size()==0;
	}
	
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		calculator.setConnection(connection);
	}
	
	@Override
	public C createPredictor(ModelQueryResults model)
			throws ResourceException {
		if (model.getContentMediaType().equals(AlgorithmFormat.JAVA_CLASS.getMediaType()))
			try {
				Profile<Property> p = new Profile<Property>();
				Property property = DescriptorsFactory.createDescriptor2Property(model.getContent(),model.getParameters());
				property.setEnabled(true);
				p.add(property);
				
				AbstractDescriptorCalculator c = new DescriptorsCalculator();
				c.setDescriptors(p);
				calculator = (C) c;
				return calculator;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
		else throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE,model.getContentMediaType());
	}
	
	public AbstractDescriptorCalculator createCalculator() {
		return new DescriptorsCalculator();
	}
	@Override
	public String getCompoundURL(IStructureRecord target) throws AmbitException {
		return null;
	}

	@Override
	public Object predict(IStructureRecord target) throws AmbitException {
		try {
			return calculator.process(target);
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x); 
		}
	}
	@Override
	public String toString() {

		StringBuilder b = new StringBuilder();
		b.append(String.format("Structures required\t%s\n",structureRequired?"YES":"NO"));

		if (predictor != null) {
			b.append(predictor.toString());
		}
		return b.toString();
				
	}	
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		try {
			return ((IStructureDiagramHighlights)calculator).getLegend(width, height);
		} catch (Exception x) {
			String[] msg = model.getName().split(" ");
			return writeMessages(msg, width, height);
		}
	}
	
}
