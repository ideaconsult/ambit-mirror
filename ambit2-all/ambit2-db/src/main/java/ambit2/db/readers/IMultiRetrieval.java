package ambit2.db.readers;

import java.sql.ResultSet;

import net.idea.modbcum.i.exceptions.AmbitException;

public interface IMultiRetrieval<V> extends IQueryRetrieval<V> {
	V getMultiObject(ResultSet rs) throws AmbitException;
}