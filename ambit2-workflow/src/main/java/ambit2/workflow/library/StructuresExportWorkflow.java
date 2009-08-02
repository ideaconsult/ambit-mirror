/* StructuresExportWorkflow.java
 * Author: nina
 * Date: May 3, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.workflow.library;

import java.io.FileWriter;
import java.io.Writer;
import java.sql.SQLException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.Reporter;
import ambit2.core.io.FileOutputState;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.SDFReporter;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.workflow.DBProcessorPerformer;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;

public class StructuresExportWorkflow extends ExportWorkflow<IQueryObject<IStructureRecord>> {

	public StructuresExportWorkflow()  {

        Sequence seq=new Sequence();
        seq.setName("[Export PBT assessment results]");    	

    	Primitive<IQueryObject<IStructureRecord>,FileOutputState> export =
    		new Primitive<IQueryObject<IStructureRecord>,FileOutputState>( 
    			getContentTag(),
    			"FILE",
    			new DBExporter(getProcessor(),getOutputTag()));
    	export.setName("Export as PDF/RTF/HTML/SDF file");
    	
    	FileOutputState fo = new FileOutputState();
    	fo.setSupportedExtensions(new String[] {".pdf",".rtf",".html",".sdf"});
    	fo.setSupportedExtDescriptions(new String[] {"Adobe PDF files (*.pdf)","Rich Text Format files (*.rtf)","HTML files (*.html)","SDF (*.sdf)"});
        setDefinition(new OutputFileSelection(export, fo));
        
	}
	@Override
	protected String getContentTag() {
		return DBWorkflowContext.STOREDQUERY;
	}
	@Override
	protected MyExporter getProcessor() {
		return new MyExporter();
	}
	@Override
	protected String getOutputTag() {
		return OutputFileSelection.OUTPUTFILE;
	}
}



class MyExporter extends AbstractDBProcessor<IQueryObject<IStructureRecord>, FileOutputState> implements Reporter <IQueryObject<IStructureRecord>, FileOutputState> {
	 protected FileOutputState output;
	 protected SDFReporter<IQueryRetrieval<IStructureRecord>> reporter;
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 7588709827950264505L;

	public MyExporter() {
		setReporter(new SDFReporter<IQueryRetrieval<IStructureRecord>>());
	}
	
	public FileOutputState process(IQueryObject<IStructureRecord> query)
			throws AmbitException {
		Writer writer = null;
    	try {
    		IQueryRetrieval<IStructureRecord>  results ;
    		if (query instanceof IStoredQuery) {
    			results = new QueryStoredResults((IStoredQuery)query);
    		} else if (query instanceof AbstractStructureQuery ) {
    			results = (AbstractStructureQuery) query;
    		} else throw new AmbitException(query.getClass().getName());
    		writer = new FileWriter(getOutput().getFile());
    		getReporter().setConnection(getConnection());
    		getReporter().setOutput(writer);
    		getReporter().process(results);
    	} catch (AmbitException x) {
    		throw x;
    	} catch (Exception x) {
			throw new AmbitException(x);
    	} finally {
    		try {if (writer !=null) writer.flush(); } catch (Exception x) { x.printStackTrace();}
    		try {if (reporter !=null) reporter.close(); } catch (Exception x) { x.printStackTrace();}    		
    	}		
    	return getOutput();
	}
	@Override
	public void close() throws SQLException {
		super.close();
		getReporter().close();
	}
	public void open() throws DbAmbitException {
		
	}
	public FileOutputState getOutput() throws AmbitException {
		return output;
	}

	public void setOutput(FileOutputState output) throws AmbitException {
		this.output = output;
		
	}
	public SDFReporter<IQueryRetrieval<IStructureRecord>> getReporter() {
		return reporter;
	}

	public void setReporter(SDFReporter<IQueryRetrieval<IStructureRecord>> reporter) {
		this.reporter = reporter;
	}	

	
}

class DBExporter extends DBProcessorPerformer<MyExporter,IQueryObject<IStructureRecord>,FileOutputState> {
	protected String output_tag;

	public DBExporter(MyExporter processor) {
		super(processor,false);
		this.output_tag =output_tag;
	}	
	public DBExporter(MyExporter processor,String output_tag) {
		super(processor,false);
		this.output_tag =output_tag;
	}
	@Override
	public FileOutputState execute() throws Exception {
		MyExporter exporter = getProcessor();
		exporter.setOutput((FileOutputState)context.get(output_tag));
		return super.execute();
	}
}
