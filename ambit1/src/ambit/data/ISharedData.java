/* ISharedData.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-30 
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

package ambit.data;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.DataContainer;



/**
 * An interface providing common functionalities for shared data used by several applications.
 * The basic assumption is that the application has a set of molecules and set of query molecules.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-30
 */
public interface ISharedData {
    /**
     * @return current molecule
     */
	public IMolecule getMolecule();
	/**
	 * Sets current molecule 
	 * @param molecule {@link IMolecule}
	 */
	public void setMolecule(IMolecule molecule);
	/**
	 * Creates a new molecule and adds it to the current set of molecules
	 */
	public void newMolecule();
	/**
	 * @return current query {@link IMolecule}
	 */
	public IMolecule getQuery();
	/**
	 * Sets current query
	 * @param molecule {@link IMolecule}
	 */
	public void setQuery(IMolecule molecule);
	/**
	 * Creates a new query and adds it to the current set of query
	 */
	public void newQuery();		
	/**
	 * Application status
	 * @return {@link JobStatus}
	 */
	public JobStatus getJobStatus() ;
	/**
	 * Application status
	 * @param jobStatus {@link JobStatus}
	 */
	public void setJobStatus(JobStatus jobStatus) ;
	/**
	 * 
	 * @return default directory
	 */
    public String getDefaultDir();
    /**
     * Sets default directory
     * @param defaultDir
     */
    public void setDefaultDir(String defaultDir);
    /**
     * 
     * @return template directory
     */
    public String getTemplateDir();
    /**
     * Sets template directory
     * @param defaultDir
     */
    public void setTemplateDir(String defaultDir);	   
    DataContainer getMolecules();
    DataContainer getQueries();
    
    public String getTMPFile();
    public void setTMPFile(String file);
}
