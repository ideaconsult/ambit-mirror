/* SetLimitsProcessor.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-15 
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

package ambit2.processors;

import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.exceptions.AmbitException;

/**
 * Returns only object within specified limits, otherwise returns null. Could be used to present result as pages (inefficient).
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-15
 */
public class SetLimitsProcessor extends DefaultAmbitProcessor {
    protected long min = 0;
    protected long max = 1;
    protected long processed = 0;
    /**
     * 
     */
    public SetLimitsProcessor(long min, long max) {
        super();
        this.min = min;
        this.max = max;
        processed = 0;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object == null) return null;
        else if ((processed >= min) && (processed < max)) {
            processed++;
            return object;
        } else { processed++; return null; }
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#createResult()
     */
    public IAmbitResult createResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#getResult()
     */
    public IAmbitResult getResult() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#setResult(ambit2.processors.IAmbitResult)
     */
    public void setResult(IAmbitResult result) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }

}
