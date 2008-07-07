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

package ambit.data.qmrf;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.AmbitObjectChanged;
import ambit.data.DefaultSharedData;
import ambit.data.IAmbitObjectListener;
import ambit.data.JobStatus;
import ambit.data.molecule.DataContainer;
import ambit.log.AmbitLogger;

public class QMRFData extends DefaultSharedData implements IAmbitObjectListener {
	protected QMRFObject qmrf;
	
	public QMRFData(String[] args, String configFile, boolean adminUser)  {
		super(configFile);

		qmrf = new QMRFObject(args,adminUser);
		qmrf.init();
        qmrf.addAmbitObjectListener(this);


		
	}
	protected void init() {
	    loadConfiguration();
		AmbitLogger.configureLog4j(true);
		jobStatus = new JobStatus();
		jobStatus.setModified(true);
	}
	
	public IMolecule getMolecule() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataContainer getMolecules() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataContainer getQueries() {
		// TODO Auto-generated method stub
		return null;
	}

	public IMolecule getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public void newMolecule() {
		// TODO Auto-generated method stub

	}

	public void newQuery() {
		// TODO Auto-generated method stub

	}

	public void setMolecule(IMolecule molecule) {
		// TODO Auto-generated method stub

	}

	public void setQuery(IMolecule molecule) {
		// TODO Auto-generated method stub

	}

	public QMRFObject getQmrf() {
		return qmrf;
	}

	public void setQmrf(QMRFObject qmrf) {
		this.qmrf = qmrf;
	}
    public void setParameters(String[] args) {
        qmrf.setParameters(args);
    }
    public void ambitObjectChanged(AmbitObjectChanged event) {
        setChanged();
        notifyObservers(event.getObject());
        
    }
	public void setAdminUser(boolean adminUser) {
		qmrf.setAdminUser(adminUser);
	}
    
}


