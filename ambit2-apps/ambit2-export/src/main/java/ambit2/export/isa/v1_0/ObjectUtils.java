package ambit2.export.isa.v1_0;

import java.net.URI;

import ambit2.export.isa.v1_0.objects.Protocol;

public class ObjectUtils 
{
	
	public static Object getIdInstance(Object obj, URI id)
	{
		if (obj instanceof Protocol)
		{
			Protocol p = new Protocol();
			nullify(p);
			p.id = id;
			return p;
		}
			
		return null;
	}
	
	public static void nullify(Protocol p)
	{
		p.comments = null;
		p.components = null;
		p.parameters = null;
	}
}
