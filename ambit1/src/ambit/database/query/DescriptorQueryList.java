package ambit.database.query;

import ambit.data.descriptors.DescriptorDefinition;
import ambit.data.descriptors.DescriptorsDefinitionList;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;

/**
 * A complex descriptor query. <br>
 * Used by {@link ambit.database.search.DbSearchDescriptors}, {@link ambit.database.processors.ReadDescriptorsProcessor}.<br>
 * Visualization by {@link ambit.ui.query.DescriptorQueryPanel}, {@link ambit.ui.query.DescriptorQueryTableModel}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */

public class DescriptorQueryList extends DescriptorsDefinitionList implements IDBQueryList {
    protected boolean combineWithAND = true;
	public DescriptorQueryList() {
		super();
	}
	public DescriptorQuery getDescriptorQuery(int i) {
		return (DescriptorQuery) getItem(i); 
	}
	public void setEnabled(int index,boolean value)  {
	    ((DescriptorQuery) getItem(index)).setEnabled(value);
	    setModified(true);
	    
	}
/*
 * 	 * -------------+------------+-----------------+--------------------------------------------------------+
 min(value)  | max(value) | avg(value)      | name                                                   |
-------------+------------+-----------------+--------------------------------------------------------+
    -12.5650 |    65.4530 |      2.64113871 | org.openscience.cdk.qsar.XLogPDescriptor               |
    -13.5077 |    -2.8553 |     -9.59721733 | eHOMO                                                  |
     32.0450 |   697.6130 |    221.99612545 | MOLECULAR WEIGHT                                       |
      2.8553 |    13.5077 |      9.53785534 | IONIZATION POTENTIAL                                   |
     -6.7885 |     3.7066 |     -0.18727368 | eLUMO                                                  |
 -55199.4141 | -1055.5260 | -15813.83681880 | ELECTRONIC ENERGY                                      |
    587.0295 | 47383.2969 |  13102.93620308 | CORE-CORE REPULSION                                    |
  -9105.2666 |  -468.4965 |  -2710.90062956 | TOTAL ENERGY                                           |
   -715.3523 |   431.1553 |    -26.72242988 | FINAL HEAT OF FORMATION                                |
      2.0000 |   496.0000 |    122.11469928 | org.openscience.cdk.qsar.ZagrebIndexDescriptor         |
      0.0000 |    68.0000 |      8.67060397 | org.openscience.cdk.qsar.AromaticBondsCountDescriptor  |
      0.0000 |    60.0000 |      8.48202973 | org.openscience.cdk.qsar.AromaticAtomsCountDescriptor  |
      0.0000 |    63.0000 |      7.04993879 | org.openscience.cdk.qsar.RotatableBondsCountDescriptor |
      0.0000 |     4.0000 |      0.39199849 | org.openscience.cdk.qsar.RuleOfFiveDescriptor          |
	 *
	 */
 
	public void loadDefault() {
		
	 	DescriptorQuery d1 = new DescriptorQuery("org.openscience.cdk.qsar.XLogPDescriptor",ReferenceFactory.createEmptyReference());
	 	d1.setMinValue(2); d1.setMaxValue(5); d1.setValue(2.5);
	 	d1.setEnabled(true);
	 	addItem(d1);
	 	DescriptorQuery d4 = new DescriptorQuery("eHOMO",ReferenceFactory.createEmptyReference());
	 	d4.setMinValue(-10.5); d4.setMaxValue(-10.1); d4.setValue(-9);
	 	addItem(d4);
	 	DescriptorQuery d5 = new DescriptorQuery("eLUMO",ReferenceFactory.createEmptyReference());
	 	d5.setMinValue(-3); d5.setMaxValue(-2.8); d5.setValue(-2.8);
	 	addItem(d5);
	 	DescriptorQuery d6 = new DescriptorQuery("FINAL HEAT OF FORMATION",ReferenceFactory.createEmptyReference());
	 	d6.setCondition(">");
	 	d6.setMinValue(-500); d6.setMaxValue(-400); d6.setValue(0);
	 	addItem(d6);
	 	DescriptorQuery d7 = new DescriptorQuery("MOLECULAR WEIGHT",ReferenceFactory.createEmptyReference());
	 	d6.setCondition("<");
	 	d7.setMinValue(100); d7.setMaxValue(200); d7.setValue(300);
	 	addItem(d7);
	 	DescriptorQuery d9 = new DescriptorQuery("ambit.data.descriptors.SpherosityDescriptor",ReferenceFactory.createEmptyReference());
	 	d9.setMinValue(0); d9.setMaxValue(1); d9.setValue(0.1);
	 	d9.setCondition("<");
	 	addItem(d9);
	 	
	 	DescriptorQuery d2 = new DescriptorQuery("org.openscience.cdk.qsar.ZagrebIndexDescriptor",ReferenceFactory.createEmptyReference());
	 	d2.setMinValue(200); d2.setMaxValue(250); d2.setValue(100);
	 	addItem(d2);
	 	DescriptorQuery d3 = new DescriptorQuery("org.openscience.cdk.qsar.AromaticBondsCountDescriptor",ReferenceFactory.createEmptyReference());
	 	d2.setMinValue(12); d3.setMaxValue(18); d3.setValue(6);
	 	addItem(d3);	 	
	 	DescriptorQuery d8 = new DescriptorQuery("org.openscience.cdk.qsar.RuleOfFiveDescriptor",ReferenceFactory.createEmptyReference());
	 	d8.setMinValue(0); d8.setMaxValue(4); d8.setValue(0);
	 	addItem(d8);
 	
	 	DescriptorQuery q = new DistanceQuery("C","N",11);
		q.setCondition("between");	q.setMaxValue(15);		q.setMinValue(13); q.setValue(11); 
	 	addItem(q);	 		 	
	}
	
