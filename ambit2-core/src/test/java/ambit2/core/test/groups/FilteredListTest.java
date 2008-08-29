package ambit2.core.test.groups;

import junit.framework.TestCase;
import ambit2.core.groups.FilteredList;

public class FilteredListTest extends TestCase {
	protected FilteredList<Integer> list;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		list = new FilteredList<Integer>();
	}
	public void testAdd() throws Exception {
		list.setFiltered(false);
		list.clear();

		assertEquals(0,list.size());
		list.setFiltered(true);
		assertEquals(0,list.size());
		
		list.add(new Integer(2));
		assertEquals(1,list.size());
		list.setFiltered(false);
		assertEquals(1,list.size());
        
        list.setFiltered(false);
        list.add(new Integer(10));
        assertEquals(2,list.size());
        list.setFiltered(true);
        assertEquals(1,list.size());
        list.clear();
        assertEquals(0,list.size());
        list.setFiltered(false);
        assertEquals(2,list.size());
        
        Integer item = list.get(1);
        list.addToFilter(item);
        assertEquals(2,list.size());        
        list.setFiltered(true);
        assertEquals(1,list.size());

	}
    
}
