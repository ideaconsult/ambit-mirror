package ambit2.rest.task;

import java.util.UUID;
import java.util.concurrent.Callable;

public interface ICallableTask extends Callable<TaskResult> {
	public UUID getUuid();
	public void setUuid(UUID uuid);
}
