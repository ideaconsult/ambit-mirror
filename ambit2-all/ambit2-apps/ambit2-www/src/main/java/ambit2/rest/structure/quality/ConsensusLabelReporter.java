package ambit2.rest.structure.quality;

import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import ambit2.db.search.qlabel.QueryConsensus;

public class ConsensusLabelReporter extends QueryReporter<String, QueryConsensus, Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4756603556533645998L;

	@Override
	public void footer(Writer output, QueryConsensus query) {
		
		
	}

	@Override
	public void header(Writer output, QueryConsensus query) {
		
	}

	@Override
	public Object processItem(String item) throws AmbitException {
		try { getOutput().write(item); } catch (Exception x) {};
		return item;
	}

	public void open() throws DbAmbitException {
	
	}

}
