package ambit2.test.ui;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit2.ui.data.AuthorEntryTableModel;
import ambit2.ui.data.molecule.SourceDataSetTableModel;
import ambit2.ui.editors.AbstractAmbitEditor;
import ambit2.ui.editors.AmbitListEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.experiment.AquireTemplate;
import ambit2.data.experiment.Experiment;
import ambit2.data.experiment.ExperimentFactory;
import ambit2.data.experiment.StudyTemplate;
import ambit2.data.literature.AuthorEntries;
import ambit2.data.literature.AuthorEntry;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.molecule.SourceDataset;
import ambit2.data.molecule.SourceDatasetList;

public class LiteratureEntryEditorTest extends TestCase {
	
	public void testExperiment() {
		
		try {
			Experiment entry = ExperimentFactory.createBenzeneLC50();
			entry.editor(true).view(null,true,"experiment");
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}	
	public void test() {
		LiteratureEntry entry = ReferenceFactory.createEmptyReference();
		try {
		entry.editor(true).view(null,true,"Select reference");
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	
	public void testSrcEditor() {
		SourceDatasetList list = new SourceDatasetList();
		SourceDataset d = new SourceDataset("test",ReferenceFactory.createDatasetReference(
				"xxxxxxxxxxxxxxxx\\xxxxxxxxxxxxxxxxxxxxxxxx\\xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\\xxxxxxxxxx", "www"));
		list.addItem(d);
		list.addItem(new SourceDataset("search",ReferenceFactory.createSearchReference(
				"name")));
		try {
		list.editor(true).view(null,true,"");
		} catch (Exception x) {
			
		}
		/*
		IAmbitEditor e = new AbstractAmbitEditor("dataset",d) {
			protected ambit2.ui.data.AbstractPropertyTableModel createTableModel(ambit2.data.AmbitObject object) {
				return new SourceDataSetTableModel((SourceDataset)object);
			};
		};
		

			JOptionPane.showMessageDialog(null,e.getJComponent());
			*/

	}
	public void testAuthorEditor() {
		AuthorEntry d = new AuthorEntry("test");
		IAmbitEditor e = new AbstractAmbitEditor("author",d) {
			protected ambit2.ui.data.AbstractPropertyTableModel createTableModel(ambit2.data.AmbitObject object) {
				return new AuthorEntryTableModel((AuthorEntry)object);
			};
			
		};

			JOptionPane.showMessageDialog(null,e.getJComponent());
	}
	public void testAuthorsEditor() {
		AuthorEntries d = new AuthorEntries();
		IAmbitEditor e = new AmbitListEditor(d,true);

			JOptionPane.showMessageDialog(null,e.getJComponent());
	}	
	public void testStudyTemplateEditor() {
		StudyTemplate d = new AquireTemplate();
		IAmbitEditor e = new AmbitListEditor(d,true);

		JOptionPane.showMessageDialog(null,e.getJComponent());
	}
}
