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

package ambit2.db.search;

import net.idea.modbcum.i.IQueryObject;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;



public interface IStoredQuery extends IQueryObject<IStructureRecord> , ISourceDataset{
	static String SQL_INSERT = "insert ignore into query_results (idquery,idchemical,idstructure,selected,metric,text) ";
	void setName(String name);
	String getName();
	int getRows();
	void setRows(int rows);
	IQueryObject<IStructureRecord> getQuery();
	void setQuery(IQueryObject<IStructureRecord> query);	
	String getContent();
	void setContent(String content);
	Template getTemplate();
	void setTemplate(Template template);
}


