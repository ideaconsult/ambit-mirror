package ambit2.export.isa.v1_0;

import java.net.URI;

import ambit2.export.isa.v1_0.objects.Protocol;
import ambit2.export.isa.v1_0.objects.Sample;
import ambit2.export.isa.v1_0.objects.Source;

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
		
		if (obj instanceof Source)
		{
			Source s = new Source();
			nullify(s);
			s.id = id;
			return s;
		}
		
		if (obj instanceof Sample)
		{
			Sample s = new Sample();
			nullify(s);
			s.id = id;
			return s;
		}
			
		return null;
	}
	
	public static void nullify(Protocol p)
	{
		p.comments = null;
		p.components = null;
		p.parameters = null;
	}
	
	public static void nullify(Source s)
	{
		s.characteristics = null;
	}
	
	public static void nullify(Sample s)
	{
		s.characteristics = null;
		s.factorValues = null;
		s.derivesFrom = null;
	}
	
}
