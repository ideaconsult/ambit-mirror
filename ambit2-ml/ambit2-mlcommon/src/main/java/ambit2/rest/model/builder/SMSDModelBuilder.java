package ambit2.rest.model.builder;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.PredictedVarsTemplate;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;


public class SMSDModelBuilder extends AbstractStructuresModelBuilder<ModelQueryResults> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3199396535541771758L;
	public SMSDModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter) {
		this(applicationRootReference,model_reporter,alg_reporter,null,null);
	}
	
	public SMSDModelBuilder(Reference applicationRootReference,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> model_reporter,
			AlgorithmURIReporter alg_reporter,
			String[] targetURI,
			String[] parameters) {
		super(applicationRootReference,model_reporter,alg_reporter,targetURI,parameters);
	}

	@Override
	public void transform(IStructureRecord[] records) {
	
		for (IStructureRecord record:records) {
			for (Property key : record.getRecordProperties()) {
				if (getTrainingData().getPredicted()==null) 
					getTrainingData().setPredicted(
							new PredictedVarsTemplate(String.format("%s#predicted", getTrainingData().getName()))
							);
				getTrainingData().getPredicted().add(key);
			}
		}
		
	}

	@Override
	public ModelQueryResults process(Algorithm algorithm) throws AmbitException {
		if (getTrainingData()==null) throw new AmbitException("No MCSS structures found!");
		
		if (getTrainingData()==null) {
			String name = String.format("%s.%s",UUID.randomUUID().toString(),algorithm.getName());
			ModelQueryResults m = new ModelQueryResults();
			m.setParameters(parameters);
			m.setId(null);
			m.setContentMediaType(AlgorithmFormat.WWW_FORM.getMediaType());
			m.setName(name);
			m.setAlgorithm(alg_reporter.getURI(algorithm));
			setTrainingData(m);
			Template empty = new Template("Empty");
			m.setPredictors(empty);
			m.setDependent(empty);
			PredictedVarsTemplate emptyp = new PredictedVarsTemplate("Empty");
			m.setPredicted(emptyp);
			setTrainingData(m);
		}
		
		AlgorithmURIReporter r = new AlgorithmURIReporter();

		Form form = new Form();
		try {
			form.add("algorithm","smsd");
			if (targetURI!=null)
				form.add("target",targetURI[0]);
			Iterator<Property> p = getTrainingData().getPredicted().iterator();
			while (p.hasNext())
				form.add("feature",getTrainingData().getPredicted().get(p.next()).toString());
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
		try {
			serializeModel( form, getTrainingData());
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		return getTrainingData();
	}
	
	protected void serializeModel(Form form, ModelQueryResults m) throws IOException {
		m.setContent(form.getWebRepresentation().getText());
	}
}
