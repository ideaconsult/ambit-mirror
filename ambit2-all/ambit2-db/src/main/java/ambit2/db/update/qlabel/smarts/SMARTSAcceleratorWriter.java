package ambit2.db.update.qlabel.smarts;

import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.AbstractUpdateProcessor;

public class SMARTSAcceleratorWriter  extends AbstractUpdateProcessor<IStructureRecord, IStructureRecord> {
	protected CreateSMARTSData g = new CreateSMARTSData();
	/**
	 * 
	 */
	private static final long serialVersionUID = -5985642442961325777L;
	
	public SMARTSAcceleratorWriter() {
		setQueryCreate(new CreateSMARTSData());
	}

	@Override
	public IStructureRecord create(IStructureRecord record) throws SQLException,
			OperationNotSupportedException, AmbitException {
			
		try {
			getQueryCreate().setObject(record);
		
		} catch (Exception x) {
			getQueryCreate().setObject(null);
		}
		return super.create(record);
	}
}