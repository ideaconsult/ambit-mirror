/* CASSmilesLookupTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-12 
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

package ambit.test.processors;

import junit.framework.TestCase;
import ambit.processors.IdentifiersProcessor;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-12
 */
public class CASSmilesLookupTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CASSmilesLookupTest.class);
    }
    public void testHyphenateCas() {
        try {
        String cas = IdentifiersProcessor.hyphenateCAS("50000");
        assertEquals("50-00-0",cas);
        } catch (Exception x) {
            
        }
    }
}