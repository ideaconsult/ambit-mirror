package ambit2.db.readers;

import java.sql.ResultSet;

import ambit2.core.exceptions.AmbitException;

public interface IRetrieval<V> {
	V getObject(ResultSet rs) throws AmbitException;
}
