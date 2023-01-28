package CookbookApplication;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


public class DataManager
{	
	// make public and static
	private final int CONST_TITLE_LENGTH_RECIPE = 256;
	private final int CONST_TITLE_LENGTH_CB = 64;
	
	private WindowMain windowMain;
	private String savePath;
	
	private List<Cookbook> cookbooks = new ArrayList<Cookbook>();
	private Cookbook workingCB;
	private Recipe workingRecipe;
	
	private boolean deletedRecipe, changedRecipe, addedCB, deletedCB;
	private AtomicBoolean newRecipe = new AtomicBoolean(false);
	
	private List<Unit> units = new ArrayList<Unit>();
	
	private ResourceBundle resources;
	
	private UserConfig cfg;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private PropertyChangeListener listener = new PropertyChangeListener()
											{
												@Override 
												public void propertyChange(PropertyChangeEvent e)
												{		  
													switch (e.getPropertyName())
													{
														case "workingRecipe":		
															windowMain.updateTextFields();
															break;
														case "workingCB":
															setWorkingRecipe(null);		
															windowMain.updateObsvListLV();
															break;
														case "newRecipe":
															windowMain.updateObsvListLV();
															windowMain.getMainUI().getWindowNewRecipe().updateObsvList();
															windowMain.getListView().getSelectionModel().select(workingRecipe);
															break;
														case "deletedRecipe":
															setWorkingRecipe(null);		
															windowMain.updateObsvListLV();
															break;
														case "changedRecipe":
															windowMain.updateTextFields();
															break;
														case "addedCB":							
															windowMain.updateObsvListCB();
															setWorkingCB(cookbooks.get(cookbooks.size() - 1));
															windowMain.getComboBox().getSelectionModel().select(workingCB);												
															break;
														case "deletedCB":							
															windowMain.updateObsvListCB();
															if (cookbooks.isEmpty())
																addCookbook(getExampleCB());
															else
																windowMain.getComboBox().getSelectionModel().select(cookbooks.get(0));											
															break;
														default:
															break;
													  }
												}
											}; 
											
																				
	public DataManager(String path)
	{
		savePath = path;
		addPropertyChangeListener(listener);
		cfg = UserConfig.loadUserConfig(savePath + "\\Rezepte" + "\\user_config.cfg");
	}
	
	public static List<Field> getAllFields(Class<?> type)
	{
		List<Field> fields = new ArrayList<Field>();
		for(Class<?> cl = type; cl != null; cl = cl.getSuperclass())
			fields.addAll(Arrays.asList(cl.getDeclaredFields()));
		
		return fields;
	}
	
	public static String getDataFromClipboard()
	{
		return Clipboard.getSystemClipboard().getString();
	}
	
	public static void copyToClipboard(String s)
	{
		assert(s != null);
		ClipboardContent content = new ClipboardContent();
		content.putString(s);
		Clipboard.getSystemClipboard().setContent(content);
	}
	
