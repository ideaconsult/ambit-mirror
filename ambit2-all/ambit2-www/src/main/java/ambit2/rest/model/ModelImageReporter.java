package ambit2.rest.model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OpenTox;
import ambit2.rest.model.predictor.ModelPredictor;

public class ModelImageReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends QueryReporter<ModelQueryResults, Q,BufferedImage > {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6892183497775648934L;
	protected Request request;
	protected String dataset;
	protected String param;
	protected Dimension d;
	
	public ModelImageReporter(Request request, Form form,Dimension d) throws ResourceException {
		super();
		this.request = request;
		this.d = d;
		dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (dataset==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		
		param = form.getFirstValue(OpenTox.params.parameters.toString());
	
	}
	@Override
	public void footer(BufferedImage output, Q query) {
		
	}

	@Override
	public void header(BufferedImage output, Q query) {
		
	}

	@Override
	public Object processItem(ModelQueryResults model) throws AmbitException {
		ModelPredictor predictor = ModelPredictor.getPredictor(model,request);
		setOutput(getImage(predictor));
		return model;
	}

	public void open() throws DbAmbitException {
		
	}	
	
	
	protected BufferedImage getImage(ModelPredictor predictor) throws ResourceException {
		Reference firstmol = new Reference(dataset);
		firstmol.addQueryParameter("max", "1");
		ClientResource client = new ClientResource(firstmol);
		Representation r = null;
		MyIteratingMDLReader reader = null;
		try {
			r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF);
			reader = new MyIteratingMDLReader(r.getStream(),DefaultChemObjectBuilder.getInstance());
			while (reader.hasNext()) {
				Object o = reader.next();
				if (o instanceof IAtomContainer)
					return predictor.getImage((IAtomContainer)o, param, d.width,d.height,false);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(x);
		} finally {
			try {reader.close(); } catch (Exception x) {}
			try {r.release(); } catch (Exception x) {}
			try {client.release(); } catch (Exception x) {}
		}
		return null;
	}
	

}
