package ambit2.ui.editors;

import ambit2.base.data.Dictionary;

public class DictionaryEditor extends BeanEditor<Dictionary> {
	public DictionaryEditor() {
		super(null, new String[] {"template","parentTemplate"},
			new String[] {"Name","Parent"},"");
	}
}
