package ambit2.db.readers;

import java.sql.ResultSet;

import ambit2.base.exceptions.AmbitException;

public interface IMultiRetrieval<V> extends IQueryRetrieval<V> {
	V getMultiObject(ResultSet rs) throws AmbitException;
}