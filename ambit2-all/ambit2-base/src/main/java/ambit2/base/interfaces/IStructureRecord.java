/* IStructureRecord.java
 * Author: Nina Jeliazkova
 * Date: May 7, 2008 
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

package ambit2.base.interfaces;

import java.util.Map;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;


public interface IStructureRecord extends IChemical{
	public enum MOL_TYPE {SDF,CML,CSV,URI,INC,NANO};
	public static enum STRUC_TYPE {

		NA {
			@Override
			public String toString() {
				return "NA";
			}
		},
		MARKUSH {
			@Override
			public String toString() {
				return "MARKUSH";
			}
		},
		D1 {
			@Override
			public String toString() {
				return "SMILES";
			}
		},
		D2noH {
			@Override
			public String toString() {
				return "2D no H";
			}
		},
		D2withH {
			@Override
			public String toString() {
				return "2D with H";
			}
		},
		D3noH {
			@Override
			public String toString() {
				return "3D no H";
			}
		},
		D3withH {
			@Override
			public String toString() {
				return "3D with H";
			}
		},
		optimized {
			@Override
			public String toString() {
				return "optimized";
			}
		},
		experimental {
			@Override
			public String toString() {
				return "experimental";
			}
		},
		NANO {
			@Override
			public String toString() {
				return "NANO";
			}
		}
	};
		
	
    String getFormat();
    void setFormat(String format);

    int getIdstructure();
    void setIdstructure(int idstructure);

    String getContent();
    void setContent(String content);
    
    boolean isSelected();
    void setSelected(boolean value);

    int getNumberOfProperties();
    Iterable<Property> getProperties();
    //void setProperties(Map properties);
    void setProperty(Property key,Object value);    
    Object getProperty(Property key);
    Object removeProperty(Property key);
    void clearProperties();
    void addProperties(Map newProperties);
    void clear();
    String getWritableContent();
    ILiteratureEntry getReference();
    void setReference(ILiteratureEntry reference);
    Object clone() throws CloneNotSupportedException ;
    STRUC_TYPE getType();
    void setType(STRUC_TYPE type);
    /**
     * Data entry (tuples) in a dataset, i.e. row identifier
     * @return
     */
    int getDataEntryID();
    void setDataEntryID(int id);

    int getDatasetID();
    void setDatasetID(int id);
    
    boolean usePreferedStructure();
    void setUsePreferedStructure(boolean value);
    
    Iterable<IFacet> getFacets();
    void addFacet(IFacet facet);
    void removeFacet(IFacet facet);
    void clearFacets();
}