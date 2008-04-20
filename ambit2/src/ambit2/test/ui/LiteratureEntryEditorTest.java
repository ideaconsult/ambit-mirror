package ambit2.test.ui;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import ambit2.config.EditorPreferences;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.ui.editors.IAmbitEditor;

public class LiteratureEntryEditorTest extends TestCase {
	/*
	public void testExperiment() {
		
		try {
			Experiment entry = ExperimentFactory.createBenzeneLC50();
			entry.editor(true).view(null,true,"experiment");
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	*/	
	public void test() throws Exception {
		LiteratureEntry entry = ReferenceFactory.createEmptyReference();
		IAmbitEditor<LiteratureEntry> e = EditorPreferences.getEditor(entry);
		JOptionPane.showMessageDialog(null,e.getJComponent());
	}
	

	
	/*
	public void testStudyTemplateEditor() {
		StudyTemplate d = new AquireTemplate();
		IAmbitEditor e = new AmbitListEditor(d,true);

		JOptionPane.showMessageDialog(null,e.getJComponent());
	}
	*/
}
