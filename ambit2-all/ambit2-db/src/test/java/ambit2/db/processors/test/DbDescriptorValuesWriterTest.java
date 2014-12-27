/* DbDescriptorValuesWriterTest.java
 * Author: nina
 * Date: Jan 7, 2009
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

/**
 * 
 */
package ambit2.db.processors.test;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.StringDescriptorResultType;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.db.RepositoryReader;
import ambit2.db.processors.DbDescriptorValuesWriter;
import ambit2.descriptors.SizeDescriptor;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.descriptors.processors.PropertyCalculationProcessor;

/**
 * @author nina
 *
 */
public class DbDescriptorValuesWriterTest extends DbUnitTest {
	DbDescriptorValuesWriter writer;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		writer = new DbDescriptorValuesWriter();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorValuesWriter#getStructure()}.
	 */
	@Test
	public void testGetStructure() {
		writer.setStructure(new StructureRecord(7,100211,"",""));
		Assert.assertEquals(100211,writer.getStructure().getIdstructure());
	}

	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorValuesWriter#setStructure(ambit2.base.interfaces.IStructureRecord)}.
	 */
	@Test
	public void testSetStructure() {
		writer.setStructure(new StructureRecord(7,100211,"",""));
		Assert.assertEquals(7,writer.getStructure().getIdchemical());
	}



	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorWriter#write(org.openscience.cdk.qsar.DescriptorValue)}.
	 */
	@Test
	public void testWriteDescriptorValue() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(2,values.getRowCount());
		ITable templates = 	c.createQueryTable("EXPECTED_TEMPLATES","SELECT * FROM template");	
		Assert.assertEquals(5,templates.getRowCount());		
		ITable dictionary = 	c.createQueryTable("EXPECTED_ONTOLOGY","SELECT * FROM dictionary");	
		Assert.assertEquals(3,dictionary.getRowCount());		
		
