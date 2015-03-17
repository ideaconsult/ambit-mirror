package ambit2.rest.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.resource.ResourceException;

import ambit2.base.json.JSONUtils;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.IEvaluation;
import ambit2.core.data.model.IEvaluation.EVStatsType;
import ambit2.core.data.model.IEvaluation.EVType;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.model.evaluation.EvaluationStats;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class ModelJSONReporter<Q extends IQueryRetrieval<ModelQueryResults>> extends ModelURIReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected String jsonpCallback = null;
	
	enum jsonModel {
		URI,
		title,
		algorithm,
		trainingDataset,
		independent,
		dependent,
		predicted,
		legend,
		creator,
		mimetype,
		content,
		evaluations,
		algFormat,
		parameters,
		stars;
		
		public String jsonname() {
			return name();
		}
	}
	public ModelJSONReporter(Request baseRef,String jsonpcallback) {
		super(baseRef);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpcallback);
	}
	
	protected void parseContent(ModelQueryResults model) {
		 ObjectInputStream ois = null;
		try {
			Form form = new Form(model.getContent());
			InputStream in = new ByteArrayInputStream(Base64.decode(form.getFirstValue("model")));
			ois =  new ObjectInputStream(in);
		 	Object o = ois.readObject();
		 	if (o==null)  throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,String.format("Error when reading model %s",model.getName()));
		 	
	 		List<IEvaluation<String>> e = new ArrayList<IEvaluation<String>>();
	 		for (EVType evt : EVType.values()) {
	 			String[] evals = form.getValuesArray(evt.name());
		 		for (int i=0; i < evals.length; i++) {
		 			EvaluationStats<String> stats = new EvaluationStats<String>(evt,evals[i]);
		 			for (EVStatsType evst : EVStatsType.values()) {
		 				Object value = form.getFirstValue(evt.name() + "_" + evst.name());
		 				if (value==null) continue;
		 				try {
		 					stats.getStats().put(evst,Double.parseDouble(value.toString()));
		 				} catch (Exception x) {}
		 			}
		 			e.add(stats);
		 		}
	 		}
	 		if (e.size()==0) model.setEvaluation(null);
	 		else model.setEvaluation(e);
	 		
		} catch (Exception x) {
			
		} finally {
			try { ois.close();} catch (Exception x) {}
		}
	}
	@Override
	public Object processItem(ModelQueryResults model) throws AmbitException {
		try {
			String uri = getURI(model);
			parseContent(model);
			AlgorithmFormat algFormat = AlgorithmFormat.JAVA_CLASS;
			for (AlgorithmFormat af : AlgorithmFormat.values()) 
				if (af.getMediaType().equals(model.getContentMediaType())) {
					algFormat = af;
					break;
				}
			
			if (comma!=null) getOutput().write(comma);
			StringBuilder evals = new StringBuilder();
			evals.append("{\n");
			if (model.getEvaluation()!=null)
			for (int i=0; i < model.getEvaluation().size(); i++) {
				IEvaluation ev = model.getEvaluation().get(i);
				if (i>0) evals.append(",");
				evals.append(String.format("\t\"%s\":\n\t\t{\"content\":\"%s\"",
							ev.getType().name(),
							JSONUtils.jsonEscape(ev.getContent().toString())));
				if (ev instanceof EvaluationStats)
					for (EVStatsType evst : EVStatsType.values()) {
						Object value = ((EvaluationStats)ev).getStats().get(evst);
						if (value==null) continue;
						evals.append(String.format(",\n\t\t\"%s\":%s",evst.name(),value));	
					}
				evals.append("\t\n}");
			}
			evals.append("\t}");
			
			StringBuilder p = new StringBuilder();
			if (model.getParameters()!=null) {
			    String d = "";
			    for (String param : model.getParameters()) {
				p.append(d);
				p.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(param)));
				d = ",";
			    }
			}    
			getOutput().write(String.format(
					"\n{"+
					"\n\"%s\":\"%s\"," + //uri
					"\n\"%s\":%d," + //id
					"\n\"%s\":\"%s\"," + //title
					"\n\"%s\":%d," + //stars
					"\n\"%s\":{\n\t\"URI\":\"%s\",\n\t\"%s\":\"%s\",\n\t\"img\":\"%s\"\n}," + 					//algorithm
					"\n\"%s\":\"%s\"," + //dataset
					"\n\"%s\":\"%s\"," + //vars
					"\n\"%s\":\"%s\"," + //vars
					"\n\"%s\":\"%s\"," + //vars
					"\n\"%s\":{" + 	
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":\"%s\"," +
					"\n\t\"%s\":%s," +
					"\n\t\"%s\":%s, " +
					"\n\t\"%s\":[%s] " +
					"\n\n}}",
					
					jsonModel.URI.jsonname(),uri,
					"id",model.getId(),
					jsonModel.title.jsonname(),JSONUtils.jsonEscape(model.getName()),
					jsonModel.stars.jsonname(),model.getStars(),
					jsonModel.algorithm.jsonname(),model.getAlgorithm()==null?"":JSONUtils.jsonEscape(model.getAlgorithm()),
					jsonModel.algFormat.jsonname(),JSONUtils.jsonEscape(algFormat.name()),
					String.format("/%s",model.getAlgorithm().indexOf("org.openscience.cdk")>=0?"images/cdk.png":
									model.getAlgorithm().indexOf("toxtree")>=0?"images/toxtree.png":
									model.getAlgorithm().indexOf("ambit2.descriptors")>=0?"images/ambit.png":
									algFormat.getImage()),
					
					jsonModel.trainingDataset.jsonname(),model.getTrainingInstances()==null?"":JSONUtils.jsonEscape(model.getTrainingInstances()),
					jsonModel.independent.jsonname(),String.format("%s/independent",uri),
					jsonModel.dependent.jsonname(),String.format("%s/dependent",uri),
					jsonModel.predicted.jsonname(),String.format("%s/predicted",uri),
					
					"ambitprop",
					jsonModel.legend.jsonname(),String.format("%s?media=image/png",uri),
					jsonModel.creator.jsonname(),JSONUtils.jsonEscape(model.getCreator()),
					jsonModel.mimetype.jsonname(),model.getContentMediaType(),
					jsonModel.content.jsonname(),"application/java".equals(model.getContentMediaType())
												?JSONUtils.jsonEscape(model.getContent())
												:(uri+"?media=text/plain"),
					jsonModel.evaluations.jsonname(),evals.toString(),
					jsonModel.stars.jsonname(),model.getStars(),
					jsonModel.parameters.jsonname(),p.toString()
					));
			comma = ",";

		} catch (Exception x) {
			x.printStackTrace();
		}
		return model;
	}
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
		try {
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}		

	};
	
	public void header(java.io.Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
		} catch (Exception x) {	}
		try {
			output.write("{\"model\": [");
		} catch (Exception x) {}
	};
	
}
