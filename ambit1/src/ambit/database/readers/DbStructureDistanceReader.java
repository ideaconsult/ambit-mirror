/* DbStructureDistanceReader.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
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

package ambit.database.readers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//TODO search :  select * from cas join  atom_structure using (idstructure) join atom_distance using(iddistance) where atom1="O" and atom2="O" and distance =11;
//TODO stats: select count(distinct(idstructure)),atom1,atom2,distance from atom_structure join atom_distance using (iddistance ) group by atom1,atom2,distance;
/**
 * Reads structures without precalculated pairwise atom distances from the database. Used by {@link ambit.ui.actions.process.DBCalculateAtomDistancesAction}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class DbStructureDistanceReader extends DbStructureReader {
    public static String MISSING_DISTANCES =
        //"select s.idstructure from structure as s where  idstructure not in (select idstructure from atom_structure) limit ";
        //"select structure.idstructure,idsubstance from structure left join atom_structure using(idstructure) where iddistance is null group by (idstructure) limit ";
        "select structure.idstructure from structure left join atom_structure using(idstructure) where (type_structure>\"2D with H\") and (iddistance is null) group by (idstructure) limit ";
        //"select structure.idstructure,idsubstance,uncompress(structure) as ustructure from structure left join atom_structure using(idstructure) where iddistance is null group by (idstructure) limit ";
    protected Statement stmt = null;

    /**
     * 
     */
    public DbStructureDistanceReader(Connection conn, long limit) {
        super();
        try {
			//stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY); 					    
            stmt = conn.createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			//stmt.setFetchSize(Integer.MIN_VALUE);
			logger.info(MISSING_DISTANCES + Long.toString(limit));
			ResultSet rs = stmt.executeQuery(MISSING_DISTANCES + Long.toString(limit));
			logger.info("Read "+MISSING_DISTANCES + Long.toString(limit));
			setResultset(rs);
        } catch (SQLException x) {
            logger.error(x);
            stmt = null;
        }
    }

    /**
     * @param resultset
     */
    public DbStructureDistanceReader(ResultSet resultset) {
        super(resultset);
    }
    /* (non-Javadoc)
     * @see ambit.database.DbReader#close()
     */
    public void close() throws IOException {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException x) {
            logger.error(x);
        }
        super.close();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Structures without calculated pairwise atom distances from database";
    }
}
