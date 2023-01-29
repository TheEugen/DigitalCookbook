package CookbookApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserConfig implements Serializable
{
	static final long serialVersionUID = 1;
	
	private String savePath;
	private ArrayList<Unit> units = new ArrayList<Unit>();
	private String[] languages = {"German", "English"};
	private String workingLang;
	private boolean suppressWarningRecipe;
	private boolean suppressWarningCookbook;
	
	
	public String getWorkingLanguage()					{	return workingLang;				}
	public void setWorkingLanguage(String s)			{	workingLang = s;				}	
	public void setSavePath(String s)					{	savePath = s;					}
	public ArrayList<Unit> getUnits()					{	return units;					}
	public String[] getLanguages()						{	return languages;				}
	public void setSuppressWarningRecipe(boolean b)		{	suppressWarningRecipe = b;		}
	public void setSuppressWarningCookbook(boolean b)	{	suppressWarningCookbook = b;	}
	public boolean getSuppressWarningRecipe()			{	return suppressWarningRecipe;	}
	public boolean getSuppressWarningCookbook()			{	return suppressWarningCookbook;	}
	
	public UserConfig()
	{
		// default units
		units.add(new Unit("mg"));
		units.add(new Unit("g"));
		units.add(new Unit("kg"));;
		units.add(new Unit("TL"));
		units.add(new Unit("EL"));

		// get user system language
		Locale locale = Locale.getDefault();
		
		// if german set to german, else to english
		if(locale.equals(new Locale("de", "DE")))
			workingLang = languages[0];
		else
			workingLang = languages[1];		
	}

	public static UserConfig loadUserConfig(String path)
	{	
		// load the config file C:/Users/xxx/Rezepte/user_config.cfg
		File file = new File(path);
		
		UserConfig cfg;
		
		// if user_config.cfg exists load the file
		if(file.exists())
		{
			try(FileInputStream fis = new FileInputStream(path);
					ObjectInputStream ois = new ObjectInputStream(fis))
				{
					cfg = (UserConfig) ois.readObject();
					return cfg;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					
					return null;
				}
		}
		// otherwise create a new one
		else
		{
			cfg = new UserConfig();
			UserConfig.saveUserConfig(cfg, path.replace("user_config.cfg", ""));
			return cfg;
		}

	}
	
	public static void saveUserConfig(UserConfig cfg, String path)
	{
		try(FileOutputStream fos = new FileOutputStream(path + "user_config.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos))
		{
			oos.writeObject(cfg);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean configFileExists()
	{
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(savePath + "\\Rezepte")))
		{
			for(Path file: stream)
			{
				if (file.getFileName().toString().equals("user_config.cfg"))
					return true;
			}
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		
		return false;
	}
	
	public List<String> getUnitStrings() throws Exception
	{
		List<String> s_units = new ArrayList<String>();
		
		if (!(units.isEmpty()))
		{
			for (Unit u: units)
				s_units.add(u.getTitle());
			
			return s_units;
		}
		
		throw new Exception("UserConfig: units list empty");
	}
	

}
