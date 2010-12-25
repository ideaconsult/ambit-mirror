package ambit2.rest.task;

import java.util.UUID;
import java.util.concurrent.Callable;

import org.restlet.data.Reference;

public interface ICallableTask extends Callable<Reference> {
	public UUID getUuid();
	public void setUuid(UUID uuid);
}
