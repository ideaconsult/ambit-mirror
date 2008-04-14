/* JB_IterateReader.java
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

package ambit2.workflow.jukebox;

import java.io.File;
import java.io.IOException;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.repository.IProcessor;
import ambit2.repository.processors.ProcessorCloseReader;
import ambit2.workflow.ActivityIterator;
import ambit2.workflow.ActivityPrimitive;

import com.microworkflow.process.Activity;

/**
 * 
 * Defines a sequence to create {@link IIteratingChemObjectReader} from a {@link File},
 * iterate through records, execute IProcessor<BodyTarget, BodyResult> over each record and close the reader.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Apr 13, 2008
 */
public class JB_Iterator<BodyTarget, BodyResult> {
    public Activity createReaderIterationBlock(
            String fileTag, String readerTag, 
            String iterationTag,String iterationResultTag,
            String errorTag, 
            IProcessor<File, IIteratingChemObjectReader> file2reader,
            IProcessor<BodyTarget, BodyResult> body
            ) {
        
        ActivityPrimitive<File, IIteratingChemObjectReader> getReader = 
            new ActivityPrimitive<File, IIteratingChemObjectReader>(fileTag,readerTag, 
                    file2reader);
        
        ActivityIterator iterative = 
            new ActivityIterator<BodyTarget, BodyResult>(readerTag,
                    iterationTag,iterationResultTag,body);

        ActivityPrimitive<IIteratingChemObjectReader,IOException> close = 
            new ActivityPrimitive<IIteratingChemObjectReader,IOException>(readerTag,errorTag, 
                    new ProcessorCloseReader());
        return getReader.addStep(iterative).addStep(close);
        
    }


}
