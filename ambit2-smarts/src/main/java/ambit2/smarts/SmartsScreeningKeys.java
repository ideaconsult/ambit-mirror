package ambit2.smarts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class SmartsScreeningKeys 
{
	public int nKeys = 1024;
	boolean mKeysLoaded = false;
	List<String> mSmartsKeys = new ArrayList<String>();
	
	
	public List<String> getKeys() throws Exception
	{
		if (!mKeysLoaded)
			loadKeysFromResource();
		
		return (mSmartsKeys);
	}
	
	void loadKeysFromResource() throws Exception
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
			
		} finally {
			try { inReader.close(); } catch (Exception x) {}
		}
		mKeysLoaded = true;
	}
	
}
