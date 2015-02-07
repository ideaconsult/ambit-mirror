package ambit2.base.data.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;

public class PropertyAnnotationTest {
    @Test
    public void test1() throws Exception {
	PropertyAnnotation a = new PropertyAnnotation<>();
	a.setObject("o");
	a.setPredicate("p");
	a.setType("t");
	Assert.assertEquals("\n\t{\t\"type\" : \"t\",\t\"p\" : \"p\",\t\"o\" : \"o\"}",a.toJSON());
    }
    
    @Test
    public void testMany() throws Exception {
	PropertyAnnotations as = new PropertyAnnotations();
	PropertyAnnotation a1 = new PropertyAnnotation<>();
	a1.setObject("o");
	a1.setPredicate("p");
	a1.setType("t");
	as.add(a1);
	PropertyAnnotation a2 = new PropertyAnnotation<>();
	a2.setObject("o1");
	a2.setPredicate("p1");
	a2.setType("t1");
	as.add(a2);
	Assert.assertEquals("\n\t{\t\"type\" : \"t\",\t\"p\" : \"p\",\t\"o\" : \"o\"},\n\t{\t\"type\" : \"t1\",\t\"p\" : \"p1\",\t\"o\" : \"o1\"}",as.toJSON());
    }
}
