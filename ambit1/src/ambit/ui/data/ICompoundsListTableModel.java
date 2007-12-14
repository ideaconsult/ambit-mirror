/*
 * Created on 2006-3-5
 *
 */
package ambit.ui.data;

import ambit.data.AmbitList;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public interface ICompoundsListTableModel {
    AmbitList getList();
    void setList(AmbitList list);
}
