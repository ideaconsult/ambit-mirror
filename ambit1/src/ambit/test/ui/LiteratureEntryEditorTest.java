package ambit.test.ui;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit.data.IAmbitEditor;
import ambit.data.experiment.AquireTemplate;
import ambit.data.experiment.Experiment;
import ambit.data.experiment.ExperimentFactory;
import ambit.data.experiment.StudyTemplate;
import ambit.data.literature.AuthorEntries;
import ambit.data.literature.AuthorEntry;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.data.molecule.SourceDatasetList;
import ambit.ui.data.AbstractAmbitEditor;
import ambit.ui.data.AmbitListEditor;
import ambit.ui.data.AuthorEntryTableModel;
import ambit.ui.data.molecule.SourceDataSetTableModel;

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
			protected ambit.ui.data.AbstractPropertyTableModel createTableModel(ambit.data.AmbitObject object) {
				return new SourceDataSetTableModel((SourceDataset)object);
			};
		};
		

			JOptionPane.showMessageDialog(null,e.getJComponent());
			*/

	}
	public void testAuthorEditor() {
		AuthorEntry d = new AuthorEntry("test");
		IAmbitEditor e = new AbstractAmbitEditor("author",d) {
			protected ambit.ui.data.AbstractPropertyTableModel createTableModel(ambit.data.AmbitObject object) {
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
