package ambit2.rest.dataset;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;

public class DatasetHTMLReporter extends QueryReporter<SourceDataset, IQueryRetrieval<SourceDataset>, Writer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	@Override
	protected void processItem(SourceDataset item, Writer output) {
		try {
		output.write("<a href='");
		output.write("/dataset/");
		output.write(item.getName());
		output.write("' >");
		output.write(item.getName());
		output.write("</a>");
		} catch (Exception x) {
			
		}
	}
	@Override
	public void close() throws SQLException {
		try {
			getOutput().write("</body></html>");
			getOutput().flush();
		} catch (Exception x) {
			
		}
		super.close();
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Writer getOutput() throws AmbitException {
		//StringWriter writer = new StringWriter();
		Writer w = super.getOutput();
		try {
		w.write("<html><head><title>Datasets</title></head><body>");
		} catch (IOException x) {}
		return w;
	}

	
}
