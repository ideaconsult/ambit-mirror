/* DbConnectionRefused.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-1 
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

package ambit2.database.exception;

import ambit2.data.AmbitObject;

/**
 * Database connection refused exception
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-1
 */
public class DbConnectionRefused extends DbAmbitException {

    /**
     * @param object
     */
    public DbConnectionRefused(AmbitObject object) {
        super(object);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param object
     * @param arg0
     */
    public DbConnectionRefused(AmbitObject object, String arg0) {
        super(object, arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param object
     * @param arg0
     */
    public DbConnectionRefused(AmbitObject object, Throwable arg0) {
        super(object, arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param object
     * @param arg0
     * @param arg1
     */
    public DbConnectionRefused(AmbitObject object, String arg0, Throwable arg1) {
        super(object, arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
