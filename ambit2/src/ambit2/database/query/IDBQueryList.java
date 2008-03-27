package ambit2.database.query;

import ambit2.data.molecule.SourceDataset;

/**
 * Common interface for a list of queries.
 * <b>Modified</b> Aug 29, 2006
 */
public interface IDBQueryList {
    /**
     * @return true if to combine individual queries with logical "and", false if to combine with logical "or"
     */
    public boolean isCombineWithAND();
    /**
     * @param combineWithAND true if to combine individual queries with logical "and", false if to combine with logical "or"
     */
    public void setCombineWithAND(boolean combineWithAND);
    /**
     * 
     * @param page the page number to start from
     * @param pagesize  number of records per page
     * @return SQL string of the query
     */
    public String toSQL(SourceDataset srcDataset,int page, int pagesize);
}
