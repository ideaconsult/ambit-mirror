/*
* Copyright (C) 2005-2008  Ideaconsult Ltd.
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
package ambit2.db.test;

import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.core.external.CommandShell;
import ambit2.db.DatasourceFactory;
import ambit2.db.processors.ICommand;
import ambit2.db.processors.MySQLCommand;
import ambit2.db.processors.MySQLShell;
import ambit2.db.processors.MySQLStart;

/**
 * Test for {@link MySQLShell}
 * @author nina
 *
 */
public class MySQLShellTest  {
	protected MySQLCommand command;
	@Before
	public void  setUp() {
		command = new MySQLCommand();
		
	}
	@Test
    public void testPathNotFound() throws Exception {
        MySQLShell ms = new MySQLShell();
        command.setCommand(ICommand.COMMAND.START);
        command.setMysqlPath("nowhere");
        try {
        	ms.process(command);
        } catch (Exception x) {
        	Assert.assertNotNull(command.getException());
        }
	}	
	
    public void testStartStop() throws Exception {
        MySQLShell ms = new MySQLShell();
        command.setCommand(ICommand.COMMAND.START);
        //TODO setup a test instance
        command.setMysqlPath("e:/Ideaconsult/AmbitXT-v2.00/mysql");
        ms.process(command);
        if (command.getException() != null) 
        	throw new Exception(command.getException());
        Assert.assertNull(command.getException());
        Assert.assertNotNull(command.getProcess());
        
		DataSource datasource = DatasourceFactory.getDataSource(
		DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "mysql", "root","" ));
		//will fail if not running
		Connection c = datasource.getConnection();
		Assert.assertNotNull(c);
		c.close();
		command.setCommand(ICommand.COMMAND.STOP);
		ms.process(command);
        if (command.getException() != null) 
        	throw new Exception(command.getException());		
		Assert.assertNull(command.getProcess());
		Assert.assertNull(command.getException());
    }
	
	
    public void testDoubleStart() throws Exception {
        MySQLShell ms = new MySQLShell();
        command.setCommand(ICommand.COMMAND.START);
        //TODO setup a test instance
        command.setMysqlPath("e:/Ideaconsult/AmbitXT-v2.00/mysql");
        ms.process(command);
        if (command.getException() != null) 
        	throw new Exception(command.getException());
        Assert.assertNull(command.getException());
        Assert.assertNotNull(command.getProcess());
        
		DataSource datasource = DatasourceFactory.getDataSource(
		DatasourceFactory.getConnectionURI(
						"jdbc:mysql", 
						"localhost", "33060", "mysql", "root","" ));
		//will fail if not running
		Connection c = datasource.getConnection();
		Assert.assertNotNull(c);
		c.close();
		
		ms.process(command);
        Assert.assertNotNull(command.getException());
        Assert.assertNotNull(command.getProcess());
        
		command.setCommand(ICommand.COMMAND.STOP);
		ms.process(command);
        if (command.getException() != null) 
        	throw new Exception(command.getException());		
		Assert.assertNull(command.getProcess());
		Assert.assertNull(command.getException());
    }	
	
	
    public void testStop() throws Exception {
        MySQLShell ms = new MySQLShell();
  
		command.setCommand(ICommand.COMMAND.STOP);
		ms.process(command);
		
		Assert.assertNull(command.getProcess());
		Assert.assertNotNull(command.getException());
    }		
	
	/**
	 * Test of a newer implementation of MySQLShell, as a {@link ambit2.core.external.CommandShell} descendant
	 * @throws Exception
	 */
	public void testShell() throws Exception {
        command.setMysqlPath("e:/Ideaconsult/AmbitXT-v2.00/mysql");
		MySQLStart shell = new MySQLStart();
		shell.runShell(command);
		Assert.assertNotNull(command.getProcess());
		Assert.assertNull(command.getException());
		command.setCommand(ICommand.COMMAND.STOP);
		shell.runShell(command);
		Assert.assertNotNull(command.getProcess());
		Assert.assertNull(command.getException());		
	}
}
