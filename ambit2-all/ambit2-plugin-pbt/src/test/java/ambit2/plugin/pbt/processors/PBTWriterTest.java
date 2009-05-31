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
package ambit2.plugin.pbt.processors;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.LiteratureEntry;
import ambit2.db.LoginInfo;
import ambit2.db.SourceDataset;
import ambit2.db.processors.DBProcessorsChain;
import ambit2.db.processors.RepositoryWriter;
import ambit2.plugin.pbt.DbUnitTest;
import ambit2.plugin.pbt.PBTWorkBook;
import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;

public class PBTWriterTest extends DbUnitTest {


	@Test
	public void testProcess() throws Exception {
		setUpDatabase("src/test/resources/ambit2/plugin/pbt/empty-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable structures = c.createQueryTable("EXPECTED_STRUCTURES",
		"SELECT * FROM structure");
		Assert.assertEquals(0, structures.getRowCount());			
		ITable names = c.createQueryTable("EXPECTED_NAMES",
				"SELECT * FROM properties");
		Assert.assertEquals(0, names.getRowCount());
		ITable values = c.createQueryTable("EXPECTED_VALUES",
				"SELECT * FROM property_values");
		Assert.assertEquals(0, values.getRowCount());
		ITable templates = c.createQueryTable("EXPECTED_TEMPLATES",
				"SELECT * FROM template");
		Assert.assertEquals(2, templates.getRowCount());
		ITable dictionary = c.createQueryTable("EXPECTED_ONTOLOGY",
				"SELECT * FROM dictionary");
		Assert.assertEquals(0, dictionary.getRowCount());

		LoginInfo li = new LoginInfo();
		li.setDatabase(getDatabase());
		li.setPort(getPort());
		li.setUser(getUser());
		li.setPassword(getUser());

		PBTWorkBook pbt = new PBTWorkBook();
		pbt.setStructure(MoleculeFactory.makeBenzene());
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB12(72.2);
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB13(100);
		pbt.getWorksheet(WORKSHEET_INDEX.SUBSTANCE).setB10("Benzene");
	
		SourceDataset dataset = new SourceDataset(PBTWorkBook.PBT_TITLE,
				LiteratureEntry.getInstance(PBTWorkBook.PBT_TITLE,"by Clariant"));
		
		RepositoryWriter writer = new RepositoryWriter();
		
        final DBProcessorsChain chain = new DBProcessorsChain();
	    chain.add(new PBTProperties());
	    chain.add(writer);
	    
		writer.setDataset(dataset);
		chain.setConnection(c.getConnection());
		chain.open();
		chain.process(pbt);
		chain.close();
		
		int count=72;
		
		c = getConnection();
		structures = c.createQueryTable("EXPECTED_STRUCTURES",	"SELECT * FROM structure");
		Assert.assertEquals(1, structures.getRowCount());
		
		templates = c.createQueryTable("substance_sheet","SELECT name,idvalue,idtype FROM properties join property_values using(idproperty) where name regexp '^SUBSTANCE_' order by name");
		Assert.assertEquals(8, templates.getRowCount());

		templates = c.createQueryTable("substance_sheet","SELECT name,idvalue,idtype FROM properties join property_values using(idproperty) where name regexp '^Persistence_' order by name");
		Assert.assertEquals(25, templates.getRowCount());

		templates = c.createQueryTable("substance_sheet","SELECT name,idvalue,idtype FROM properties join property_values using(idproperty) where name regexp '^Bioaccumulation_' order by name");
		Assert.assertEquals(19, templates.getRowCount());
		
		templates = c.createQueryTable("substance_sheet","SELECT name,idvalue,idtype FROM properties join property_values using(idproperty) where name regexp '^Toxicity_' order by name");
		Assert.assertEquals(19, templates.getRowCount());

		templates = c.createQueryTable("substance_sheet","SELECT name,idvalue,idtype FROM properties join property_values using(idproperty) where name regexp '^RESULT_' order by name");
		Assert.assertEquals(1, templates.getRowCount());
		
		
		templates = c.createQueryTable("EXPECTED_PROPERTIES",	"SELECT * FROM properties");
		Assert.assertEquals(count, templates.getRowCount());	
		templates = c.createQueryTable("EXPECTED_VALUES",	"SELECT * FROM property_values");
		Assert.assertEquals(count, templates.getRowCount());			
		c.close();
		
	}

}
