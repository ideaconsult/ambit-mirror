/*
Copyright (C) 2007-2008  

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

package ambit2.repository.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ambit2.repository.StructureRecord;

/**
<pre>
CREATE  TABLE IF NOT EXISTS `ambit_repository`.`chemicals` (
  `idchemical` INT NOT NULL  AUTO_INCREMENT ,
  `inchi` text character set latin1 collate latin1_bin,
  `smiles` text character set latin1 collate latin1_bin,
  `formula` VARCHAR(64) NULL ,
  PRIMARY KEY (`idchemical`),
  KEY `sinchi` (`inchi`(760)),
  KEY `ssmiles` (`smiles`(760))
  );
  
DROP TABLE IF EXISTS `ambit_repository`.`structure`;
CREATE TABLE  `ambit_repository`.`structure` (
  `idstructure` int(11) NOT NULL auto_increment,
  `idchemical` int(11) NOT NULL,
  `structure` blob NOT NULL,
  `format` varchar(16) collate utf8_bin NOT NULL default 'CML',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`idstructure`),
  KEY `idchemical` (`idchemical`),
  CONSTRAINT `fk_idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
</pre>  
 * @author nina
 *
 */
public class RepositoryWriter extends AbstractRepositoryWriter<String,List<StructureRecord>> {
	protected static final String insert_chemical = "INSERT INTO CHEMICALS (idchemical) values (null)";
	protected PreparedStatement ps_chemicals;
	protected static final String insert_structure = "INSERT INTO STRUCTURE (idstructure,idchemical,structure,format) values (null,?,compress(?),?)";
	protected PreparedStatement ps_structure;

	public List<StructureRecord> write(String arg0) throws SQLException {
        List<StructureRecord> sr = new ArrayList<StructureRecord>();
		ps_chemicals.executeUpdate();
		ResultSet rs = ps_chemicals.getGeneratedKeys();
		ps_structure.clearParameters();
		while (rs.next()) {
			ps_structure.setInt(1,rs.getInt(1));
			ps_structure.setString(2,arg0);
			ps_structure.setString(3,"SDF");
			ps_structure.executeUpdate();
            ResultSet rss = ps_structure.getGeneratedKeys();
            while (rss.next()) 
                sr.add(new StructureRecord(rs.getInt(1),rss.getInt(1),null,null));
            rss.close();
		} 
		rs.close();
        return sr;
        
	}
	protected void prepareStatement(Connection connection) throws SQLException {
		 ps_chemicals = connection.prepareStatement(insert_chemical,Statement.RETURN_GENERATED_KEYS);
		 ps_structure = connection.prepareStatement(insert_structure,Statement.RETURN_GENERATED_KEYS);
	}
	public void close() throws SQLException {
		ps_chemicals.close();
		ps_structure.close();
		super.close();
	}
}
