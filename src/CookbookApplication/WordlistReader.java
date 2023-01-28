package CookbookApplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;


public class WordlistReader 
{
	private HashSet<String> hashset = new HashSet<String>();
	
	public HashSet<String> getHashSet()			{	return hashset;		}
	
	public void read(String path)
	{
		try(BufferedReader reader = new BufferedReader(new FileReader(path)))
		{
			String s;
			while((s = reader.readLine()) != null)
			{
				hashset.add(s);
				Debug.log("added to hashset: " + s);
			}
			Debug.log("number of hashset elements: " + hashset.size());
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
}
