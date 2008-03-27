/* StudyListModel.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-3 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  
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

package ambit2.ui.data.experiment;

import ambit2.data.experiment.Study;
import ambit2.data.experiment.StudyList;
import ambit2.ui.data.AbstractPropertyTableModel;

/**
 *  UI for {@link ambit2.data.experiment.StudyList}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-3
 */
public class StudyListModel extends AbstractPropertyTableModel {
    protected String[] columnnames = {"Name","Conditions","Template"};
    
    /**
     * 
     */
    public StudyListModel(StudyList list) {
        super(list);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 3;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return ((StudyList) object).size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        StudyList studyList = (StudyList)object;
        Study study = studyList.getStudy(rowIndex);
        switch (columnIndex) {
        case 0: return study.getName();
        case 1: return study.getStudyConditions();
        case 2: return study.getTemplate();
        default: return "";
        }
    }

    public synchronized StudyList getStudyList() {
        return (StudyList)object;
    }
    public synchronized void setStudyList(StudyList studyList) {
        setObject(studyList);
    }
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return columnnames[column];
    }
}
