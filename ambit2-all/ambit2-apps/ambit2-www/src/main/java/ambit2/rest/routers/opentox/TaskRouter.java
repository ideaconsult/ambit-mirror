package ambit2.rest.routers.opentox;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.task.TaskResource;

/**
 * OpenTox tasks /task
 * @return
 */
public class TaskRouter extends MyRouter {
	public TaskRouter(Context context) {
		super(context);
		attachDefault(TaskResource.class);
		attach(TaskResource.resourceID, TaskResource.class);
	}
}
