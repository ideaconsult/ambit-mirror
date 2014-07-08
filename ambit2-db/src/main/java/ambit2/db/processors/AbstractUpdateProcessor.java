package ambit2.db.processors;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;

/**
 * Generic processor to execute IQueryUpdate<Group,Target>
 * @author nina
 *
 * @param <Group>
 * @param <Target>
 */
public class AbstractUpdateProcessor<Group,Target> extends AbstractRepositoryWriter<Group,Target>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3168837881397247324L;
	protected IQueryUpdate<Group,Target> queryCreate;
	protected IQueryUpdate<Group,Target> queryDelete;
	protected IQueryUpdate<Group,Target> queryUpdate;
	
	public AbstractUpdateProcessor() {
		
	}
	public AbstractUpdateProcessor(OP operation,IQueryUpdate<Group,Target> query) {
		super();
		setOperation(operation);
		switch (getOperation()) {
		case CREATE: { setQueryCreate(query); break; }
		case DELETE: { setQueryDelete(query); break; }
		case UPDATE: { setQueryUpdate(query); break; }
		default: ;
		}
	}
	public IQueryUpdate<Group, Target> getQueryCreate() {
		return queryCreate;
	}

	public void setQueryCreate(IQueryUpdate<Group, Target> queryCreate) {
		this.queryCreate = queryCreate;
	}

	public IQueryUpdate<Group, Target> getQueryDelete() {
		return queryDelete;
	}

	public void setQueryDelete(IQueryUpdate<Group, Target> queryDelete) {
		this.queryDelete = queryDelete;
	}

	public IQueryUpdate<Group, Target> getQueryUpdate() {
		return queryUpdate;
	}

	public void setQueryUpdate(IQueryUpdate<Group, Target> queryUpdate) {
		this.queryUpdate = queryUpdate;
	}

	public Target getTarget() {
		switch (getOperation()) {
		case CREATE: return (getQueryCreate()==null?null:getQueryCreate().getObject());
		case DELETE: return (getQueryDelete()==null?null:getQueryDelete().getObject());
		case UPDATE: return (getQueryUpdate()==null?null:getQueryUpdate().getObject());
		default: return null;
		}
	}

	public void setTarget(Target target) {
		
		if (getQueryCreate() != null) getQueryCreate().setObject(target);
		if (getQueryUpdate() != null) getQueryUpdate().setObject(target);
		if (getQueryDelete() != null) getQueryDelete().setObject(target);
	}

	protected Target execute(Group group, IQueryUpdate<Group, Target> query) 
	throws java.sql.SQLException ,javax.naming.OperationNotSupportedException ,	AmbitException {
		if (query == null) throw new OperationNotSupportedException();
		query.setGroup(group);
		query.setObject(getTarget());
		exec.process(query);
		return query.getObject();
	};	
	public Target create(Group group) 
		throws java.sql.SQLException ,javax.naming.OperationNotSupportedException ,	AmbitException {
		return execute(group, queryCreate);
	};
	public Target delete(Group group) 
	throws java.sql.SQLException ,javax.naming.OperationNotSupportedException ,AmbitException {
		return execute(group, queryDelete);
	};
	public Target update(Group group) 
	throws java.sql.SQLException ,javax.naming.OperationNotSupportedException ,AmbitException {
	return execute(group, queryUpdate);	
	};
}
