/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/
package ambit2.db.processors;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.BitSet;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

/**
 * Writes fingerprints, taken from IStructureRecord.getProperties().get(AmbitCONSTANTS.Fingerprint).
 * TODO to be replaced by @{link CreateFingerprintChemical}
 * @author nina
 *
 */
public class FP1024Writer extends AbstractRepositoryWriter<IStructureRecord, IStructureRecord> {

	protected FPTable fpTable = FPTable.fp1024;
	public FP1024Writer() {
		
	}
	public FP1024Writer(FPTable tableName) {
		this();
		fpTable = tableName;
	}	

	public enum FP1024_status {invalid,valid,error};
	protected BigInteger[] h16 = new BigInteger[16];	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2919255769379557423L;
	protected static final String insert_bitset = "INSERT INTO %s (idchemical,time,bc,status,fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE time=?,bc=?,status=?,fp1=?,fp2=?,fp3=?,fp4=?,fp5=?,fp6=?,fp7=?,fp8=?,fp9=?,fp10=?,fp11=?,fp12=?,fp13=?,fp14=?,fp15=?,fp16=?,updated=CURRENT_TIMESTAMP()";
	protected PreparedStatement ps_bitset;
	
	@Override
	protected void prepareStatement(Connection connection) throws SQLException {
		ps_bitset = connection.prepareStatement(String.format(insert_bitset,fpTable.getTable()));
		
	}

	@Override
	public IStructureRecord write(IStructureRecord record) throws SQLException {
		if (record.getIdchemical() < 0) throw new SQLException("Undefined ID");
		Object time = record.getProperty(Property.getInstance(fpTable.getTimeProperty(),fpTable.getProperty()));
		if (time == null)
			time = new Long(0);
		else ((Long) time).longValue();
		Object fp = record.getProperty(Property.getInstance(fpTable.getProperty(),fpTable.getProperty()));
		if (fp == null) {
			writeBitSetValue(record.getIdchemical(), null, ((Long)time).longValue(),FP1024_status.error);
		} else if (fp instanceof BitSet)		
			writeBitSetValue(record.getIdchemical(), (BitSet) fp, ((Long)time).longValue(),FP1024_status.valid);
		else {
			writeBitSetValue(record.getIdchemical(), null, ((Long)time).longValue(),FP1024_status.invalid);			
		}
		return record;
	}
	
	protected void writeBitSetValue(int idchemical,BitSet bs, long fp_time , FP1024_status status) throws SQLException {
		int bitCount = 0;
		if (bs != null)	bitCount = bs.cardinality();
		ps_bitset.clearParameters();
		ps_bitset.setInt(1,idchemical);
		ps_bitset.setLong(2,fp_time);
		ps_bitset.setInt(3,bitCount);
		ps_bitset.setInt(4,status.ordinal()+1);
		int o = 5+16;

		if (bs == null)
			for (int i=0; i < h16.length; i++) h16[i] = new BigInteger("0");
		else 
			MoleculeTools.bitset2bigint16(bs,64,h16);		
		for (int i=0; i < h16.length; i++) {
			ps_bitset.setObject(i+5,h16[i]);
			ps_bitset.setObject(i+o+3,h16[i]);
		}
		
		ps_bitset.setLong(o,fp_time);
		ps_bitset.setInt(o+1,bitCount);
		ps_bitset.setInt(o+2,status.ordinal()+1);	
		ps_bitset.executeUpdate();
		
	}	
	@Override
	public void close() throws Exception {
		if (ps_bitset != null)
			ps_bitset.close();
		ps_bitset = null;
		super.close();
	}
}
