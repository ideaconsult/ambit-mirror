package ambit2.db.processors.test;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.db.processors.ProcessorOntology;
import ambit2.db.processors.ProcessorOntology.OP;
import ambit2.db.processors.ProcessorOntology.SIDE;

public class ProcessorOntologyEditorTest extends DbUnitTest {
	protected ProcessorOntology editor = new ProcessorOntology();
	@Test
	public void testMoveProperty() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("expected","SELECT template.name FROM template join template_def using(idtemplate) join properties using(idproperty) where properties.name=\"Property 1\"");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals("BCF",names.getValue(0,"name"));
		editor.setConnection(c.getConnection());
		editor.setOperation(OP.MOVE);
		editor.setTemplate(new Dictionary("BCF",null),SIDE.LEFT);
		editor.setTemplate(new Dictionary("Endpoints",null),SIDE.RIGHT);
		editor.process(Property.getInstance("Property 1","Dummy reference"));
		names = 	c.createQueryTable("expected","SELECT template.name FROM template join template_def using(idtemplate) join properties using(idproperty) where properties.name=\"Property 1\"");
		Assert.assertEquals(1,names.getRowCount());
		Assert.assertEquals("Endpoints",names.getValue(0,"name"));		
		c.close();
	}
	@Test
	public void testMoveTemplate() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("expected","SELECT * FROM ontology where subject=\"BCF\" and object=\"Endpoints\"");
		Assert.assertEquals(0,names.getRowCount());	
		editor.setConnection(c.getConnection());
		editor.setOperation(OP.MOVE);
		editor.setTemplate(new Dictionary(null,null),SIDE.LEFT);
		editor.setTemplate(new Dictionary("Endpoints",null),SIDE.RIGHT);
		editor.process(new Dictionary("BCF",null));
		names = 	c.createQueryTable("expected","SELECT * FROM ontology where subject=\"BCF\" and object=\"Endpoints\"");
		Assert.assertEquals(1,names.getRowCount());	
		c.close();
	}	
	@Test
	public void testCreateTemplate() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("expected","SELECT * FROM ontology where subject=\"Skin irritation\" and object=\"Endpoints\"");
		Assert.assertEquals(0,names.getRowCount());	
		editor.setConnection(c.getConnection());
		editor.setOperation(OP.CREATE);
		editor.setTemplate(new Dictionary(null,null),SIDE.LEFT);
		editor.setTemplate(new Dictionary("Endpoints",null),SIDE.RIGHT);
		editor.setCurrentSide(SIDE.RIGHT);
		editor.process(new Dictionary("Skin irritation",null));
		
		names = 	c.createQueryTable("expected","SELECT * FROM ontology where subject=\"Skin irritation\" and object=\"Endpoints\"");
		Assert.assertEquals(1,names.getRowCount());	
		c.close();
	}		
	
	@Test
	public void testCreateProperty() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("expected","SELECT * FROM template_properties where template=\"Physicochemical effects\" and property=\"Boling point\"");
		Assert.assertEquals(0,names.getRowCount());	
		editor.setConnection(c.getConnection());
		editor.setOperation(OP.CREATE);
		editor.setTemplate(new Dictionary(null,null),SIDE.LEFT);
		editor.setTemplate(new Dictionary("Physicochemical effects",null),SIDE.RIGHT);
		editor.setCurrentSide(SIDE.RIGHT);
		editor.process( Property.getInstance("Boiling point","My new reference"));
		
		names = 	c.createQueryTable("expected","SELECT * FROM template_properties where template=\"Physicochemical effects\" and property=\"Boiling point\"");
		Assert.assertEquals(1,names.getRowCount());	
		c.close();
	}		
	/*
	@Test
	public void testSelectTemplate() throws Exception {
		setUpDatabase("src/test/resources/ambit2/db/processors/test/descriptors-datasets.xml");
		IDatabaseConnection c = getConnection();
		ITable names = 	c.createQueryTable("EXPECTED","SELECT * FROM template_properties where template=\"Physicochemical effects\" and property=\"Boling point\"");
		Assert.assertEquals(0,names.getRowCount());	
		editor.setConnection(c.getConnection());
		editor.setOperation(OP.SELECT);
		editor.setCurrentSide(SIDE.RIGHT);
		editor.process(new Dictionary("Physicochemical effects","Endpoints"));
		
		names = 	c.createQueryTable("EXPECTED","SELECT * FROM template_properties where template=\"Physicochemical effects\" and property=\"Boiling point\"");
		Assert.assertEquals(1,names.getRowCount());	
		c.close();
	}	
	*/	
}
