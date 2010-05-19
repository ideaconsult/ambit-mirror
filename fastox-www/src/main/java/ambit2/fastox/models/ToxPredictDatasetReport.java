package ambit2.fastox.models;

import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTDatasetScrollableReport;
import ambit2.rest.task.dsl.OTFeatures;
import ambit2.rest.task.dsl.OTModels;

public class ToxPredictDatasetReport extends OTDatasetScrollableReport {

	protected OTModels models;
	public OTModels getModels() {
		return models;
	}
	public void setModels(OTModels models) {
		this.models = models;
	}
	protected ToxPredictDatasetReport(OTDataset dataset,OTFeatures features, String application, int page, int pageSize) throws Exception {
		super(dataset,features, application,page,pageSize);

	}
	public static ToxPredictDatasetReport report(OTDataset dataset, OTFeatures features, String application, int page, int pageSize) throws Exception {
		return new ToxPredictDatasetReport(dataset,features,application,page,pageSize);
	}
	/*
	@Override
	public void writeData(int row, List<String> values, Writer writer)
			throws IOException {
		writer.write("<table class='tablesorter'>");
		writer.write("<thead>");
		writer.write("</thead>");
		writer.write("<tbody>");
		
		//OTModels models = session.getSelectedModels();
		if (models != null)
		for (OTModel model: models.getItems()) {
			writer.write(String.format("<tr><th colspan='2'>%s</th></tr>",model.toString()));
			OTFeatures features = model.getPredictedVariables();
			if (features==null) continue;
				try {
					for (OTFeature feature: features.getItems()) {
						int i = header.indexOf(feature.getUri().toString());
						String value = values.get(i);
						writer.write(String.format("<tr><td>%s</td><td>%s</td></tr>",header.get(i),value)); //test
					}
				} catch (Exception x) {
					//session.setError(key,x);
				}
		}		
		writer.write("</tbody>");
		writer.write("</table>");
	}
	*/
}
