package ambit2.ui.editors;

import ambit2.base.data.StringBean;

public class StringBeanEditor extends BeanEditor<StringBean> {

    public StringBeanEditor() {
	super(null, new String[] { "name" }, "");
    }

    public StringBeanEditor(StringBean object, String detailsCaption) {
	super(object, new String[] { "name" }, detailsCaption);
    }

}
