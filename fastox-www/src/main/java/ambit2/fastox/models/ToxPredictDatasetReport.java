package ambit2.fastox.models;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTDatasetScrollableReport;
import ambit2.rest.task.dsl.OTDatasetTableReport;
import ambit2.rest.task.dsl.OTFeatures;
import ambit2.rest.task.dsl.OTModel;
import ambit2.rest.task.dsl.OTModels;

import com.hp.hpl.jena.ontology.OntModel;

public class ToxPredictDatasetReport extends OTDatasetScrollableReport {
	protected OTModels models;
	protected OntModel jenaModel;

	protected ToxPredictDatasetReport(OTDataset dataset,OTModels models, String application, int page, int pageSize) throws Exception {
		super(dataset,null, application,page,pageSize);
		this.models = models;
	}
	public static ToxPredictDatasetReport report(OTDataset dataset, OTModels models, String application, int page, int pageSize) throws Exception {
		return new ToxPredictDatasetReport(dataset,models,application,page,pageSize);
	}
	@Override
	public void writeData(int row, List<String> values, Writer writer)
			throws IOException {
		writer.write("<table class='tablesorter'>");
		writer.write("<thead>");
		writer.write("</thead>");
		writer.write("<tbody>");
		if (models != null)
		for (OTModel model: models.getItems()) {
			writer.write(String.format("<tr><th colspan='2'>%s</th></tr>",model.toString()));
			OTFeatures features = model.getPredictedVariables();
			if (features==null) continue;
				try {
					OTDataset dataset = OTDataset.dataset(values.get(0)).getFeatures(features);
					OTDatasetTableReport report = OTDatasetTableReport.report(dataset, null, application, 0,1000);
					report.write(writer);
				} catch (Exception x) {
					//session.setError(key,x);
				}
		}		
		writer.write("</tbody>");
		writer.write("</table>");
	}
}
