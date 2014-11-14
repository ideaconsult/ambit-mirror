package ambit2.db.search.structure.pairwise;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.NumberCondition;

/**
 * returns idstructure of two molecules
 * @author nina
 *
 * @param <Dataset1>
 * @param <Dataset2>
 */
public abstract class QueryStructurePairs<Dataset1, Dataset2> extends 
				AbstractQuery<Dataset1, Dataset2,NumberCondition, IStructureRecord[]> 
				implements IQueryRetrieval<IStructureRecord[]>{
	protected IStructureRecord[] records = new IStructureRecord[] {
				new StructureRecord(),
				new StructureRecord()	
				};
	/**
	 * 
	 */
	private static final long serialVersionUID = 1306709480084311651L;

	@Override
	public double calculateMetric(IStructureRecord[] object) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public IStructureRecord[] getObject(ResultSet rs) throws AmbitException {
		try {
			int idstructure1 = rs.getInt(1);
			if (idstructure1 != records[0].getIdstructure()) {
				records[0].clear();
				records[0].setIdstructure(idstructure1);
			}
			int idstructure2 = rs.getInt(2);
			if (idstructure2 != records[1].getIdstructure()) {
				records[1].clear();
				records[1].setIdstructure(idstructure2);
			}
			return records;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}


}
