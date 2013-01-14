package ambit2.rest.model;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;

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
		algFormat,
		stars;
		
		public String jsonname() {
			return name();
		}
	}
	public ModelJSONReporter(Request baseRef) {
		super(baseRef);
	}
	@Override
	public Object processItem(ModelQueryResults model) throws AmbitException {
		try {
			String uri = getURI(model);
			AlgorithmFormat algFormat = AlgorithmFormat.JAVA_CLASS;
			for (AlgorithmFormat af : AlgorithmFormat.values()) 
				if (af.getMediaType().equals(model.getContentMediaType())) {
					algFormat = af;
					break;
				}
			
			if (comma!=null) getOutput().write(comma);
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
					"\n\t\"%s\":%d " +
					"\n\n}}",
					
					jsonModel.URI.jsonname(),uri,
					"id",model.getId(),
					jsonModel.title.jsonname(),model.getName(),
					jsonModel.stars.jsonname(),model.getStars(),
					jsonModel.algorithm.jsonname(),model.getAlgorithm()==null?"":model.getAlgorithm(),
					jsonModel.algFormat.jsonname(),algFormat.name(),
					String.format("/%s",model.getAlgorithm().indexOf("org.openscience.cdk")>=0?"images/cdk.png":
									model.getAlgorithm().indexOf("toxtree")>=0?"images/toxtree.png":
									algFormat.getImage()),
					
					jsonModel.trainingDataset.jsonname(),model.getTrainingInstances()==null?"":model.getTrainingInstances(),
					jsonModel.independent.jsonname(),String.format("%s/independent",uri),
					jsonModel.dependent.jsonname(),String.format("%s/dependent",uri),
					jsonModel.predicted.jsonname(),String.format("%s/predicted",uri),
					
					"ambitprop",
					jsonModel.legend.jsonname(),String.format("%s?media=image/png",uri),
					jsonModel.creator.jsonname(),model.getCreator(),
					jsonModel.mimetype.jsonname(),model.getContentMediaType(),
					jsonModel.content.jsonname(),"application/java".equals(model.getContentMediaType())?model.getContent():"",
					jsonModel.stars.jsonname(),model.getStars()
					));
			comma = ",";

		} catch (Exception x) {
			
		}
		return model;
	}
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	};
	
	public void header(java.io.Writer output, Q query) {
		try {
			output.write("{\"model\": [");
		} catch (Exception x) {}
	};
	
}
