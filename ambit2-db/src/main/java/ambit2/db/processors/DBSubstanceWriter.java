package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.substance.CreateSubstance;
import ambit2.db.substance.relation.UpdateSubstanceRelation;

/**
 * Writes IUCLID5 substances
 * @author nina
 *
 */
public class DBSubstanceWriter  extends AbstractDBProcessor<IStructureRecord, IStructureRecord> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2237399197958151808L;
	private CreateSubstance q;
    private UpdateSubstanceRelation qr;
    private UpdateExecutor x;
    private RepositoryWriter writer;

    
	public DBSubstanceWriter() {
		super();
	    q = new CreateSubstance();
	    qr = new UpdateSubstanceRelation();
	    x = new UpdateExecutor();
	    x.setCloseConnection(false);
	    writer = new RepositoryWriter();
	    writer.setCloseConnection(false);
	    writer.setPropertyKey(new ReferenceSubstanceUUID());
		writer.setUsePreferredStructure(true);
		writer.setDataset(new SourceDataset("I5Z INPUT",LiteratureEntry.getInstance("File","file:input.i5z")));
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		x.setConnection(connection);
		writer.setConnection(connection);
	}
	@Override
	public void close() throws SQLException {
		try {x.close();} catch (Exception x) {}
		try {writer.close();} catch (Exception x) {}
		super.close();
	}
	@Override
	public IStructureRecord process(IStructureRecord record) throws AmbitException {
		 try {
			 if (record instanceof SubstanceRecord) {
	         	SubstanceRecord substance = (SubstanceRecord) record;
	         	q.setObject(substance);
	         	x.process(q);
	         	for (CompositionRelation rel : substance.getRelatedStructures()) {
	         		Object i5uuid = rel.getSecondStructure().getProperty(Property.getI5UUIDInstance());
	         		if (rel.getSecondStructure().getIdchemical()<=0) {
	         			writer.write(rel.getSecondStructure());		
	         		}
	         		rel.getSecondStructure().setProperty(Property.getI5UUIDInstance(),i5uuid);
	         		qr.setCompositionRelation(rel);
	         		x.process(qr);
	         	}
	         } else if (record instanceof IStructureRecord) {
	         	writer.write(record);
	         }
			 return record;
		 } catch (AmbitException x) {
			 throw x;
		 } catch (Exception x) {
			 throw new AmbitException(x);
		 }
	}

	@Override
	public void open() throws DbAmbitException {
		
	}
	
}