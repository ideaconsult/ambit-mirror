/* InternalProcessorPerformer.java
 * Author: Nina Jeliazkova
 * Date: Apr 14, 2008 
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

package ambit2.workflow;

import ambit2.repository.IProcessor;

import com.microworkflow.execution.Performer;

public class InternalProcessorPerformer<Target,Result,P extends IProcessor<Target,Result>> extends Performer<Target,Result> {
    public static String errorTag="error";
    protected String processorTag = "processor";
    
    public InternalProcessorPerformer() {
        
    }
    public InternalProcessorPerformer(String processorTag) {
        setProcessorTag(processorTag);
    }
    
    public synchronized String getProcessorTag() {
        return processorTag;
    }
    public synchronized void setProcessorTag(String processorTag) {
        this.processorTag = processorTag;
    }
    protected P getProcessor()  {
        return (P)get(getProcessorTag());
    }
    protected void setProcessor(P processor) {
        context.put(getProcessorTag(),processor);
    }
    @Override
    public Result execute() {
        try {
            P p = getProcessor();
            return p.process(getTarget());
        } catch (Exception x) {
            context.put(errorTag, x);
            return null;
        }
    }
    public static synchronized String getErrorTag() {
        return errorTag;
    }
    public static synchronized void setErrorTag(String errorTag) {
        InternalProcessorPerformer.errorTag = errorTag;
    }
}
