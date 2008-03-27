package ambit2.test.data.descriptors;

import junit.framework.TestCase;
import ambit2.data.literature.ReferenceFactory;
import ambit2.database.query.DescriptorQuery;
import ambit2.database.query.DescriptorQueryList;
import ambit2.database.query.DistanceQuery;

public class DescriptorQueryListTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DescriptorQueryListTest.class);
	}
	public void testSQLGeneration() {
		DescriptorQuery q = new DescriptorQuery("XlogP",ReferenceFactory.createEmptyReference());
		q.setId(1);
		q.setValue(5);
		String s = q.toSQL("logP");
		//System.out.println(s);
		assertEquals("select dvalues.idstructure from dvalues where iddescriptor=1 and value between 0 and 1",s);
		s = q.id2SQL("logP.");
		//System.out.println(s);
		assertEquals("logP.iddescriptor=1 and logP.value between 0 and 1",s.trim());
	}

	public void testListSQLGeneration() {
		DescriptorQuery q = new DescriptorQuery("XlogP",ReferenceFactory.createEmptyReference());
		q.setId(1);
		q.setMinValue(2);
		q.setMaxValue(3);
		q.setEnabled(true);
		
		DescriptorQuery q1 = new DescriptorQuery("MolWeight",ReferenceFactory.createEmptyReference());
		q1.setId(3);
		q1.setMinValue(200);
		q1.setMaxValue(300);
		q1.setEnabled(true);
	
		DescriptorQueryList l = new DescriptorQueryList();
		l.setCombineWithAND(true);
		l.addItem(q);
		l.addItem(q1);
		String s = l.toSQL(null,0,100);
		//System.out.println("---");
		//System.out.println(s);
		//System.out.println("---");
		assertEquals(
		        "select D0.idstructure,D0.value,D1.value"+
		        "\nfrom"+
		        "\nDVALUES as D0"+
		        "\njoin DVALUES as D1 using(idstructure)"+ 
		        "\nwhere  D0.iddescriptor=1 and D0.value between 2 and 3"+ 
		        "\nand  D1.iddescriptor=3 and D1.value between 200 and 300"+ 
		        "\norder by idstructure"+
		        "\nlimit 0,100"
		        ,s.trim());
		l.setCombineWithAND(false);
		s = l.toSQL(null,0,100);
		
		assertEquals(
		        "select "+
		        "\nidsubstance,structure.idstructure"+ 
		        "\nfrom"+ 
		        "\n("+
		        "\nselect dvalues.idstructure from dvalues where iddescriptor=1 and value between 2 and 3"+
		        "\nunion"+
		        "\nselect dvalues.idstructure from dvalues where iddescriptor=3 and value between 200 and 300"+
		        "\n) as T"+
		        "\njoin structure using(idstructure)"+
		        "\nlimit 0,100"
		        ,s.trim());
		//System.out.println(s);
		DistanceQuery qd = new DistanceQuery("N","C",11);
		qd.setEnabled(true);
		l.addItem(qd);
		s = l.toSQL(null,0,100);

		assertEquals(
		        "select "+
		        "\nidsubstance,structure.idstructure"+
		        "\nfrom"+
		        "\n("+
		        "\nselect dvalues.idstructure from dvalues where iddescriptor=1 and value between 2 and 3"+
		        "\nunion"+
		        "\nselect dvalues.idstructure from dvalues where iddescriptor=3 and value between 200 and 300"+
		        "\nunion"+
		        "\nselect idstructure from atom_structure join atom_distance using(iddistance) where"+
		        "\natom1=\"C\" and atom2=\"N\" and distance between 0 and 1"+
		        "\n"+
		        "\n) as T"+
		        "\njoin structure using(idstructure)"+
		        "\nlimit 0,200"
		         ,s);
		l.setCombineWithAND(true);
		s = l.toSQL(null,0,100);
		assertEquals(
		        "select D0.idstructure,D0.value,D1.value,distance"+
		        "\nfrom"+
		        "\nDVALUES as D0"+
		        "\njoin DVALUES as D1 using(idstructure)"+
		        "\njoin atom_structure as D2 using(idstructure)"+
		        "\njoin atom_distance using(iddistance)"+
		        "\nwhere  D0.iddescriptor=1 and D0.value between 2 and 3"+
		        "\nand  D1.iddescriptor=3 and D1.value between 200 and 300"+
		        "\nand atom1=\"C\" and atom2=\"N\" and distance between 0 and 1"+
		        "\norder by idstructure"+
		        "\nlimit 0,200"
		        ,s);
		q1.setEnabled(false);
		q.setEnabled(false);
		//only distance query
		s = l.toSQL(null,0,100);
		
		assertEquals(
		        "select D2.idstructure,distance"+
		        "\nfrom"+
		        "\natom_structure as D2"+
		        "\njoin atom_distance using(iddistance)"+
		        "\nwhere atom1=\"C\" and atom2=\"N\" and distance between 0 and 1"+
		        "\norder by idstructure"+
		        "\nlimit 0,200"
		        ,s);
		qd.setEnabled(false);
		q1.setEnabled(true);
		//only second descriptor query
		s = l.toSQL(null,0,500);
		assertEquals(
		        "select D1.idstructure,D1.value"+
		        "\nfrom"+
		        "\nDVALUES as D1"+
		        "\nwhere  D1.iddescriptor=3 and D1.value between 200 and 300"+
		        "\norder by idstructure"+
		        "\nlimit 0,500"
		        ,s);
	}	
	public void testDistanceSQLGeneration() {
		DistanceQuery q = new DistanceQuery("N","C",11);
		q.setCondition("=");
		String[] atoms = q.getAtoms();
		assertEquals("C",atoms[0]);
		assertEquals("N",atoms[1]);
		assertEquals(11,q.getValue(),0.001);
		
		assertEquals(
		        "select idstructure from atom_structure join atom_distance using(iddistance) where"+
		        "\natom1=\"C\" and atom2=\"N\" and distance = 11",
		        q.toSQL().trim());
		
		
		q.setCondition("between");
		q.setMaxValue(13);
		q.setMinValue(11);
		assertEquals(
		        "select idstructure from atom_structure join atom_distance using(iddistance) where"+
		        "\natom1=\"C\" and atom2=\"N\" and distance between 11 and 13",
		        q.toSQL().trim());
	}	
	
}
