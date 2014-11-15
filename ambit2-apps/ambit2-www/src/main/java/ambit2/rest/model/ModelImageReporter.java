package ambit2.rest.model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.task.ClientResourceWrapper;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.core.data.IStructureDiagramHighlights;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.core.io.MyIteratingMDLReader;
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
	protected ModelURIReporter<IQueryRetrieval<ModelQueryResults>> modelURIReporter;
	
	public ModelImageReporter(Request request, Form form,Dimension d,ResourceDoc doc) throws ResourceException {
		super();
		this.request = request;
		this.d = d;
		dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		//if (dataset==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		
		param = form.getFirstValue(OpenTox.params.parameters.toString());
		modelURIReporter = new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(request,doc);
	
	}
	@Override
	public void footer(BufferedImage output, Q query) {
		
	}

	@Override
	public void header(BufferedImage output, Q query) {
		
	}

	@Override
	public Object processItem(ModelQueryResults model) throws Exception {
		ModelPredictor predictor = ModelPredictor.getPredictor(model,request);
		String resultsURI = String.format("%s/%s",modelURIReporter.getURI(model),OpenTox.URI.feature);
		setOutput(getImage(predictor,resultsURI));
		return model;
	}

	public void open() throws DbAmbitException {
		
	}	
	
	
	protected BufferedImage getImage(ModelPredictor predictor,String resultsURI) throws ResourceException {
		if (dataset==null) { //legend only
			if (predictor instanceof IStructureDiagramHighlights) try {
				return ((IStructureDiagramHighlights)predictor).getLegend(d.width,d.height);
			} catch (AmbitException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			}
			else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		}
		Reference firstmol = new Reference(dataset);
		firstmol.addQueryParameter(OpenTox.params.feature_uris.toString(),resultsURI);
		firstmol.addQueryParameter("max", "1");
		ClientResourceWrapper client = new ClientResourceWrapper(firstmol);
		Representation r = null;
		MyIteratingMDLReader reader = null;
		try {
			r = client.get(ChemicalMediaType.CHEMICAL_MDLSDF);
			reader = new MyIteratingMDLReader(r.getStream(),SilentChemObjectBuilder.getInstance());
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