	public String getPrettyName(int index) {
	    DescriptorDefinition d = getDescriptorDef(index);
	    if (d instanceof DistanceQuery) {
	        String [] a = ((DistanceQuery) d).getAtoms();
	        return "Distance between atoms " + a[0] + " and " + a[1]; 
	    } else {
		    String n = ((DescriptorQuery) getItem(index)).getName();
	    	int p = n.lastIndexOf('.');
	    	if ((p == -1) || (p==n.length())) return n;
	    	else return  n.substring(p+1);
	    }
	}
	public void setCondition(int index,String value)  {
	    ((DescriptorQuery) getItem(index)).setCondition(value);
	    setModified(true);
	}
	public void setValue(int index,double value)  {
	    ((DescriptorQuery) getItem(index)).setValue(value);
	    setModified(true);
	}
	public void setMinValue(int index,double value)  {
	    ((DescriptorQuery) getItem(index)).setMinValue(value);
	    setModified(true);
	}		
	public void setMaxValue(int index,double value)  {
	    ((DescriptorQuery) getItem(index)).setMaxValue(value);
	    setModified(true);
	}			
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i= 0;i < size();i++) {
			DescriptorQuery query = getDescriptorQuery(i);
			b.append(query.toString());
			b.append("\n");	
		}
		return b.toString();
	}
	/*
	 * 
	 * select structure.idstructure,uncompress(structure) from 
	 * select structure.idstructure from
(
select dvalues.idstructure from dvalues where  iddescriptor=1 and value between 2 and 5 
union
select dvalues.idstructure from dvalues where  iddescriptor=2 and value between -10.5 and -10.1 
union
select idstructure from atom_structure,atom_distance where
distance between 11 and 13 and  atom1="C" and atom2="N" and atom_structure.iddistance=atom_distance.iddistance
) as T
join structure using(idstructure) limit 1000

	 */
	protected String toSQLCombineWithOR(SourceDataset srcDataset,int page, int pagesize) {
		StringBuffer b = new StringBuffer();
		b.append("select ");
		/*
		for (int i= 0;i < size();i++) {
			DescriptorQuery query = getDescriptorQuery(i);
			if (query.isEnabled()) { 	
				b.append("F"); b.append((i)); b.append(',');
			}	
		}
		*/
		//b.append("\ncasno,name,idsubstance,structure.idstructure,");
		b.append("\nidsubstance,structure.idstructure");		
		b.append("\nfrom\n");
		
		String command = "(";
		for (int i= 0;i < size();i++) {
  		 //join (select iddescriptor,dvalues.idstructure,value as eHomo from dvalues where iddescriptor=2 and value < -4) as t3  using(idstructure)
			DescriptorQuery query = getDescriptorQuery(i);
			if (query.isEnabled()) {
				b.append(command);
				b.append('\n');
				b.append(query.toSQL("F"+(i)));
				b.append('\n');
				command = "union";
			}
		}
		b.append(") as T\n");
		b.append("join structure using(idstructure)\n");
		if (srcDataset != null) {
			b.append("\njoin struc_dataset using(idstructure)");
			b.append("\nwhere ");
			b.append("id_srcdataset=");
			b.append(srcDataset.getId());
		}
				
		b.append("\nlimit ");
		b.append(page);
		b.append(",");
		b.append(pagesize);
		
		//b.append("left join cas using(idstructure)\n");
		//b.append("left join name using(idstructure)\n");
		return b.toString();
	}
