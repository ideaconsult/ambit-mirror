/*
Copyright (C) 2005-2006  

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

package ambit2.database.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;

import ambit2.exceptions.AmbitException;
import ambit2.log.AmbitLogger;

/**
 * Read cas numbers from database (cas table). See the example at {@link ambit2.database.search.DbSimilarityByFingerprintsReader}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class ReadCASProcessor extends ReadIdentifierProcessor {
	protected static AmbitLogger logger = new AmbitLogger(ReadAliasProcessor.class);
	
	
	public ReadCASProcessor(Connection connection) throws AmbitException {
		super(connection);
        setIdentifierLabel(CDKConstants.CASRN);

	}

	public void prepare(Connection connection) throws AmbitException {
		try {
			psSubstance = connection.prepareStatement("select casno  from cas join structure using(idstructure) where idsubstance=?");
			psStructure = connection.prepareStatement("select casno  from cas where idstructure=?");
		} catch (SQLException x) {
			
		}
	}
	public Object getIdentifierValue(ResultSet rs) throws Exception {
        return rs.getString("casno");
    }

	public String toString() {
		return "Read CAS numbers from database";
	}
	public void close() {
		try {
			psSubstance.close();
			psStructure.close();
		} catch (Exception x) {
			logger.error(x);
		}
		super.close();
	}
}


