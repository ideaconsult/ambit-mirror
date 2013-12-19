package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.core.processors.structure.key.ReferenceSubstanceUUID;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.substance.CreateSubstance;
import ambit2.db.substance.relation.UpdateSubstanceRelation;
import ambit2.db.substance.study.UpdateEffectRecords;
import ambit2.db.substance.study.UpdateSubstanceStudy;

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
    private UpdateSubstanceStudy qss;
    private UpdateEffectRecords qeffr;
    private UpdateExecutor x;
    private RepositoryWriter writer;
 
	protected SubstanceRecord importedRecord;

    
	public SubstanceRecord getImportedRecord() {
		return importedRecord;
	}

	public void setImportedRecord(SubstanceRecord importedRecord) {
		this.importedRecord = importedRecord;
	}

	public SourceDataset getDataset() {
		return writer.getDataset();
	}

	public DBSubstanceWriter(SourceDataset dataset,SubstanceRecord importedRecord) {
		super();
	    q = new CreateSubstance();
	    qr = new UpdateSubstanceRelation();
	    x = new UpdateExecutor();
	    x.setCloseConnection(false);
	    writer = new RepositoryWriter();
	    writer.setCloseConnection(false);
	    writer.setPropertyKey(new ReferenceSubstanceUUID());
		writer.setUsePreferredStructure(true);
		writer.setDataset(dataset==null?datasetMeta():dataset);
		this.importedRecord = importedRecord;
	}
	
	public static SourceDataset datasetMeta() {
		ILiteratureEntry reference = LiteratureEntry.getI5UUIDReference();
		reference.setType(_type.Dataset);
		SourceDataset dataset = new SourceDataset("IUCLID5 .i5z file",reference);
		dataset.setLicenseURI(null);
		dataset.setrightsHolder(null);
		return dataset;
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
			 if (record==null) return record;
			 if (record instanceof SubstanceRecord) {
	         	SubstanceRecord substance = (SubstanceRecord) record;
	         	if (substance.getMeasurements()!=null) {
	         		for (ProtocolApplication papp : substance.getMeasurements()) {
	         			if (qss==null) qss = new UpdateSubstanceStudy(importedRecord.getCompanyUUID(), papp);
	         			else {
	         				qss.setGroup(importedRecord.getCompanyUUID());
	         				qss.setObject(papp);
	         			}
	         			x.process(qss);
	         			if ( papp.getEffects()!=null)
	         			for (Object effect : papp.getEffects()) 
	         				if (effect instanceof EffectRecord) {
	         					if (qeffr==null) qeffr = new UpdateEffectRecords(papp.getDocumentUUID(),(EffectRecord)effect);
	         					else {qeffr.setGroup(papp.getDocumentUUID()); qeffr.setObject((EffectRecord)effect);}
	         					x.process(qeffr);
	         				}
	         		}
	         	} else {
		         	q.setObject(substance);
		         	x.process(q);
	         		importedRecord.setCompanyUUID(substance.getCompanyUUID());
	         		importedRecord.setIdsubstance(substance.getIdsubstance());
		         	if (substance.getRelatedStructures()!=null)
			         	for (CompositionRelation rel : substance.getRelatedStructures()) {
			         		Object i5uuid = rel.getSecondStructure().getProperty(Property.getI5UUIDInstance());
			         		if (rel.getSecondStructure().getIdchemical()<=0) {
			         			writer.write(rel.getSecondStructure());		
			         		}
			         		rel.getSecondStructure().setProperty(Property.getI5UUIDInstance(),i5uuid);
			         		qr.setCompositionRelation(rel);
			         		x.process(qr);
			         	}
	         		
	         	}
	         } else if (record instanceof IStructureRecord) {
	        	 if (STRUC_TYPE.NA.equals(((IStructureRecord)record).getType())) {
	        		 writer.create(record); //with the current settings, if the structure is already there, it will be used
	        	 } else {
	        		 writer.update(record);  //with the current settings, if the structure is already there, it will be updated
	        	 }	 
	         }
			 return record;
		 } catch (AmbitException x) {
			 throw x;
		 } catch (Exception x) {
			 x.printStackTrace();
			 throw new AmbitException(x);
		 }
	}

	@Override
	public void open() throws DbAmbitException {
		
	}
	
}