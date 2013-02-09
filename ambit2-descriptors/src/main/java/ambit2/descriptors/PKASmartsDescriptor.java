/* PKASmartsDescriptor.java
 * Author: Nina Jeliazkova
 * Date: Oct 3, 2008 
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

package ambit2.descriptors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;
import ambit2.smarts.query.SMARTSException;

/**
 * pKa calculation 
 * <p>
 * Adam C. Lee, Jing-yu Yu, and Gordon M. Crippen, pKa Prediction of Monoprotic Small Molecules the SMARTS Way
 * <p> 
 * http://pubs.acs.org/cgi-bin/abstract.cgi/jcisd8/asap/abs/ci8001815.html
 * @author nina
 *
 */
public class PKASmartsDescriptor implements IMolecularDescriptor {
	protected static Logger logger = Logger.getLogger(PKASmartsDescriptor.class.getName());
    public static String[] title= {"pKa-SMARTS"};
    protected Hashtable<Integer, PKANode> tree;
    protected PKANode root;
    
    
    public PKASmartsDescriptor() {
        try {
            root = initialize();
        } catch (Exception x) {
            root = null;
            logger.log(Level.WARNING,x.getMessage(),x);
        }
    }
    public DescriptorValue calculate(IAtomContainer arg0)  {
        try {
        	ArrayList<String> trace = new ArrayList<String>();
            PKANode node = traverse(arg0, root,trace);

            return new DescriptorValue(getSpecification(), getParameterNames(), 
                    getParameters(),
                    new VerboseDescriptorResult<String,DoubleResult>(new DoubleResult(node.getPka()),trace.toString()),
                    title);        
            
        } catch (Exception x) {
            return new DescriptorValue(getSpecification(), getParameterNames(), 
                    getParameters(),
                    null,
                    title,x);   
        }
    }

    public synchronized Hashtable<Integer, PKANode> getTree() {
        return tree;
    }
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0);
    }

    public String[] getParameterNames() {
        return null;
    }

    public Object getParameterType(String arg0) {
        return null;
    }

    public Object[] getParameters() {
        return null;
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
        		String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"pkaSMARTS"),
        		this.getClass().getName(),
                "$Id: PKASmartsDescriptor.java,v 0.1 2008/10/03 21:20:00 Nina Jeliazkova Exp $",
                "http://pubs.acs.org/doi/abs/10.1021/ci8001815");
    }

    public void setParameters(Object[] arg0) throws CDKException {

    }

    protected PKANode traverse(IAtomContainer ac, PKANode node, ArrayList<String> trace) throws SMARTSException {
    	
    	if (node.isTerminal()) {
    		return node;
    	} else {
	    	boolean results[] = new boolean[] {Boolean.TRUE,Boolean.FALSE};
	    	for (boolean result : results) {
	    		PKANode next = node.getNodeNo(result);
	    		if (next.find(ac) == next.isPresent())  {
	    			trace.add(Integer.toString(next.getId())+((next.isPresent()) ? 'Y' : 'N'));
	    			if (next.isTerminal()) {
	    				return next;
	    			} else {
	    				return traverse(ac, next,trace);
	    			}	
	    		}	
	    	}
    	}
    	return null;
    }
    protected PKANode initialize() throws IOException {
        InputStream stream = PKASmartsDescriptor.class.getClassLoader().getResourceAsStream("ambit2/descriptors/pkatree.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        int record = 0;
        tree = new Hashtable<Integer, PKANode>();
        PKANode root = null;
        
        while ((line = reader.readLine()) != null) {
        	line = line.trim();
        	if ("".equals(line)) continue;
            if (record>=9) {
            	
                StringTokenizer st = new StringTokenizer(line, ",");
                int column =0;
                PKANode node = new PKANode();
                
                 while(st.hasMoreTokens()){
                     String s = st.nextToken();

                     //#node,#parent,children,FP,SMARTS,Y/N,pKa_cal,pKa_range
                     //1,0,1,0,,100,5.9131093,17.32
                     //2,1,1,1001,[#G6H]C(=O),1,3.6849957,5.9569998
                     //3,1,1,1001,[#G6H]C(=O),0,7.206913,17.32
                     switch (column) {
                     case 0: {node.setId(Integer.parseInt(s)); break;}
                     case 1: {node.setParent(Integer.parseInt(s)); break;}
                     case 2: {node.setTerminal(Integer.parseInt(s)==0); break;}
                     case 3: break;
                     case 4: {
                    	 //quick fix for MOE incompatibilities
                    	 s = s.replace("[Ov2]","[O;v2]");
                    	 s = s.replace("[Av1]","[A;v1]");
                    	 node.setSmarts(s); break;
                    	 }
                     case 5: {
                         int r = Integer.parseInt(s);
                         if (r == 100) root = node;
                         else
                             node.setPresent(r==1); break;
                         }
                     case 6: {node.setPka(Double.parseDouble(s)); break;}
                     case 7: {node.setPka_range(Double.parseDouble(s)); break;}                    
                     }
                     column++;
                 }

                 tree.put(node.getId(),node);
            }
            record++;
        }
        stream.close();
        
        Enumeration<Integer> e = tree.keys();
        while (e.hasMoreElements()) {
            Integer id = e.nextElement();
            PKANode node = tree.get(id);
            
            PKANode parent = tree.get(node.getParent());
            
            if (parent != null)
            	parent.setNode(node.isPresent(), node);
            
        }
        return root;
    }
	public String[] getDescriptorNames() {
		return title;
	}
}

