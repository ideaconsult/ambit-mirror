/**
 * Created on 2005-3-29
 *
 */
package ambit.test.data;

import junit.framework.TestCase;
import ambit.data.AmbitList;
import ambit.data.AmbitListChanged;
import ambit.data.IAmbitListListener;
import ambit.data.AmbitObject;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;


/**
 * JUnit test for {@link ambit.data.AmbitList} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ambitListTestCase extends TestCase {
	protected static int listFires = 0;
	protected static int objectFires = 0;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ambitListTestCase.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		listFires = 0;
		objectFires = 0;
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	protected void ambitListModified(AmbitList aList) {
		IAmbitListListener al = new IAmbitListListener() {
			/* (non-Javadoc)
			 * @see ambit.data.IAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
			 */
			public void ambitObjectChanged(AmbitObjectChanged event) {
				boolean m = event.getObject().isModified();
				assertTrue(m);
				objectFires++;
			}
			public void ambitListChanged(AmbitListChanged event) {
				boolean m = event.getList().isModified();
				String s = "";
				if (event.getObject() != null)
					s = event.getObject().toString();
				System.out.println(
						event.getList().getSelectedIndex() +
						"\t" + s
						);
				assertTrue(m);
				listFires++;
			}			
		};
		aList.addListListener(al);		
		IAmbitObjectListener aol = new IAmbitObjectListener() {
			/* (non-Javadoc)
			 * @see ambit.data.IAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
			 */
			public void ambitObjectChanged(AmbitObjectChanged event) {
				boolean m = event.getObject().isModified();
				assertTrue(m);
			}
		};		
		aList.addAmbitObjectListener(aol);
		
	}
	public void testSetModified() {
		AmbitList aList = new AmbitList();
		aList.setNotModified();
		ambitListModified(aList);
		aList.addItem(new AmbitObject("name 1"));
		assertEquals(1,listFires);
		aList.setNotModified();
		
		aList.addItem(new AmbitObject("name 2"));
		assertEquals(2,listFires);
		aList.setNotModified();
		
		aList.addItem(new AmbitObject("name 3"));
		assertEquals(3,listFires);
		aList.setNotModified();
		aList.remove(5);
		assertEquals(3,listFires);
		aList.setNotModified();
		aList.remove(2);
		assertEquals(4,listFires);
		aList.setSelectedIndex(1);
		assertEquals(5,listFires);
		aList.clear();
		
		assertEquals(6,listFires);
	}

}
