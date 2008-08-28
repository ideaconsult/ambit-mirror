/* DefaultAppplicationContext.java
 * Author: Nina Jeliazkova
 * Date: Jul 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package nplugins.shell.application;

import nplugins.core.NPluginsException;
import nplugins.shell.INPApplicationContext;

public class DefaultAppplicationContext implements INPApplicationContext {
    protected TaskMonitor monitor;

    public DefaultAppplicationContext() {
        monitor = new TaskMonitor();
    }
    public Object getBean(String id) throws NPluginsException {
        throw new NPluginsException("Not implemented");
    }

    public TaskMonitor getTaskMonitor(){
        return monitor;
    }

}
