/* IterativeReader.java
 * Author: Nina Jeliazkova
 * Date: Apr 13, 2008 
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

import java.util.Iterator;

import ambit2.repository.IProcessor;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Iterative;
import com.microworkflow.process.Primitive;

/**
 * 
 * Expect get(targetKey) to return an {@link Iterator}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 13, 2008
 */
public class ActivityIterator<IterationTarget,IterationResult> extends Iterative {
    public ActivityIterator(String targetKey,  String iterationTargetKey, 
            String iterationResultKey, IProcessor<IterationTarget,IterationResult> body) {
        super(targetKey,
                new Performer(){
                    public Object execute() {
                        Object o = get(targetKey);
                        if (o instanceof Iterator) return (Iterator) o;
                        else return null;  
                    }
                },
                iterationTargetKey,
                new Primitive(iterationTargetKey,iterationResultKey, 
                        new ProcessorPerformer(body))
                );
    }    
}
