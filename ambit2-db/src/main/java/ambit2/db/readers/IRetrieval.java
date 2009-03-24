package ambit2.db.readers;

import java.sql.ResultSet;

import ambit2.base.exceptions.AmbitException;

public interface IRetrieval<V> {
	V getObject(ResultSet rs) throws AmbitException;
}
