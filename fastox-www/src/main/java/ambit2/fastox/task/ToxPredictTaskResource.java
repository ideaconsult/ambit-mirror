package ambit2.fastox.task;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Reference;

import ambit2.fastox.FastoxApplication;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.users.IToxPredictUser;
import ambit2.rest.task.Task;

public class ToxPredictTaskResource extends FastoxStepResource {

	public ToxPredictTaskResource(int stepIndex) {
		super(1);
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Iterator<Task<Reference,IToxPredictUser>> tasks = 
			((FastoxApplication) getApplication()).getTasks();
		while (tasks.hasNext()) {
			writer.write(tasks.next().toString());
			writer.write("<br>");
		}
	}

}
