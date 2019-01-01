package ambit2.rest.task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * creates tasks by iterating over query results
 * @author nina
 *
 */
public class TaskCreator<USERID,T> extends QueryReporter<T,IQueryRetrieval<T>, List<UUID>> {
	protected boolean async;
	protected Form form;
	protected List<UUID> tasks;
	
	public TaskCreator(Form form, boolean async) throws AmbitException {
		super();
		tasks = new ArrayList<UUID>();
		setAsync(async);
		setForm(form);
	}
	@Override
	public void setOutput(List<UUID> output)
			throws AmbitException {
	}
	@Override
	public List<UUID> getOutput() throws AmbitException {
		return tasks;
	}
	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8074204952293683628L;

	
	@Override
	public Object processItem(T item) throws AmbitException {
		try {
			ICallableTask callable = getCallable(form,item);
			if (async)	{
				ITask<Reference,USERID> task = createTask(callable,item);
				tasks.add(task.getUuid());
			}
			else {
				ITaskResult ref = callable.call();
			}
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);

		}
		return item;
	}

	public void open() throws DbAmbitException {
	}
	
	protected ICallableTask getCallable(Form form,T item) throws ResourceException  {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	protected ITask<Reference,USERID> createTask(ICallableTask callable, T item) throws ResourceException  {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	@Override
	public void footer(List<UUID> output, IQueryRetrieval<T> query) {
		
	}
	@Override
	public void header(List<UUID> output, IQueryRetrieval<T> query) {
		
	}
}
