package ambit2.db.update.chemical;

import java.sql.SQLException;

import javax.naming.OperationNotSupportedException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorException;
import ambit2.db.processors.AbstractUpdateProcessor;

public class InChIChemicalsWriter  extends AbstractUpdateProcessor<IStructureRecord,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5985642442961325777L;
	
	public InChIChemicalsWriter() {
		super();
		setQueryUpdate(new UpdateChemical<IStructureRecord>());
		setOperation(OP.UPDATE);
	}

	@Override
	public IStructureRecord process(IStructureRecord target)
			throws AmbitException {
		try {
			getQueryUpdate().setObject(target);
			//if (target.getInchi()==null && (target.getInchiKey()==null)) return target;
			return update(target);
        } catch (OperationNotSupportedException x) {
            throw new ProcessorException(this,x);
        } catch (SQLException x) {
            throw new ProcessorException(this,x);
        }
	}


}