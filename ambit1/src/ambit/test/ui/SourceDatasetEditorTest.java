/*
 Copyright (C) 2005-2006  

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

package ambit.test.ui;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit.data.AmbitList;
import ambit.data.descriptors.DescriptorFactory;
import ambit.data.descriptors.DescriptorsList;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.exceptions.AmbitException;
import ambit.ui.data.CompoundsGridPane;
import ambit.ui.data.GridTableModel;
import ambit.ui.data.descriptors.DescriptorsPanel;

public class SourceDatasetEditorTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SourceDatasetEditorTest.class);
	}

	public void test() {
		SourceDataset d = new SourceDataset("dataset", ReferenceFactory
				.createDatasetReference("file.sdf", "file"));
		try {
			d.editor(true).view(null, true, "");
		} catch (AmbitException x) {

		}
	}

	public void testGrid() {
		AmbitList l = new AmbitList();
		l.addItem(new SourceDataset("dataset", ReferenceFactory
				.createDatasetReference("file.sdf", "file")));
		l.addItem(new SourceDataset("dataset", ReferenceFactory
				.createDatasetReference("file.sdf", "file")));
		l.addItem(new SourceDataset("dataset", ReferenceFactory
				.createDatasetReference("file.sdf", "file")));
		l.addItem(new SourceDataset("dataset", ReferenceFactory
				.createDatasetReference("file.sdf", "file")));

		// JTable t = new JTable(new GridTableModel(l,2));
		// JScrollPane p = new JScrollPane(t);
		JOptionPane.showMessageDialog(null, new CompoundsGridPane(
				new GridTableModel(l, 1), new Dimension(100,80)));
	}

	public void testGridReference() {
		AmbitList l = new AmbitList();
		l.addItem(ReferenceFactory.createAmesReference());
		l.addItem(ReferenceFactory.createBCFGrammaticaReference());
		l.addItem(ReferenceFactory.createBCFWinReference());
		l.addItem(ReferenceFactory.createDebnathReference());
		l.addItem(ReferenceFactory.createDebnathReference());
		l.addItem(ReferenceFactory.createKOWWinReference());

		// JTable t = new JTable(new GridTableModel(l,2));
		// JScrollPane p = new JScrollPane(t);
		JOptionPane.showMessageDialog(null, new CompoundsGridPane(
				new GridTableModel(l, 1), new Dimension(180, 200)),"",JOptionPane.PLAIN_MESSAGE);

		l.clear();
		l.addItem(ReferenceFactory.createDebnathRefAuthors());
		JOptionPane.showMessageDialog(null, new CompoundsGridPane(
				new GridTableModel(l, 1), new Dimension(100, 30)));

		l.clear();
		l.addItem(ReferenceFactory.createJournalMutRes());
		JOptionPane.showMessageDialog(null, new CompoundsGridPane(
				new GridTableModel(l, 1), new Dimension(100, 100)));
		
		l.clear();
		l = DescriptorFactory.createDebnathSmilesFileDescriptors().getSelectedPropertiesList()
		;
		JOptionPane.showMessageDialog(null, new DescriptorsPanel((DescriptorsList)l),"",JOptionPane.PLAIN_MESSAGE);		
	}
	
	public void testDescriptors() {
		DescriptorsList d = new DescriptorsList();
		try {
			d.editor(true).view(null, true, "");
		} catch (AmbitException x) {

		}
	}
}
