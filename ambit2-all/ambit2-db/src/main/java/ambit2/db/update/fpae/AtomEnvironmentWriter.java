package ambit2.db.update.fpae;

import java.sql.SQLException;
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.IQueryUpdate;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.AbstractUpdateProcessor;

public class AtomEnvironmentWriter<S> extends AbstractUpdateProcessor<IStructureRecord, S> {
	protected CreateAtomEnvironment g = new CreateAtomEnvironment();
	/**
	 * 
	 */
	private static final long serialVersionUID = -5985642442961325777L;
	
	public AtomEnvironmentWriter() {
		setQueryCreate((IQueryUpdate)new CreateAtomEnvironment());
	}

	@Override
	public S create(IStructureRecord record) throws SQLException,
			OperationNotSupportedException, AmbitException {
			
		try {
			getQueryCreate().setGroup(record);
		
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
			getQueryCreate().setObject(null);
		}
		return super.create(record);
	}

}