/*
 * select d1.idstructure, 
d1.value, d2.value,d3.value,d4.value,d5.value,d6.value ,distance
from dvalues as d1
join dvalues as d2 using(idstructure) 
join dvalues as d3 using(idstructure) 
join dvalues as d4 using(idstructure) 
join dvalues as d5 using(idstructure) 
join dvalues as d6 using(idstructure) 
join atom_structure as s using(idstructure)
join atom_distance using(iddistance)
where 
d1.iddescriptor=1 and (d1.value between 0 and 2) and
d2.iddescriptor=2 and (d2.value between -11 and -10) and
d3.iddescriptor=3 and (d3.value between 200 and 300) and
d4.iddescriptor=4 and (d4.value between 10 and 11) and
d5.iddescriptor=5 and (d5.value between -2 and -1) and
d6.iddescriptor=6  and (d6.value between -10000 and -9000) and
atom1="C" and atom2="N"  and distance between 2 and 3
order by idstructure
limit 30;

 */	
	protected String toSQLCombineWithAND(SourceDataset srcDataset, int page, int pagesize) {
		StringBuffer b = new StringBuffer();
		b.append("select ");
		int k =0;
		for (int i= 0;i < size();i++) {
			DescriptorQuery query = getDescriptorQuery(i);
			if (query.isEnabled()) {
			    
			    if (k==0) {
			        b.append("D"); b.append((i));
			        b.append(".idstructure");
			    }
			    if (query instanceof DistanceQuery) b.append(",distance");
			    else {
			        b.append(",D"); b.append((i)); b.append(".value");
			    }
			    k++;
			}	
		}
		//b.append("\ncasno,name,idsubstance,structure.idstructure,");
		String[][] command={{"",""},{"\njoin "," using(idstructure)"}};
		int commandIndex = 0;
		String[] where_command= {"\nwhere ","\nand "};
				
		b.append("\nfrom\n");		
		
		StringBuffer where = new StringBuffer();
		
		for (int i= 0;i < size();i++) {
			
  		 //join (select iddescriptor,dvalues.idstructure,value as eHomo from dvalues where iddescriptor=2 and value < -4) as t3  using(idstructure)
			DescriptorQuery query = getDescriptorQuery(i);
			if (query.isEnabled()) {
				b.append(command[commandIndex][0]);
				b.append(query.getSQLTable("D"+(i)));
				b.append(command[commandIndex][1]);
				if (query instanceof DistanceQuery)
				    b.append("\njoin atom_distance using(iddistance)");
		
				where.append(where_command[commandIndex]);
				where.append(query.id2SQL("D"+(i)+"."));
				
				commandIndex = 1;
			}
		}
		
		if (srcDataset != null) {
			b.append("\njoin struc_dataset using(idstructure)");
			where.append(where_command[commandIndex]);
			where.append("id_srcdataset=");
			where.append(srcDataset.getId());
		}
		
		b.append(where.toString());
		b.append("\norder by idstructure");
		b.append("\nlimit ");
		b.append(page);
		b.append(",");
		b.append(pagesize);
		return b.toString();
	}
    public synchronized boolean isCombineWithAND() {
        return combineWithAND;
    }
    public synchronized void setCombineWithAND(boolean combineWithAND) {
        this.combineWithAND = combineWithAND;
    }
    public String toSQL(SourceDataset srcDataset,int page, int pagesize) {
        int newLimit = pagesize;
        boolean hasQuery = false;
    	for (int i= size()-1; i>=0;i--) {
			DescriptorQuery query = getDescriptorQuery(i);
			if (query.isEnabled()) {
			    hasQuery = true;
			    if (query instanceof DistanceQuery) {
			        newLimit = pagesize*2;
			    }
			}
		}	    
    	if (!hasQuery) return "";
        if (combineWithAND) return toSQLCombineWithAND(srcDataset,page,newLimit);
        else return toSQLCombineWithOR(srcDataset,page,newLimit);
    }
}
