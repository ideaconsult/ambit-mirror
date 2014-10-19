/* IStructureKey.java
 * Author: nina
 * Date: Mar 24, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.core.processors.structure.key;

import net.idea.modbcum.i.processors.IProcessor;

public interface IStructureKey<Target, Result> extends IProcessor<Target, Result>{
	public Object getKey();
	public Object getQueryKey();
	public Class getType();
	public boolean useExactStructureID();
	
	public enum Matcher {

		CAS {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.CASKey";
			}
			@Override
			public String getDescription() {
				return "Match by CAS registry number";
			}			
		},
		EINECS {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.EINECSKey";
			}
			@Override
			public String getDescription() {
				return "Match by EINECS registry number";
			}			
		},
		PubChemCID {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.PubchemCID";
			}
			@Override
			public String getDescription() {
				return "Match by PubChem Compound ID (PUBCHEM_COMPOUND_CID)";
			}			
		},
		PubChemSID {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.PubchemSID";
			}
			@Override
			public String getDescription() {
				return "Match by PubChem Substance ID (PUBCHEM_COMPOUND_SID)";
			}			
		},		
		DSSToxCID {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.DSSToxCID";
			}
			@Override
			public String getDescription() {
				return "Match by DSSTox Chemical ID (DSSTox_CID) number uniquely assigned to a particular STRUCTURE across all DSSTox files";
			}			
		},
		DSSToxRID {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.DSSToxRID";
			}
			@Override
			public String getDescription() {
				return "Match by DSSTox Record ID (DSSTox_RID) is number uniquely assigned to each DSSTox record across all DSSTox files";
			}			
		},
		DSSToxGenericSID {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.DSSGenericSID";
			}
			@Override
			public String getDescription() {
				return "Records with the same DSSTox_Generic_SID (Generic Substance ID) will share all DSSTox Standard Chemical Fields, including STRUCTURE. Field distinguishes at the level of \"Test Substance\" across all DSSTox data files, most often corresponding to the level of CASRN distinction, but not always.";
			}			
		},		
		InChI {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.InchiPropertyKey";
			}
			@Override
			public String getDescription() {
				return "Match by InChI";
			}
		},
		SMILES {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.SmilesKey";
			}
			@Override
			public String getDescription() {
				return "Match by SMILES";
			}
		},
		ChEMBL {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.ChEMBLCompoundURI";
			}
			@Override
			public String getDescription() {
				return "http://rdf.farmbio.uu.se/chembl/onto/#forMolecule";
			}
		},	
		SAMPLE {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.SampleKey";
			}
			@Override
			public String getDescription() {
				return "Match by column \"SAMPLE\"";
			}
		},		
		NAME {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.NameKey";
			}
			@Override
			public String getDescription() {
				return "Match by chemical name";
			}
		},			
		IUCLID5_REFERENCESUBSTANCE {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.ReferenceSubstanceUUID";
			}
			@Override
			public String getDescription() {
				return "Match by IUCLID5 Refenrence Substance UUID";
			}			
		},
		None {
			@Override
			public String getClassName() {
				return "ambit2.core.processors.structure.key.NoneKey";
			}
			@Override
			public String getDescription() {
				return "Don't match, add as a new structure";
			}			
		};	
		public abstract String getClassName();
		public abstract String getDescription();
		public IStructureKey getMatcher() {
			try {
				Class classDefinition = Class.forName(getClassName());
				return (IStructureKey) classDefinition.newInstance();
			} catch (Exception x) {
				return null;
			}
		}
				
	}
}
