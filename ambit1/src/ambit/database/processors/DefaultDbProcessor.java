/* DefaultDbProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-3 
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

package ambit.database.processors;

import java.sql.Connection;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.DefaultProcessorEditor;
import ambit.processors.IAmbitDBProcessor;

/**
 * An abstract class for database processors.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-3
 */
public abstract class DefaultDbProcessor extends DefaultAmbitProcessor implements IAmbitDBProcessor {


    /**
     * 
     */
    public DefaultDbProcessor(Connection connection) throws AmbitException {
        super();
        prepare(connection);
    }

    public abstract void prepare(Connection connection) throws AmbitException;
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    public String toString() {
    	return getClass().getName();
    }
}
