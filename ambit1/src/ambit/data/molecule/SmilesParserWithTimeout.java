/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.data.molecule;

import java.util.Timer;
import java.util.TimerTask;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.smiles.SmilesParser;

import ambit.misc.AmbitCONSTANTS;

/**
 * This is a hack to overcome SMILES parsing hanging forever on some SMILES.
 * setInterrupted method was introduced in {@link org.openscience.cdk.smiles.SmilesParser}. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class SmilesParserWithTimeout extends SmilesParser{
    public Molecule parseSmiles(String smiles, long timeout) throws InvalidSmilesException {

		Molecule mol = null;		
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
		    /* (non-Javadoc)
             * @see java.util.TimerTask#run()
             */
            public void run() {
               setInterrupted(true);
            }
		},timeout);
		long now = System.currentTimeMillis();
		try {
			synchronized (smiles) {
				mol = parseSmiles(smiles);
			}
		} catch (InvalidSmilesException x) {

			throw new InvalidSmilesException(x.getMessage());
		} catch (Exception x) {
			x.printStackTrace();
			smiles = x.getMessage();
		} finally {
			timer.cancel();
			timer = null;		    
		}
	
		if (mol == null)  { mol = new Molecule(); mol.setProperty(AmbitCONSTANTS.SMILES,smiles);}
		return mol;
    }

}


