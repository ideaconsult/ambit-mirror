package ambit2.sln.dictionary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ambit2.sln.SLNContainer;
import ambit2.sln.SLNHelper;
import ambit2.sln.SLNParser;
import ambit2.sln.SLNParserError;


public class SLNDictionary 
{
	public static class DictionaryObjectReferencesCheckResult {
		public List<String> errors = new ArrayList<String>();
		public List<String> warnings = new ArrayList<String>();
	}
	
	
	private HashMap<String,ISLNDictionaryObject> objects = new HashMap<String,ISLNDictionaryObject>();
	private List<String> names = new ArrayList<String>();
	private ArrayList<SLNParserError> parserErrors = new ArrayList<SLNParserError>();
	private ArrayList<String> checkErrors = new ArrayList<String>();
		
	public void addDictionaryObject(ISLNDictionaryObject dictObj)
	{
		names.add(dictObj.getObjectName());
		objects.put(dictObj.getObjectName(), dictObj);
	}
	
	public ISLNDictionaryObject getDictionaryObject(String name)
	{	
		return objects.get(name);
	}
	
	public ISLNDictionaryObject getDictionaryObject(int index)
	{	
		String name = names.get(index);
		return objects.get(name);
	}
	
	public boolean containsObject(String name)
	{
		return objects.containsKey(name);
	}
	
	public boolean containsObject(String name, ISLNDictionaryObject.Type type)
	{
		return objects.containsKey(name);
	}
	
	public List<String> getNames() 
	{
		return names;
	}
			
	public ArrayList<SLNParserError> getParserErrors() {
		return parserErrors;
	}
	
	public String getParserErrorMessages() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parserErrors.size(); i++) {
			sb.append(parserErrors.get(i).getError() + "\n");
		}
		return (sb.toString());
	}
	
	
	public ArrayList<String> getCheckErrors() {
		return checkErrors;
	}
	
	public String getCheckErrorMessages() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < checkErrors.size(); i++) {
			sb.append(checkErrors.get(i) + "\n");
		}
		return (sb.toString());
	}

	public String toSLN() {
		StringBuffer sb = new StringBuffer();
		SLNHelper slnHelper = new SLNHelper();
		
		for (int i = 0; i<names.size(); i++)
		{
			sb.append("{");
			String name = names.get(i);
			sb.append(name);
			sb.append(":");
			sb.append(toSLN(objects.get(name), slnHelper));
			sb.append("}");
		}
		return sb.toString();
	}
	
	
	String toSLN(ISLNDictionaryObject dictObj, SLNHelper slnHelper) {
		switch (dictObj.getObjectType())
		{
		case ATOM:
			AtomDictionaryObject a = (AtomDictionaryObject) dictObj;
			return slnHelper.toSLN(a.container);
		case MACRO_ATOM:
			MacroAtomDictionaryObject ma = (MacroAtomDictionaryObject) dictObj;
			return slnHelper.toSLN(ma.container);
		case MARKUSH_ATOM:
			MarkushAtomDictionaryObject markushAt = (MarkushAtomDictionaryObject) dictObj;
			return markushAt.getContentString(slnHelper);
		}
		
		return "";
	}
	
	
	public static SLNDictionary getDictionary(String fileName) throws Exception
	{
		SLNParser parser = new SLNParser();
		return getDictionary(fileName, parser);
	}
	
	public static SLNDictionary getDictionary(String fileName, SLNParser parser) throws Exception
	{
		if (fileName == null)
			return null;
		if (parser == null)
			return null;
				
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
		}
		catch (FileNotFoundException e) {
			throw e;
		}
		
		parser.getErrors().clear();
		SLNDictionary dict = new SLNDictionary(); 
		
		try {
			int nLine = 0;
			String line;
			
			while ( (line = br.readLine()) != null ) 
			{
				nLine++;
				String s = line.trim();
				
				if (s.isEmpty())
				{
					//System.out.println("Empty SLN string in line #" + nLine);
					continue;
				}				
				
				if (s.isEmpty() || !s.startsWith("{") || !s.endsWith("}"))
				{	
					parser.getErrors().add(new SLNParserError("Line #" + nLine + "  " + 
							s, "Missing open or close brackets {}" , 0, ""));
					continue;
				}
				
				String dictObjStr = s.substring(1,s.length()-1);			
				ISLNDictionaryObject dictObj = parser.parseDictionaryObject(dictObjStr);
				if (dictObj != null)
					dict.addDictionaryObject(dictObj);
			}	
			
			br.close();
		}
		catch (IOException e) {			
			
			throw e;
		}
		
		
		if (!parser.getErrors().isEmpty())
		{	
			//Store errors inside the dictionary
			dict.parserErrors.addAll(parser.getErrors());			
			parser.getErrors().clear();
		}
		
		return dict;
	}
	
	
	public static SLNDictionary getDictionary(String dictionaryObjects[], SLNParser parser) 
	{
		if (dictionaryObjects == null)
			return null;
		if (parser == null)
			return null;
		
		parser.getErrors().clear();
		SLNDictionary dict = new SLNDictionary(); 
		
		for (int i = 0; i < dictionaryObjects.length; i++)
		{
			String s = dictionaryObjects[i];
			if (s.isEmpty() || !s.startsWith("{") || !s.endsWith("}"))
			{	
				parser.getErrors().add(new SLNParserError(s, "Missing open or close brackets {}" , 0, ""));
				continue;
			}
			
			String dictObjStr = s.substring(1,s.length()-1);			
			ISLNDictionaryObject dictObj = parser.parseDictionaryObject(dictObjStr);
			if (dictObj != null)
				dict.addDictionaryObject(dictObj);
		}
		
		if (!parser.getErrors().isEmpty())
		{	
			//Store errors inside the dictionary
			dict.parserErrors.addAll(parser.getErrors());			
			parser.getErrors().clear();
		}
		
		return dict;
	}
	
	public void checkDictionary() {
		
		for (String name:names)
		{
			ISLNDictionaryObject obj = getDictionaryObject(name);
			
			if (obj instanceof MacroAtomDictionaryObject)
				checkMacroAtom((MacroAtomDictionaryObject) obj);
			else if (obj instanceof MarkushAtomDictionaryObject)
				checkMarkushAtom((MarkushAtomDictionaryObject) obj);
		}
		
		 
	}
	
	public void checkMacroAtom(MacroAtomDictionaryObject maDO) {
		//Do semantic check: valences values correctness
		int valAtInd[] = maDO.getValenceAtomIndices();
		if (valAtInd == null)
			return;
		
		for (int i = 0; i < valAtInd.length; i++)
			if (valAtInd[i] == -1)
			{
				String err = maDO.name + " Incorrect valence element [" + (i+1) + "]: " + maDO.getValences()[i];
				checkErrors.add(err);				
			}
	}
	
	public void checkMarkushAtom(MarkushAtomDictionaryObject maDO) {
		
		//TODO
	}
	
	
	public static DictionaryObjectReferencesCheckResult checkContainer(SLNContainer container) {
		
		//Check whether the dictionary objects (e.g. ATOM, MACRO_ATOM, MARKUSH_ATOMS)
		//are referenced properly  
		
		//TODO 
		
		return null;
	}
}
