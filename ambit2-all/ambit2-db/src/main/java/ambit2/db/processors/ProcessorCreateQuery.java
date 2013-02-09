/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.db.processors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import ambit2.base.config.Preferences;
import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorException;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.UpdateExecutor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryStructureReporter;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.QueryExecutor;
import ambit2.db.search.QueryParam;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.update.IQueryUpdate;
import ambit2.db.update.dictionary.TemplateAddProperty;
import ambit2.db.update.storedquery.CreateStoredQuery;
import ambit2.db.update.storedquery.QueryAddTemplate;

/**
 * Inserts into query table idstructure numbers from a query identified by {@link StoredQuery}. Example:
 * @author nina

 */
public class ProcessorCreateQuery  extends AbstractDBProcessor<IQueryObject<IStructureRecord>,IStoredQuery> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6765755816334059911L;
	//protected final String sql = "insert into query (idquery,idsessions,name,content,idtemplate) values (null,?,?,?,?)";
	protected IStoredQuery result;
	protected boolean copy = false;
	protected boolean delete = false;
	protected Template profile;
	protected UpdateExecutor<IQueryUpdate> exec;
	protected QueryExecutor qexec = new QueryExecutor();	
	protected String queryName = null;
    public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public Template getProfile() {
		return profile;
	}
	public void setProfile(Template profile) {
		this.profile = profile;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isCopy() {
		return copy;
	}
	public void setCopy(boolean copy) {
		this.copy = copy;
	}
	public IStoredQuery getStoredQuery() {
		return result;
	}
	public void setStoredQuery(IStoredQuery storedQuery) {
		this.result = storedQuery;
	}
	public void open() throws DbAmbitException {
        // TODO Auto-generated method stub
        
    }
	public IStoredQuery process(IQueryObject<IStructureRecord> target) throws AmbitException {
		if (target == null) throw new AmbitException("Undefined query!");


		try {
			if (result == null) {
				if (!copy & (target instanceof QueryStoredResults))
					return ((QueryStoredResults) target).getFieldname();
				
				result = new StoredQuery(-1);
				result.setName(queryName==null?UUID.randomUUID().toString():queryName);
			}  else {
				if (target instanceof QueryStoredResults) {
					if ((((QueryStoredResults) target).getId()==result.getId()) && (result.getId()>0))
						delete = false; //if we are copying to the same query , don't delete!
				}
			}
			
			result.setQuery(target);
			connection.setAutoCommit(false);	
			//create entry in the query table
			if (result.getId()<=0) {
				PreparedStatement s = connection.prepareStatement(CreateStoredQuery.sql_byname,Statement.RETURN_GENERATED_KEYS);
				s.setNull(1,Types.INTEGER);
				if (result.getName().length()>255)
					s.setString(2,result.getName().substring(0,255));
				else
					s.setString(2,result.getName());
				try {
					s.setString(3,result.getQuery().toString()==null?"Results":result.getQuery().toString());
				} catch (Exception x) {s.setString(3,"Results");}	
				s.setString(4,getSession().getName());
	
				//execute
				if (s.executeUpdate()>0) {
					ResultSet rss = s.getGeneratedKeys();
					while (rss.next())
						result.setId(new Integer(rss.getInt(1)));
					rss.close();
				}
				s.close();
			} else if (delete) {
				PreparedStatement s = connection.prepareStatement("Delete from query_results where idquery=?");
				s.setInt(1,result.getId());
				s.executeUpdate();
				s.close();
			}
			
			if (result.getId()>0) {
				int rows = 0;
				if ((result.getQuery() instanceof IQueryRetrieval) && ((IQueryRetrieval)result.getQuery()).isPrescreen()) {
					rows = insertScreenedResults(result,(IQueryRetrieval<IStructureRecord>)result.getQuery());
			   } else  {
					rows = insertResults(result);
					if (rows > 0)
						connection.commit();
					else 
						connection.rollback();
				}
			   insertProfile(result, profile);
			}
			
		
			return result;
		} catch (Exception x) {
			try {
				connection.rollback();
			} catch (SQLException xx){
				
			}
			throw new ProcessorException(this,x);
		} finally {
			try {close(); } catch (Exception e) {}
		}
	}

	protected void insertProfile(IStoredQuery q,Template template) throws Exception  {
		if (template != null) 
		try {
			connection.setAutoCommit(true);
			if (exec == null) exec = new UpdateExecutor<IQueryUpdate>();
			exec.setConnection(connection);
			TemplateAddProperty addProperty = new TemplateAddProperty();
			Dictionary d = new Dictionary();
			d.setParentTemplate("Dataset");
			d.setTemplate((template.getName()==null)?String.format("Query%d",q.getId()):template.getName());
			addProperty.setGroup(d);
			//if ((template.getId()<=0) && (template.getName()==null)) {
				Iterator<Property> properties = template.getProperties(true);
				while (properties.hasNext()) {
					addProperty.setObject(properties.next());
					exec.process(addProperty);
				}
				
			//}
			template.setName(d.getTemplate());
			QueryAddTemplate addTemplate = new QueryAddTemplate();
			addTemplate.setGroup(q);
			addTemplate.setObject(template);
			exec.process(addTemplate);
		} catch (Exception x) {
			throw x;
		} finally {
			
		}
	}
	@Override
	public void close() throws SQLException {
		super.close();
		try { if (exec != null) exec.close(); } catch (Exception x) {}
	}
	protected int insertScreenedResults(IStoredQuery result, final IQueryRetrieval<IStructureRecord> query) throws SQLException , AmbitException {
		final PreparedStatement insertGoodResults = 
			connection.prepareStatement(IStoredQuery.SQL_INSERT + " values(?,?,?,1,?,?)");
		QueryStructureReporter<IQueryRetrieval<IStructureRecord>,IStructureRecord> reporter = 
			new QueryStructureReporter<IQueryRetrieval<IStructureRecord>, IStructureRecord>() {
			
			@Override
			public void close() throws SQLException {
				// TODO Auto-generated method stub
				super.close();
			}


			@Override
			public void header(IStructureRecord output,
					IQueryRetrieval<IStructureRecord> query) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object processItem(IStructureRecord record)
					throws AmbitException {
				try {
					insertGoodResults.setInt(1,query.getId());
					insertGoodResults.setInt(2,record.getIdchemical());
					insertGoodResults.setInt(3,record.getIdstructure());
					insertGoodResults.setDouble(4,1);
					insertGoodResults.setNull(5,Types.VARCHAR);	
					insertGoodResults.executeUpdate();
				} catch (Exception x) {
					throw new AmbitException(x);
				}
				return null;
			}

			public void open() throws DbAmbitException {
			}

			@Override
			public void footer(IStructureRecord output,
					IQueryRetrieval<IStructureRecord> query) {
				// TODO Auto-generated method stub
				
			}
			
		};		
		try {
			long timeout = 8*1000;
			try {
				timeout = Long.parseLong(Preferences.getProperty(Preferences.TIMEOUT));
			} catch (Exception x) {
			}
			reporter.setTimeout(timeout); //1s
			reporter.setAutoCommit(true);
			reporter.setConnection(connection);
			reporter.process(query);
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
			try {insertGoodResults.close(); } catch (Exception x) { logger.log(Level.FINEST,x.getMessage(),x);}
			try {reporter.close();} catch (Exception x) { logger.log(Level.FINEST,x.getMessage(),x);}
		}
		return 1;

	}
	
	protected int insertResults(IStoredQuery result) throws SQLException , AmbitException {
		
		PreparedStatement sresults = connection.prepareStatement(qexec.getSQL(result));
		List<QueryParam> params = result.getParameters();
		QueryExecutor.setParameters(sresults, params);
		int rows = sresults.executeUpdate();
		result.setRows(rows);
		sresults.close();		
		return rows;
	}

}