	public static Recipe decodeRecipe(String s, Cookbook cb) throws Exception
	{		
		byte[] data = Base64.getDecoder().decode(s);
			
		Recipe r = new Recipe();
		
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data); 
				ObjectInputStream ois = new ObjectInputStream(bais))
		{
			r = (Recipe) ois.readObject();	
		}
		catch (Exception e)
		{
			System.err.println("recipe decoding failed" + "\n" + e);
		}
		
		if (cb.getRecipes().contains(r))
			throw new Exception ("tried to import existing recipe");
		
		// TODO check for existing title
		
		return r;
	}
	
	public static String encodeRecipe(Recipe r) throws Exception
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			oos.writeObject(r);
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		throw new Exception ("encoding failed");	
	}
	
	public static Cookbook decodeCB(String s) throws Exception
	{		
		byte[] data = Base64.getDecoder().decode(s);
		
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data); 
				ObjectInputStream ois = new ObjectInputStream(bais))
		{
			return (Cookbook) ois.readObject();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		throw new Exception ("decoding failed");
	}
	
	public static String encodeCB(Cookbook cb) throws Exception
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos))
		{
			oos.writeObject(cb);
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		throw new Exception ("encoding failed");	
	}
	
	public UserConfig getUserConfig()
	{
		return cfg;
	}
	
	public void flipChangedRecipe()
	{
		changedRecipe = !changedRecipe;
		pcs.firePropertyChange("changedRecipe", false, true);
	}
	
	public Path hasFile(Cookbook cb)
	{
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(savePath + "\\Rezepte")))
		{
			for(Path file: stream)
			{
				if (file.getFileName().toString().equals(cb.getTitle() + ".cbk"))
					return file;
			}
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void deleteCookbookFile() throws Exception
	{
		Path path = hasFile(workingCB);
		
		if (path != null)
		{
			File file = new File(path.toString());
			
			if (!(file.delete()))
				throw new Exception("failed to delete file");
		}
	}
	
	public void deleteCB()
	{
		assert(workingCB != null);
		
		try
		{
			deleteCookbookFile();
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
		}	
		
		cookbooks.remove(workingCB);
		deletedCB = !deletedCB;
		pcs.firePropertyChange("deletedCB", false, true);	
	}
	
	public int getRecipeMaxTitleLength()
	{
		return CONST_TITLE_LENGTH_RECIPE;
	}
	
	public int getCBMaxTitleLength()
	{
		return CONST_TITLE_LENGTH_CB;
	}
	
	public void init(WindowMain windowMain)
	{
		setWindowMain(windowMain);	
		searchForFiles();
		loadUserConfig();
		
		if (cfg.getWorkingLanguage().equals("German"))
			loadResourceBundle(new Locale("de", "DE"));
		else if (cfg.getWorkingLanguage().equals("English"))
			loadResourceBundle(new Locale("en", "US"));
		else
			loadResourceBundle(Locale.getDefault());
	}
	
	public String getText(String label)
	{
		return resources.getString(label);
	}
	
	public void loadResourceBundle(Locale locale)
	{
		resources = ResourceBundle.getBundle("CookbookApplication.Labels", locale);
	}
	
	public boolean titleExists(Class<?> cl, String title)
	{
		if (cl == Cookbook.class)
		{
			for (Cookbook cb: cookbooks)
			{
				if (cb.getTitle().equals(title))
					return true;
			}
		}
		else if (cl == Recipe.class)
		{
			for (Recipe r: workingCB.getRecipes())
			{
				if(r.getTitle().equals(title))
					return true;
			}
		}
		
		return false;
	}
		
	public void flipDeletedRecipe()
	{
		deletedRecipe = !deletedRecipe;
		pcs.firePropertyChange("deletedRecipe", false, true);
	}
	
	public void flipNewRecipe()
	{
		newRecipe.set(!newRecipe.get());
		pcs.firePropertyChange("newRecipe", false, true);
	}
	
	public String getSavePath()
	{
		return savePath;
	}
	
	public List<Unit> getUnits()
	{
		return units;
	}
	
	public void loadUserConfig()
	{
		cfg = UserConfig.loadUserConfig(savePath + "\\Rezepte" + "\\user_config.cfg");
		units.addAll(cfg.getUnits());	
	}
	
	public void setWindowMain(WindowMain windowMain)
	{
		this.windowMain = windowMain;
	}	
	
	public List<Cookbook> getCookbooks()
	{
		return cookbooks;
	}
	
	public Cookbook getWorkingCB()
	{
		return workingCB;
	}
	
	public Recipe getWorkingRecipe()
	{
		return workingRecipe;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		pcs.removePropertyChangeListener(l);
	}
	
	public void saveCookbook()
	{	
		try (FileOutputStream fos = new FileOutputStream(savePath + "\\Rezepte" + "\\" + workingCB.getTitle() + ".cbk");
			 ObjectOutputStream oos = new ObjectOutputStream(fos))
		{
			oos.writeObject(workingCB);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public void addCookbook(Cookbook cb)
	{
		assert(cb != null);
		cookbooks.add(cb);
		addedCB = !addedCB;
		pcs.firePropertyChange("addedCB", false, true);
	}
	
	public void editRecipe(Recipe r)
	{
		
	}
	
	public void deleteRecipe(Recipe r) throws Exception
	{
		if (r.equals(workingRecipe))
			setWorkingRecipe(null);
		if (!(workingCB.getRecipes().remove(r)))
			throw new Exception("cant find recipe");	 
	}
	
	public void setWorkingCB(Cookbook cb)
	{
		Cookbook oldCookbook = workingCB;
		workingCB = cb;
		pcs.firePropertyChange("workingCB", oldCookbook, workingCB);
	}
	
	public void setWorkingRecipe(Recipe r)
	{
		Recipe oldRecipe = workingRecipe;
		
		// e.g. when a new cookbook got selected
		if (r == null)
		{
			workingRecipe = null;
			pcs.firePropertyChange("workingRecipe", false, true);
			
		}
		else
		{
			workingRecipe = r;
			pcs.firePropertyChange("workingRecipe", oldRecipe, workingRecipe);
		}
	}
	
	public Cookbook findCookbook(Cookbook cookbook) throws Exception
	{
		for(Cookbook cb: cookbooks)
		{
			if (cb.equals(cookbook))
				return cb;
		}
		
		throw new Exception("cant find cookbook");
	}
	
	public String[] getCookbookTitles() throws Exception
	{
		if (cookbooks.isEmpty())
			throw new Exception("cant find any cookbook");
		
		String[] titles = new String[cookbooks.size()];
		
		for(int i = 0; i < titles.length; ++i)
			titles[i] = cookbooks.get(i).getTitle();
		
		return titles;
	}

	// search dir for cookbook files(.cbk)
	public void searchForFiles()
	{	
		long newestModifiedDate = 0;
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(savePath + "\\Rezepte")))
		{
			for(Path file: stream)
			{
				if (file.getFileName().toString().endsWith(".cbk"))
				{
					try (FileInputStream fis = new FileInputStream(savePath + "\\Rezepte" + "\\" + file.getFileName().toString());
						 ObjectInputStream ois = new ObjectInputStream(fis))
					{		
						if (cookbooks.isEmpty())
							cookbooks.add((Cookbook) ois.readObject());
						else
						{
							File f = new File(savePath + "\\Rezepte" + "\\" + file.getFileName().toString());
							if (newestModifiedDate <= f.lastModified())
								cookbooks.add(0, (Cookbook) ois.readObject());
							else
								cookbooks.add((Cookbook) ois.readObject());
							newestModifiedDate = f.lastModified();
						}
						
					}
					catch (Exception e)
					{
						System.err.println(e);
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e)
		{
			// if there is no "Rezepte" dir (first start), create one
			if (e.getClass() == NoSuchFileException.class)
			{
				new File(savePath + "\\Rezepte").mkdirs();
			}
			else
			{
				System.err.println(e);
				e.printStackTrace();
			}
		}
						
		if (cookbooks.isEmpty())
			addCookbook(getExampleCB());
			
		setWorkingCB(cookbooks.get(0));		
	}
	
	public Recipe findRecipe(Recipe recipe) throws Exception
	{	
		for(Recipe r: workingCB.getRecipes())
		{
			if (r.equals(recipe))
				return r;
		}
		
		throw new Exception("cant find recipe");
	}
	
	// if there's no cookbook
	public Cookbook getExampleCB()
	{
		Cookbook cb = new Cookbook("Test cookbook");
		Recipe r1 = new Recipe("Test rezept 1", null, "test ingredient 1", "test cooking 1");
		Recipe r2 = new Recipe("Test rezept 2", null, "test ingredient 2", "test cooking 2");
		Recipe r3 = new Recipe("Test rezept 3", null, "test ingredient 3", "test cooking 3");		
		cb.addRecipe(r1);
		cb.addRecipe(r2);
		cb.addRecipe(r3);

		return cb;
	}
}
