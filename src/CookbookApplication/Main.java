package CookbookApplication;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;


///////////////////////////////////////////
/*
					TODO				

add resize functionality
implement custom unit delete option
display warning when exiting with unsaved (cookbook and config) changes

far far away:

checking for updates
user-system

maybe:

add menu bar
cleanUp interface


					NOTES

thread-safety:
CopyOnWriteArrayList
Collections.synchronizedList

*/
///////////////////////////////////////////


public class Main extends Application
{
	private WindowMain windowMain;
	private WindowNewCookbook windowNewCB;
	private WindowNewRecipe windowNewRecipe;
	private WindowEditRecipe windowEditRecipe;
	private WindowImportRecipe windowImportRecipe;
	private WindowSettings windowSettings;
	private WindowSettings.WindowCustomUnit windowCustomUnit;
	private WindowWarning windowWarning;
	
	private Stage stageNewCB, stageNewRecipe, stageSettings, stageCustomUnit, stageEditRecipe, stageImportRecipe, stageWarning;
	private DataManager dataManager;
	private JSONHandler jsonHandler;
	
	private HtmlManager htmlManager;
	
	private Thread tReadInput;
	
	public DataManager getDataManager()				{	return dataManager;			}
	public WindowNewRecipe getWindowNewRecipe()		{	return windowNewRecipe;		}
	public WindowEditRecipe getWindowEditRecipe()	{	return windowEditRecipe;	}
	public JSONHandler getJSONHandler()				{	return jsonHandler;			}
	public HtmlManager getHtmlManager()				{	return htmlManager;			}
	
	public void showWindowCustomUnit()
	{
		windowCustomUnit.show();
	}
	
	public void showWindowWarning(Class<?> cl)
	{
		windowWarning.show(cl);
	}
	
	public void showWindowImportRecipe()
	{
		windowImportRecipe.show();
	}
	
	public void showWindowSettings(String workingLang)
	{	
		windowSettings.show(workingLang);
	}
	
	public void showWindowEditRecipe()
	{
		windowEditRecipe.loadRecipeData(dataManager.getWorkingRecipe());
		windowEditRecipe.show();
	}
	
	public void showWindowNewRecipe()
	{		
		windowNewRecipe.show();
	}
	
	public void showWindowNewCB()
	{		
		windowNewCB.show();
	}

	public void configureStage(Stage stage, Window userData, String title, Modality modality, Stage owner, String icon)
	{
		stage.setUserData(userData);
		stage.setTitle(title);
		if (modality != null)
			stage.initModality(Modality.APPLICATION_MODAL);
		if (owner != null)
			stage.initOwner(owner);
		if (icon != null)
			stage.getIcons().add(new Image(Window.class.getClassLoader().getResourceAsStream(icon)));
	}
	
	public void initStages(Stage primaryStage)
	{
		stageNewCB = new Stage();
		stageNewRecipe = new Stage();
		stageEditRecipe = new Stage();
		stageImportRecipe = new Stage();
		stageSettings = new Stage();
		stageCustomUnit = new Stage();
		stageWarning = new Stage();
		
		configureStage(primaryStage, windowMain, dataManager.getText("tWindowMain"), null, null, "book.png");
		configureStage(stageNewCB, windowNewCB, dataManager.getText("tWindowNewCB"), Modality.APPLICATION_MODAL, primaryStage, null);
		configureStage(stageNewRecipe, windowNewRecipe, dataManager.getText("tWindowNewRecipe"), Modality.APPLICATION_MODAL, primaryStage, null);
		configureStage(stageEditRecipe, windowEditRecipe, dataManager.getText("tWindowEditRecipe"), Modality.APPLICATION_MODAL, primaryStage, null);
		configureStage(stageImportRecipe, windowImportRecipe, dataManager.getText("tWindowImportRecipe"), Modality.APPLICATION_MODAL, primaryStage, null);
		configureStage(stageSettings, windowSettings, dataManager.getText("tWindowSettings"), Modality.APPLICATION_MODAL, primaryStage, "config_1.png");
		configureStage(stageCustomUnit, windowCustomUnit, dataManager.getText("tWindowCustomUnit"), Modality.APPLICATION_MODAL, stageSettings, null);
		configureStage(stageWarning, windowWarning, dataManager.getText("tWindowWarning"), Modality.APPLICATION_MODAL, primaryStage, "alert_512px.png");
	}
	
	public void init(Stage primaryStage)
	{				
		// get user resolution
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		
		// init data manager
		dataManager = new DataManager(System.getProperty("user.home"));	
		
		// init html manager
		htmlManager = new HtmlManager();
	
		// need valid windowmain obj to init data manager
		windowMain = new WindowMain(primaryStage, (int) (screenBounds.getMaxX() / 1.5f), (int) (screenBounds.getMaxY() / 1.5f), this, dataManager, htmlManager);
		
		// init data manager, needs to be called before building layouts
		dataManager.init(windowMain);
			
		// init and configure the stages
		initStages(primaryStage);
		
		// create the window objects
		windowNewCB = new WindowNewCookbook(stageNewCB, (int) screenBounds.getMaxX() / 5, (int) screenBounds.getMaxY() / 6);
		windowNewRecipe = new WindowNewRecipe(stageNewRecipe, (int) screenBounds.getMaxX() / 2, (int) screenBounds.getMaxY() / 2);
		windowEditRecipe = new WindowEditRecipe(stageEditRecipe, (int) screenBounds.getMaxX() / 2, (int) screenBounds.getMaxY() / 2);
		windowImportRecipe = new WindowImportRecipe(stageImportRecipe, (int) screenBounds.getMaxX() / 4, (int) screenBounds.getMaxY() / 4);
		windowSettings = new WindowSettings(stageSettings, (int) screenBounds.getMaxX() / 4, (int) screenBounds.getMaxY() / 4);
		windowCustomUnit = windowSettings.new WindowCustomUnit(stageCustomUnit, (int) screenBounds.getMaxX() / 6, (int) screenBounds.getMaxY() / 12);
		windowWarning = new WindowWarning(stageWarning, (int) screenBounds.getMaxX() / 6, (int) screenBounds.getMaxY() / 12);
		
		// build window layouts
		windowMain.buildLayout();		
		windowNewCB.buildLayout();		
		windowNewRecipe.buildLayout();	
		windowEditRecipe.buildLayout();
		windowImportRecipe.buildLayout();
		windowSettings.buildLayout();
		windowCustomUnit.buildLayout();
		windowWarning.buildLayout();
		
		// init jsonhandler for communication with web-extension
		jsonHandler = new JSONHandler(this);
		
		// threading to avoid IO block
		tReadInput = new Thread(jsonHandler);
		tReadInput.start();
			
		// at this point the program becomes visible to the user
		windowMain.show();
	}
	
	public void update(Stage primaryStage)
	{
		init(primaryStage);
	}
	
	@Override     
	public void start(Stage primaryStage) throws Exception 
	{
		init(primaryStage);	
	}   
	
	public static void main(String args[])
	{           
		Debug.enable(false);
		launch(args);
	} 
}