        writer.setConnection(c.getConnection());
        writer.open();
        XLogPDescriptor xlogp = new XLogPDescriptor();
		writer.setStructure(new StructureRecord(7,100211,"",""));
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(6,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(3,values.getRowCount());	        
        
        DescriptorValue v = new DescriptorValue(
                new DescriptorSpecification("XLogPReference","XLogPTitle","XLogPIdentifier","XLogPVendor"),
                new String[] {},
                new Object[] {},
                new DoubleResult(5.01),
                new String[] {"XLogP"}
                );
		writer.setStructure(new StructureRecord(10,100214,"",""));        
        writer.write(v);
        //one more time
        writer.write(v);        
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(7,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT value_num FROM property_values WHERE idstructure=100214");	
		Assert.assertEquals(1,values.getRowCount());
		Assert.assertEquals(5.01,(Double)DataType.DOUBLE.typeCast(values.getValue(0,"value_num")),1E-4);
		c.close();
	}

	@Test
	public void testWriteStringDescriptorValue() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(2,values.getRowCount());
		
        writer.setConnection(c.getConnection());
        writer.open();
        XLogPDescriptor xlogp = new XLogPDescriptor();
		writer.setStructure(new StructureRecord(7,100211,"",""));
        writer.write(xlogp.calculate(MoleculeFactory.makeAlkane(10)));
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(6,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(3,values.getRowCount());	        
        
        DescriptorValue v = new DescriptorValue(
                new DescriptorSpecification("XLogPReference","XLogPTitle","XLogPIdentifier","XLogPVendor"),
                new String[] {},
                new Object[] {},
                new StringDescriptorResultType("TESTVALUE"),
                new String[] {"XLogP"}
                );
		writer.setStructure(new StructureRecord(10,100214,"",""));        
        writer.write(v);
        //one more time
        writer.write(v);        
        c.close();
        
        c = getConnection();
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(7,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT value FROM values_string WHERE idstructure=100214");	
		Assert.assertEquals(1,values.getRowCount());
		Assert.assertEquals("TESTVALUE",values.getValue(0,"value"));	
		c.close();
	}	
	
	/**
	 * Multiple values with the same name for a given structure are not allowed.
	 * @throws Exception
	 */
	@Test
	public void testWriteMultipleValues() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values join properties using(idproperty)");	
		Assert.assertEquals(2,values.getRowCount());
		
        writer.setConnection(c.getConnection());
        writer.open();
        WeightDescriptor xlogp = new WeightDescriptor();
		writer.setStructure(new StructureRecord(7,100211,"",""));
		DescriptorValue value = xlogp.calculate(MoleculeFactory.makeBenzene());
		Assert.assertEquals(72.0,((DoubleResult)value.getValue()).doubleValue(),1E-4);
        writer.write(value);
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(6,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values join properties using(idproperty) WHERE abs(value_num-72)<1E-4");
		Assert.assertEquals(1,values.getRowCount());	        
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values join properties using(idproperty) WHERE abs(value_num-144)<1E-4");
		Assert.assertEquals(0,values.getRowCount());
		
		value = xlogp.calculate(MoleculeFactory.makeAlkane(12));
		Assert.assertEquals(144.0,((DoubleResult)value.getValue()).doubleValue(),1E-4);		

        writer.write(value);
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values join properties using(idproperty) WHERE abs(value_num-144)<1E-4");	
		Assert.assertEquals(1,values.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values join properties using(idproperty) WHERE abs(value_num-72)<1E-4");	
		Assert.assertEquals(0,values.getRowCount());	                
		
        c.close();
        

	}		
	@Test
	public void testReadWriteProperty() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/experiments-datasets.xml");
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT * FROM property_values");
		Assert.assertEquals(0,names.getRowCount());
		
		XLogPDescriptor xlogp = new XLogPDescriptor();
		
        RepositoryReader reader = new RepositoryReader();
        reader.setConnection(c.getConnection());

        writer.setConnection(c.getConnection());
        writer.open();
        reader.open();
        int records = 0;
		long now = System.currentTimeMillis();
		IChemObjectBuilder b = SilentChemObjectBuilder.getInstance();
		IStructureRecord o  ;
		while (reader.hasNext()) {
			o = reader.next();
			String content = reader.getStructure(o.getIdstructure());
			if (content == null) continue;
			IIteratingChemObjectReader mReader = new MyIteratingMDLReader(new StringReader(content),b);
			
			if (mReader.hasNext()) {
				Object mol = mReader.next();
				if (mol instanceof IMolecule) {
					writer.setStructure(o);
					writer.write(xlogp.calculate((IMolecule)mol));
				}
			}
			o.clear();
			mReader.close();
			mReader = null;
			records ++;
		}
		reader.close();
		writer.close();
		now = System.currentTimeMillis() - now;
		
		c = getConnection();
		names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT * FROM property_values where value_num is not null");
		Assert.assertEquals(2,names.getRowCount());		
		names = 	c.createQueryTable("EXPECTED_FIELDS","SELECT * FROM property_values  where idstructure in (105095,109287)");
		Assert.assertEquals(2,names.getRowCount());
		c.close();
		
	}		
	
	/**
	 * 3D 
	 * @return
	 * @throws Exception
	 */
	public IMolecule getTestMolecule() throws Exception {
		IMolecule mol = new org.openscience.cdk. Molecule();
		InputStream in = SizeDescriptor.class.getClassLoader().getResourceAsStream(
						"ambit2/db/processors/sdf/224824.sdf");
		Assert.assertNotNull(in);
		MDLV2000Reader reader = new MDLV2000Reader(in);
		
		mol = (IMolecule) reader.read(mol);
		reader.close();
		return mol;
	}
	/**
	 * Test method for {@link ambit2.db.processors.DbDescriptorWriter#write(org.openscience.cdk.qsar.DescriptorValue)}.
	 */
	@Test
	public void testWriteDescriptorValueFromProfile() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");			
        IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5,names.getRowCount());
		ITable values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values");	
		Assert.assertEquals(2,values.getRowCount());
		ITable templates = 	c.createQueryTable("EXPECTED_TEMPLATES","SELECT * FROM template");	
		Assert.assertEquals(5,templates.getRowCount());		
		ITable template_def = 	c.createQueryTable("EXPECTED_TEMPLATES","SELECT * FROM template_def");	
		Assert.assertEquals(3,template_def.getRowCount());		
		ITable dictionary = 	c.createQueryTable("EXPECTED_ONTOLOGY","SELECT * FROM dictionary");	
		Assert.assertEquals(3,dictionary.getRowCount());		
		
        writer.setConnection(c.getConnection());
        writer.open();
        
		writer.setStructure(new StructureRecord(7,100211,"",""));
		
		IAtomContainer mol = getTestMolecule();  //3D molecule
		HydrogenAdderProcessor h = new HydrogenAdderProcessor();
		h.setAddEexplicitHydrogens(false);
		mol = h.process(mol);
		
		
		
		DescriptorsFactory factory = new DescriptorsFactory();

		PropertyCalculationProcessor calc = new PropertyCalculationProcessor();
		Profile profile = factory.process(null);
		Iterator<Property> i = profile.getProperties(true);
		int count = 0;
		int countValues = 0;
		while (i.hasNext()) {
			try {
				Property p = i.next();
				//if (p.getClazz().getName().indexOf("ambit")>=0) {
				if (p.isEnabled()) {
					count++;					
					calc.setProperty(i.next());
					DescriptorValue value = calc.process(mol);
					countValues += value.getNames().length;
					writer.write(value);

				}
			} catch (Exception x) {
				throw new Exception(x);
			}
			
		}
		
		
		names = 	c.createQueryTable("EXPECTED_NAMES","SELECT * FROM properties");	
		Assert.assertEquals(5+countValues,names.getRowCount());
		values = 	c.createQueryTable("EXPECTED_VALUES","SELECT * FROM property_values WHERE status ='OK' ");	
		Assert.assertEquals(countValues,values.getRowCount());    
		template_def = 	c.createQueryTable("EXPECTED_TEMPLATES","SELECT type FROM catalog_references where type=\"Algorithm\"");	
		Assert.assertEquals(4,template_def.getRowCount());			
        
		/*
        DescriptorValue v = new DescriptorValue(
                new DescriptorSpecification("XLogPReference","XLogPTitle","XLogPIdentifier","XLogPVendor"),
                new String[] {},
                new Object[] {},
                new DoubleResult(5.01),
                new String[] {"XLogP"}
                );
		writer.setStructure(new StructureRecord(10,100214,"",""));        
        writer.write(v);
        //one more time
        writer.write(v);        
        c.close();
*/
	}	
}
