/* ExperimentConditionsQuery.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-3 
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

package ambit.database.query;

import java.util.Collection;

import ambit.data.molecule.SourceDataset;



/**
 * Query for structures having experimental data, given specific study conditions.
 * Visualization by {@link ambit.ui.query.ExperimentsQueryPanel} and {@link ambit.ui.query.DbQueryOptionsPanel}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-3
 */
public class ExperimentConditionsQuery extends ExperimentQuery {

    /**
     * 
     */
    public ExperimentConditionsQuery() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param initialCapacity
     */
    public ExperimentConditionsQuery(int initialCapacity) {
        super(initialCapacity);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param c
     */
    public ExperimentConditionsQuery(Collection c) {
        super(c);
        // TODO Auto-generated constructor stub
    }
	protected String toSQLCombineWithAND(SourceDataset srcDataset,int page, int pagesize) {
		StringBuffer b = new StringBuffer();
		b.append("select idstructure,experiment.idexperiment,idref,study.idstudy,study.name as StudyName ,");
		int k =0;
		for (int i= 0;i < size();i++) {
			TemplateFieldQuery query = getFieldQuery(i);
			if (query.isEnabled()) {
			    
			    if (k==0) {
			        b.append("D"); b.append((i));
			        b.append(".id_fieldname");
			    }
		        b.append(",D"); b.append((i)); 
		        b.append(".value");
			    k++;
			}	
		}
		//b.append("\ncasno,name,idsubstance,structure.idstructure,");
		String[][] command={{"\njoin "," using(idstudy)"},{"\njoin "," using(idstudy)"}};
		int commandIndex = 0;
		String[] where_command= {"\nwhere ","\nand "};
				
		b.append("\nfrom experiment \n");		
		
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
		
		b.append("\njoin study using(idstudy)");
		b.append(where.toString());
		b.append("\norder by idstructure");
		b.append("\nlimit ");
		b.append(page);
		b.append(',');
		b.append(pagesize);
		return b.toString();
	}
	protected String toSQLCombineWithOR(SourceDataset srcDataset,int page,int pagesize) {
		StringBuffer b = new StringBuffer();
		b.append("select structure.idstructure,experiment.idexperiment,idref,casno from " );
		
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
		b.append("join structure using(idstructure)\n");
		b.append("left join cas using(idstructure)\n");
		b.append("limit ");
		b.append(page);
		b.append(',');
		b.append(pagesize);
		//b.append("left join cas using(idstructure)\n");
		//b.append("left join name using(idstructure)\n");
		return b.toString();
	}
	
}
