/* EINECSTest.java
 * Author: nina
 * Date: Mar 21, 2009
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

package ambit2.core.data.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.core.data.EINECS;

/*
select name,value,abcdef,g from
(
select name,value,
mod(substring(value,1,1) +
2*substring(value,2,1) +
3*substring(value,3,1) +
4*substring(value,5,1) +
5*substring(value,6,1) +
6*substring(value,7,1),11) abcdef,
substring(value,9,1) g
from
properties
join
property_values using(idproperty)
join property_string using(idvalue_string)
where (length(value)=9) and (substring(value,9,1)<11)
and (substring(value,4,1)='-')
and (substring(value,8,1)='-')
) e
where
(abcdef!=g)

*/
/**

 * @author nina
 *
 */
public class EINECSTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(EINECS.isValid("200-006-5"));
		Assert.assertTrue(EINECS.isValid("200-002-3"));
		Assert.assertFalse(EINECS.isValid("200-005-1"));
	}

	@Test
	public void testIsValid1() {
		Assert.assertFalse(EINECS.isValid("429-130-1"));
	}
	
	@Test
	public void testIsValid2() {
		Assert.assertTrue(EINECS.isValid("420-910-5"));
	}
	
	@Test
	public void testIsValid3() {
		Assert.assertFalse(EINECS.isValid("419-730-1"));
	}	
	@Test
	public void testIsValid4() {
		Assert.assertTrue(EINECS.isValid("243-161-4"));
	}	
	
	@Test
	public void testIsValid5() {
		Assert.assertFalse(EINECS.isValid("431-520-1"));
	}	
	@Test
	public void testIsValid6() {
		Assert.assertTrue(EINECS.isValid("206-316-7"));
	}		
	@Test
	public void testIsValidFormat() {
		Assert.assertTrue(EINECS.isValidFormat("614-842-7"));
	}	
}
