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

package ambit2.test.database;

import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import ambit2.database.SourceDataset;

public class testHibernate extends TestCase {
	public void testDataSource() throws Exception {
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		SourceDataset dataset;
		dataset = (SourceDataset) session.get(SourceDataset.class, "NCI");
		System.out.println("Dataset name= " + dataset.getName());
		
		List datasets = session.createQuery("from src_dataset").list();
		for (int i = 0; i < datasets.size(); i++)
		{
		    dataset = (SourceDataset) datasets.get(i);
		    System.out.println("Row " + (i + 1) + "> " + dataset.getName()
		            + " (" + dataset.getId_srcdataset() + ")");
		}		
		sessionFactory.close();
		
	}
}


