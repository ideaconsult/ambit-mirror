/*
Copyright (C) 2005-2008  

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

package ambit2.core.io;



import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;

import ambit2.core.groups.ComponentGroup;
import ambit2.core.groups.ContainerGroup;
import ambit2.core.groups.DataGroup;
import ambit2.core.groups.GenericGroup;
import ambit2.core.groups.GroupException;
import ambit2.core.groups.GroupNotFoundException;
import ambit2.core.groups.ISGroup;
import ambit2.core.groups.MonomerGroup;
import ambit2.core.groups.MultipleGroup;
import ambit2.core.groups.StructureRepeatingUnit;
import ambit2.core.groups.SuperAtom;
import ambit2.core.groups.SuppleAtomContainer;
import ambit2.core.groups.UnsupportedGroupOperation;

/**
 * Helper for MDL SGroups
 * @author nina
 *
 */
public class SGroupMDL2000Helper {
	protected static Logger logger = Logger.getLogger(SGroupMDL2000Helper.class.getName());
	public enum SGROUP_LINE {
		  /**
		   * Atom List [Query]
		   * <pre>
		   * M  ALS aaannn e 11112222333344445555
		   * </pre>
		   */
		  M__ALS   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  ALS"); }
			  protected String[] parse(String line) throws GroupException {
				  throw new GroupException("Not implemented "+this ); 
			  };			  
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }
		  },
		  /**
		   * Attachment point [RGroup]
		   * <pre>
		   * M  APOnn2 aaa vvv
		   * </pre>
		   */
		  M__APO   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  APO"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,2);
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }			  
		  },
		  /**
		   * Atom Attachment order [RGroup]
		   * <pre>
		   * M  AAL aaann2 111 v1v 222 v2v
		   * </pre>
		   */		  
		  M__AAL   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  AAL"); }
			  protected String[] parse(String line) throws GroupException {
				  throw new GroupException("Not implemented "+this); 
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }			  
		  },
		  /**
		   * Rgroup Label Location [RGroup]
		   * <pre>
		   * M  RGPnn8 aaa rrr
		   * </pre>
		   */		  
		  M__RGP   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  RGP"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8);
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  for (int i=1; i < params.length; i+=2) {
                      int atomno = Integer.parseInt(params[i])-1;
                      IAtom atom = atomcontainer.getAtom(atomno);
                      if (atom instanceof IPseudoAtom) {
                          ((IPseudoAtom)atom).setLabel("R"+params[i+1]);
                      } else throw new GroupException("Expected IPseudoAtom but found "+atom.getSymbol());
                  }
			  }			  
		  },
		  /**
		   * Rgroup Logic, unsatisfied sites, range of occurecne [RGroup]
		   * <pre>
		   * M  LOGnn1 rrr iii hhh ooo
		   * </pre>
		   */		  
		  M__LOG   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  LOG"); }
			  protected String[] parse(String line) throws GroupException {
				  throw new GroupException("Not implemented "+this); 
			  };	
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }			  
		  },		  
		  /**
		   * SGroup type
		   * <pre>
		   * M  STYnn8 sss ttt
		   * </pre>
		   */
		  M__STY   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  STY");} 
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8);
			  };
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException{
				  String[] params = parse(line);
				  for (int i=1; i < params.length;i+=2) 
				  try {
					ISGroup group = createGroup(Integer.parseInt(params[i]), 
							SGROUP_TYPE.valueOf(params[i+1]));
					if (group != null)
                        sgroups.put(group.getNumber(),group);
				  }	catch (Exception x) {
					  logger.log(Level.WARNING,name(),x);
				  }
			  }
		  },
		  //Sgroup subtype
		  M__SST   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SST"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8);
			  };
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new UnsupportedGroupOperation(this.toString());
			  }			  

		  },
		  //Sgroup labels
		  M__SLB   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SLB"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8);
			  };
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new UnsupportedGroupOperation(this.toString());
			  }			  

		  },
		  //SGroup connectivity
		  M__SCN   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SCN"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8);
			  };
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  
                  //first param is the number of sgroups
                  int n = Integer.parseInt(params[0]);
                  
                  
                  //atom numbers
                  int i =1;
                  while (i < params.length) {
                      ISGroup group = sgroups.get(Integer.parseInt(params[i]));
                      if (group == null) throw new GroupNotFoundException(params[1]);
                      i++;
                      group.setProperty(ISGroup.SGROUP_CONNECTIVITY, SGROUP_CONNECTIVITY.valueOf(params[i]));
                      i++;
                  }   
                  
			  }			  
		  },
		  //SGroup expansion
		  M__SDS   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SDS"); }
			  protected String[] parse(String line) throws GroupException {
				  throw new UnsupportedGroupOperation("Not implemented "+this); 
			  };
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new UnsupportedGroupOperation(this.toString());
			  }			  
		  },
		  //SGroup atom list
		  M__SAL   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SAL"); }
			  protected String[] parse(String line) throws GroupException {
				  return parseList(line, 15); 
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  //first param is an sgroup
				  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
				  if (group == null) throw new GroupNotFoundException(params[0]);
				  //atom numbers
				  for (int i=2; i < params.length;i++) {
					  int atomno = Integer.parseInt(params[i])-1;
                      atomcontainer.addGroupAtom(group,atomcontainer.getAtom(atomno));
				  }	  
                  group.finalizeAtomList(atomcontainer);
			  }			  
		  },
		  //SGroup bond list
		  M__SBL   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SBL"); }
			  protected String[] parse(String line) throws GroupException {
				  return parseList(line, 15); 
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  //first param is an sgroup
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
				  if (group == null) throw new GroupNotFoundException(params[0]);
				  //atom numbers
				  for (int i=2; i < params.length;i++) {
					  int bondno = Integer.parseInt(params[i])-1;
                      atomcontainer.addGroupBond(group,atomcontainer.getBond(bondno));
				  }	  
			  }			  
		  },
		  /**
		  * Multiple group parent atom list
		  <pre>
		  M  SPA sssn15 aaa 
		  aaa: Atoms in paradigmatic repeating unit of multiple group sss
		  </pre>
		  */
		  M__SPA   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SPA"); }
			  protected String[] parse(String line) throws GroupException {
				  return parseList(line, 15);
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  //first param is an sgroup
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
				  if (group == null) throw new GroupNotFoundException(params[0]);
				  if (!(group instanceof MultipleGroup)) throw new UnsupportedGroupOperation("MultipleGroup expected!");
				  //atom numbers
				  for (int i=2; i < params.length;i++) {
					  int atomno = Integer.parseInt(params[i])-1;
					  ((MultipleGroup)group).addParentAtom(atomcontainer.getAtom(atomno));
				  }	  
                  ((MultipleGroup)group).finalizeParentAtomList(atomcontainer);

			  }			  
		  },
		  /**
		   * SGroup subscript
		   * <pre>
		   * M  SMT sss m..
		   * Text of subscript Sgroup sss. (For multiple groups, m.. is the text representation of the multiple group multiplier. For superatoms, m... is the text of the superatom label.)
		   * </pre>
		   */ 
		  M__SMT   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SMT"); }
			  protected String[] parse(String line) throws GroupException {
				  String[] result = new String[2];
				  result[0] = line.substring(7,10).trim();
				  result[1] = line.substring(11).trim();
				  return result;
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
				  if (group == null) throw new GroupNotFoundException(params[0]);
				  group.setSubscript(params[1]);
			  }			  
		  },
		  /**
		   * SGroup correspondence
		   */
		  M__CRS   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  CRS"); }
			  protected String[] parse(String line) throws GroupException {
				  throw new GroupException("Not implemented "+this); 
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }			  
		  },
		  /**
		   * Sgroup display information (x,y - coordinates of bracket endpoints)
		   * <pre>
		   * M  SDI sssnn4 x1 y1 x2 y2
		   * </pre>
		   */
		  M__SDI   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SDI"); }
			  protected String[] parse(String line) throws GroupException {
				 return parseList(line, 4, 9); 
			  };
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  //first param is an sgroup
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
				  if (group == null) throw new GroupNotFoundException(params[0]);
				  //brackets
				  group.addBrackets(Double.parseDouble(params[2]),Double.parseDouble(params[3]),
						  			Double.parseDouble(params[4]),Double.parseDouble(params[5]));

			  }			  
		  },
		  /**
		   * Superatom Bond and Vector information
		   * <pre>
		   * M  SBV sss bb1 x1 y1
		   * bbb1 bond connecting to contracted superatom
		   * x1,y1 vector for bond bb1 connecting to contracted superatom sss
		   * </pre>
		   */
		  M__SBV   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SBV"); }
			  protected String[] parse(String line) throws GroupException {
				  final int[] pos = {11,11+3,11+3+10,11+3+10+10};
				  return parseByPosition(line, pos);
			  };			  
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  //TODO
				  //throw new GroupException(this.toString());
			  }
		  },

		  /**
		   * Data sgroup field description 
		   * <pre>
		   * M  SDT sss fff...fffgghhh...hhhiijjj
           * sss:
           * Index of data Sgroup
           * fff...fff:
           * 30 character field name (in MACCS-II no blanks, commas, or hyphens)
           * gg:
           * Field type (in MACCS-II F = formatted, N = numeric, T = text)
           * hhh...hhh
           * 20-character field units or format
           * ii:
           * Nonblank if data line is a query rather than Sgroup data, MQ = MACCS-II query, IQ = ISIS query, PQ = program name code query
           * jjj...:
           * Data query operator (blank for MACCS-II)
		   * </pre>
		   */
		  M__SDT   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SDT"); }
			  protected String[] parse(String line) throws GroupException {
				  final int[] pos = {11,11+30,11+30+2,11+30+2+20,11+30+2+20+2};
				  return parseByPosition(line, pos);
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
                  if (group == null) throw new GroupNotFoundException(params[0]);
                  if (group instanceof DataGroup) {
                      DataGroup.SGROUP_FIELD[] prm = DataGroup.SGROUP_FIELD.values();

                      ((DataGroup)group).setName(params[1]);

                      for (int i=2; i < params.length;i++) {
                          group.setProperty(prm[i-2],params[i]);
                      }
                  } else new GroupException("Excepting DataGroup, found "+group.getClass().getName());
			  }			  
		  },
		  
		  /**
		   * Data sgroup display information
		   * <pre>
		   * M  SDD sss xxxxx.xxxxyyyyy.yyyy eeefgh i jjjkkk ll m noo
            sss:
            Index of data Sgroup
            x,y:
            Coordinates (2F10.4)
            eee:
            (Reserved for future use)
            f:
            Data display, A = attached, D = detached
            g:
            Absolute, relative placement, A = absolute, R = relative
            h:
            Display units, blank = no units displayed, U = display units
            i:
            (Reserved for future use)
            jjj:
            Number of characters to display (1...999 or ALL)
            kkk:
            Number of lines to display (unused, always 1)
            ll:
            (Reserved for future use)
            m:
            Tag character for tagged detached display (if non-blank)
            n:
            Data display DASP position (1...9). (MACCS-II only)
            oo:
            (Reserved for future use)
		   * </pre>
		   */
		  M__SDD   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SDD"); }
			  protected String[] parse(String line) throws GroupException {
				  final int[] pos = {11,21,31,35,36,37,38,40,44,47,50,52,54,56};
				  return parseByPosition(line, pos);
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
                  if (group == null) throw new GroupNotFoundException(params[0]);
                  if (group instanceof DataGroup) {
                      DataGroup.DGROUP_DISPLAY[] display = DataGroup.DGROUP_DISPLAY.values();
                      for (int i=1; (i < params.length) && ((i-1) < display.length) ;i++) {
                          group.setProperty(display[i-1],params[i]);
                      }    
                  } else new GroupException("Excepting DataGroup, found "+group.getClass().getName());
			  }			  
		  },
          M__SED   { 
              public boolean isSgroupLine(String line) { return line.startsWith("M  SED"); }
              protected String[] parse(String line) throws GroupException {
                  String[] results = new String[2];
                  results[0] = line.substring(7,10).trim();
                  results[1] = line.substring(11).trim();
                  return results; 
              };        
              public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
                  String[] params = parse(line);
                  ISGroup group = sgroups.get(Integer.parseInt(params[0]));
                  if (group == null) throw new GroupNotFoundException(params[0]);
                  if (group instanceof DataGroup) {
                     if (DataGroup.MRV_MULTICENTER.equals(((DataGroup)group).getName())) {
                         ((DataGroup)group).setMulticenter(atomcontainer.getAtom(
                                 Integer.parseInt(params[1])-1
                                 ));
                     }
                  } else new GroupException("Excepting DataGroup, found "+group.getClass().getName());

              }           
          },          
		  /**
		   * 3D Feature properties
		   */
		  M__$3D   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  $3D"); }
			  protected String[] parse(String line) throws GroupException {
				  throw new GroupException("Not implemented "+this); 
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }			  
		  },
		  
		  /**
		   * Data sgroup Data 
		   * <pre>
		   * M  SCD sss d
		   * M  SED sss d
		   * d Line of data for data Sroup sss (69 chars per line, columns 12-80)
		   * </pre>
		   */
		  M__SCD   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SCD"); }
			  protected String[] parse(String line) throws GroupException {
				  String[] results = new String[2];
				  results[0] = line.substring(7,10).trim();
				  results[1] = line.substring(11).trim();
				  return results;
			  };	
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
				  throw new GroupException(this.toString());
			  }			  
		  },

		  
		  /**
		   * Sgroup hierarchy information
		   * <pre>
		   * M  SPLnn8 ccc ppp
		   * ccc Sgroup index of the child Sgroup
		   * ppp Sgroup index of the parent Sgroup
		   * 
		   * </pre>
		   */
		  M__SPL   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SPL"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8);
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  int no = Integer.parseInt(params[0]);
                  for (int i=1; i< params.length;i+=2) {
                      ISGroup group = sgroups.get(Integer.parseInt(params[i]));
                      if (group == null) throw new GroupNotFoundException(params[i]);
                      ISGroup parent = sgroups.get(Integer.parseInt(params[i+1]));
                      if (parent == null) throw new GroupNotFoundException(params[i+1]);
                      
                      if (parent instanceof ContainerGroup) {
                          ((ContainerGroup)parent).getComponents().add(group);
                      } else new GroupException("Excepting MixtureGroup, found "+parent.getClass().getName());
                  }
			  }			  
		  },
		  
		  /**
		   * Sgroup component numbers
		   * <pre>
		   * M  SNCnn8 sss ooo ...
		   * 
		   * </pre>
		   */
		  M__SNC   { 
			  public boolean isSgroupLine(String line) { return line.startsWith("M  SNC"); }
			  protected String[] parse(String line) throws GroupException {
				  return parsePairs(line,8); 
			  };		
			  public void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException {
				  String[] params = parse(line);
                  int no = Integer.parseInt(params[0]);
                  for (int i=1; i< params.length;i+=2) {
                      ISGroup group = sgroups.get(Integer.parseInt(params[i]));
                      if (group == null) throw new GroupNotFoundException(params[i]);
                      int order = Integer.parseInt(params[i+1]);
                      if (group instanceof ComponentGroup) {
                          ((ComponentGroup)group).setOrder(order);
                      } else new GroupException("Excepting ComponentGroup, found "+group.getClass().getName());
                  }
			  }			  
		  },				  
		  ;

		  public abstract boolean isSgroupLine(String line);
		  protected abstract String[] parse(String line) throws GroupException;
		  public abstract void updateSGroups(String line, SuppleAtomContainer atomcontainer, Hashtable<Integer,ISGroup> sgroups) throws GroupException;	  
		  
		  protected String[] parsePairs(String line, int maxEntries) throws GroupException {
			  int nn8 = Integer.parseInt(line.substring(6,9).trim());
			  if (nn8 > maxEntries*2) throw new GroupException("Max entries ("+maxEntries+") exceeded ("+nn8+")");
			  String[] result = new String[nn8*2+1];
			  result[0] = line.substring(6,9).trim();
			  int position = 9;
			  for (int i=1; i < nn8*2+1; i++) {
				  position++;
				  result[i] = line.substring(position,position+3).trim();
				  position+=3;
			  }	  
			  return result;
		  };
		  protected String[] parseList(String line,int maxEntries) throws GroupException {
			  return parseList(line, maxEntries, 3);
		  };			  
		  
		  protected String[] parseList(String line,int maxEntries,int entryLength) throws GroupException {
			  String sgroupNo = line.substring(7,10).trim();
			  String nn = line.substring(10,13).trim();
			  int nn15 = Integer.parseInt(nn);
			  if (nn15 > maxEntries) throw new GroupException("Max entries ("+maxEntries+") exceeded ("+nn15+")");
			  String[] result = new String[nn15+2];
			  result[0] = sgroupNo;
			  result[1] = nn;
			  int position = 13;
			  for (int i=2; i < nn15+2; i++) {
				  position++;
				  result[i] = line.substring(position,position+entryLength).trim();
				  position+=entryLength;
			  }	  
			  return result; 
		  };			  
		  protected String[] parseByPosition(String line, int[] pos) throws GroupException {
			  String[] result = new String[pos.length+1];
			  result[0] = line.substring(7,10).trim();
			  for (int i=0; i < pos.length;i++) {
				  if (pos[i]>line.length()) break;
				  if (i==(pos.length-1))
					  result[i+1] = line.substring(pos[i]).trim();
				  else
					  if (pos[i+1]>line.length()) {
						  result[i+1] = line.substring(pos[i]).trim();
						  break;
					  } else
						  result[i+1] = line.substring(pos[i],pos[i+1]).trim();
			  }	  
			  return result;
		  };				  

		  
		  
		}

	public enum SGROUP_TYPE {
		/**
		 * Superatom
		 */
		SUP,
		/**
		 * Multiple group
		 */
		MUL,
		/**
		 * Structural repeating unit
		 */
		SRU,
		/**
		 * Monomer
		 */
		MON,
		/**
		 * ?
		 */
		MER,
		/**
		 * Copolymer
		 */
		COP,
		/**
		 * CRO
		 */
		CRO,
		/**
		 * MOD
		 */
		MOD,
		/**
		 * graft
		 */
		GRA,
		/**
		 * Component
		 */
		COM,
		/**
		 * mixture
		 */
		MIX,
		/**
		 * formulation
		 */
		FOR,
		/**
		 * Data sgroup
		 */
		DAT,
		/**
		 * Any polymer
		 */
		ANY,
		/**
		 * Generic
		 */
		GEN
	}
	
	public enum SGROUP_POLYMER_TYPE {
		/** Alternating */
		ALT,
		/** Random */ 
		RAN,
		/** Block */
		BLO
	}
    public enum SGROUP_CONNECTIVITY {
        /** head-to-head */
        HH,
        /** head-to-tail*/ 
        HT,
        /** either unknown. */
        EU
    }
        
	/**
	 * Extracts first 6 chars, replaces spaces with underscore '_' and calls SGROUP_LINE.valueof method.
	 * @param line
	 * @return {@link SGROUP_LINE}
	 * @throws IllegalArgumentException if the resulting string is not defined as enum constant
	 */
	public static SGROUP_LINE getValue(String line) throws IllegalArgumentException {
		return SGROUP_LINE.valueOf(line.substring(0,6).replace(' ', '_'));
	}
		
	public static ISGroup createGroup(int number, SGROUP_TYPE stype) throws UnsupportedGroupOperation {
 		  	switch (stype) {
			case SUP: {
				return new SuperAtom(stype.toString(),number);
			}
			case GEN: {
				return new GenericGroup(stype.toString(),number);
			}
			case SRU: {
				return new StructureRepeatingUnit(stype.toString(),number);
			}

			case MUL: {
				return new MultipleGroup(stype.toString(),number);
			}
            case MON: {
                return new MonomerGroup(stype.toString(),number);
            }
            case COM: {
                return new ComponentGroup(stype.toString(),number);
            }       
            case MIX: {
                return new ContainerGroup(stype.toString(),number);
            }   
            case FOR: {
                return new ContainerGroup(stype.toString(),number,ContainerGroup.SGROUP_MIXTURE.FORMULATION);
            }
            case COP: {
                return new ContainerGroup(stype.toString(),number,ContainerGroup.SGROUP_MIXTURE.COPOLYMER);
            }                          
            case DAT: {
                return new DataGroup(stype.toString(),number);
            }                               
			default:
				throw new UnsupportedGroupOperation(stype.toString());
			}		
	}
}
