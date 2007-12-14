package ambit.database.query;

import java.util.Collection;

import ambit.data.AmbitList;
import ambit.data.experiment.Study;
import ambit.data.experiment.StudyList;
import ambit.data.molecule.SourceDataset;

/**
 * Query for structures having experimental data, given specific study results.
 * Used by {@link ambit.database.processors.ExperimentSearchProcessor}, 
 * {@link ambit.database.processors.ReadExperimentsProcessor}, {@link ambit.database.search.DbSearchExperiments},
 * {@link ambit.ui.actions.search.DbExperimentsSearchAction}.<br>
 * Visualization by {@link ambit.ui.query.ExperimentsQueryPanel} and {@link ambit.ui.query.ExperimentsQueryTableModel}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class ExperimentQuery extends AmbitList implements IDBQueryList {
    protected boolean combineWithAND = true;
    protected StudyList studyList;
	public ExperimentQuery() {
		super();
		studyList = new StudyList();
	}

	public ExperimentQuery(int initialCapacity) {
		super(initialCapacity);
		studyList = new StudyList();
	}

	public ExperimentQuery(Collection c) {
		super(c);
		studyList = new StudyList();
	}
	public TemplateFieldQuery getFieldQuery(int i) {
		return (TemplateFieldQuery) getItem(i); 
	}
	public void setEnabled(int index,boolean value)  {
	    ((TemplateFieldQuery) getItem(index)).setEnabled(value);
	    setModified(true);
	    
	}
	public void setCondition(int index,String value)  {
	    ((TemplateFieldQuery) getItem(index)).setCondition(value);
	    setModified(true);
	}
	public void setValue(int index,Object value)  {
	    ((TemplateFieldQuery) getItem(index)).setValue(value);
	    setModified(true);
	}
	public void setMinValue(int index,double value)  {
	    ((TemplateFieldQuery) getItem(index)).setMinValue(value);
	    setModified(true);
	}		
	public void setMaxValue(int index,double value)  {
	    ((TemplateFieldQuery) getItem(index)).setMaxValue(value);
	    setModified(true);
	}			
	public String toString() {
		StringBuffer b = new StringBuffer();
		for (int i= 0;i < size();i++) {
			TemplateFieldQuery query = getFieldQuery(i);
			b.append(query.toString());
			b.append("\n");	
		}
		return b.toString();
	}
	
	protected String toSQLCombineWithAND(SourceDataset srcDataset,int page, int pagesize) {
		StringBuffer b = new StringBuffer();
		b.append("select experiment.idstructure,experiment.idexperiment,idref,idstudy,");
		int k =0;
		for (int i= 0;i < size();i++) {
			TemplateFieldQuery query = getFieldQuery(i);
			if (query.isEnabled()) {
			    
			    if (k==0) {
			        b.append("D"); b.append((i));
			        b.append(".idexperiment");
			    }
		        b.append(",D"); b.append((i)); 
		        if (query.isNumeric()) b.append(".value_num");
		        else b.append(".value");
			    k++;
			}	
		}
		//b.append("\ncasno,name,idsubstance,structure.idstructure,");
		String[][] command={{"",""},{"\njoin "," using(idexperiment)"}};
		int commandIndex = 0;
		String[] where_command= {"\nwhere ","\nand "};
				
		b.append("\nfrom\n");		
		
		StringBuffer where = new StringBuffer();
		
		for (int i= 0;i < size();i++) {
			

			TemplateFieldQuery query = getFieldQuery(i);
			if (query.isEnabled()) {
				b.append(command[commandIndex][0]);
				b.append(query.getSQLTable("D"+(i)));
				b.append(command[commandIndex][1]);
		
				where.append(where_command[commandIndex]);
				where.append(query.id2SQL("D"+(i)+"."));
				
				commandIndex = 1;
			}
		}
		
		b.append("\njoin experiment using(idexperiment)");
		//b.append("\njoin study using(idstudy)");
		
		if (srcDataset != null) {
			b.append("\njoin struc_dataset using(idstructure)");
			where.append(where_command[commandIndex]);
			where.append("id_srcdataset=");
			where.append(srcDataset.getId());
		}
		
		b.append(where.toString());
		b.append("\norder by idstructure");
		b.append("\nlimit ");
		b.append(page*pagesize);
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
    public String toSQL(SourceDataset srcDataset,int page,int pagesize) {
        int newLimit = pagesize;
        boolean hasQuery = false;
    	for (int i= size()-1; i>=0;i--) {
			TemplateFieldQuery query = getFieldQuery(i);
			if (query.isEnabled()) {
			    hasQuery = true;
			}
		}	    
    	if (!hasQuery) return "";

        if (combineWithAND) return toSQLCombineWithAND(srcDataset,page,pagesize);
        else return toSQLCombineWithOR(srcDataset,page,pagesize);
    }	
	protected String toSQLCombineWithOR(SourceDataset srcDataset,int page, int pagesize) {
		StringBuffer b = new StringBuffer();
		b.append("select experiment.idstructure,experiment.idexperiment,idref from " );
		
		String command = "(";
		for (int i= 0;i < size();i++) {
  		 //join (select iddescriptor,dvalues.idstructure,value as eHomo from dvalues where iddescriptor=2 and value < -4) as t3  using(idstructure)
			TemplateFieldQuery query = getFieldQuery(i);
			if (query.isEnabled()) {
				b.append(command);
				b.append('\n');
				b.append(query.toSQL("F"+(i)));
				b.append('\n');
				command = "union";
			}
		}
		b.append(") as T\n");
		b.append("join experiment using(idexperiment)\n");
		if (srcDataset != null) {
			b.append("\njoin struc_dataset using(idstructure)");
			b.append("\nwhere ");
			b.append("id_srcdataset=");
			b.append(srcDataset.getId());
		}

		b.append("\nlimit ");
		b.append(page*pagesize);
		b.append(",");
		b.append(pagesize);

		return b.toString();
	}

	public Study getStudy() {
		return (Study)studyList.getSelectedItem();
	}

	public void setStudy(Study study) {
	}

	public StudyList getStudyList() {
		return studyList;
	}

	public void setStudyList(StudyList studyList) {
		this.studyList = studyList;
	}
    
}
