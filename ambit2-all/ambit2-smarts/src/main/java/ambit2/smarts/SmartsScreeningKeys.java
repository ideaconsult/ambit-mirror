package ambit2.smarts;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.Vector;


public class SmartsScreeningKeys 
{
	public static int nKeys = 1024;
	static boolean mKeysLoaded = false;
	static Vector<String> mSmartsKeys = new Vector<String>();
	
	
	public Vector<String> getKeys()
	{
		if (!mKeysLoaded)
			loadKeysFromResource();
		
		return (mSmartsKeys);
	}
	
	void loadKeysFromResource()
	{
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("smartskeys.txt");
		BufferedReader inReader  = new BufferedReader(new InputStreamReader(inStream));
		mSmartsKeys.clear();
		
		try {
			String line = inReader.readLine();
			int n = 0;
			while (line != null)
			{	
				mSmartsKeys.add(line.trim());
				n++;
				if (n==nKeys)
					break;
				line = inReader.readLine();
			}
			inReader.close();
		} 
		catch (Exception e){}
		mKeysLoaded = true;
	}
	
	
}
