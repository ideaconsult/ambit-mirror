package ambit2.rest.structure.quality;

import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import ambit2.base.data.QLabel;
import ambit2.db.search.qlabel.QueryQLabel;

public class QLabelReporter extends QueryReporter<QLabel, QueryQLabel, Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6162234583079229074L;

	@Override
	public void footer(Writer output, QueryQLabel query) {
	}

	@Override
	public void header(Writer output, QueryQLabel query) {
	}

	@Override
	public Object processItem(QLabel item) throws AmbitException {
		try { getOutput().write(String.format("%s: %s\n",
				"comparison".equals(item.getUser().getName())?"comparison":"expert",item.getLabel())); 
		} catch (Exception x) {}
		return item;
	}

	public void open() throws DbAmbitException {
		
	}

}
