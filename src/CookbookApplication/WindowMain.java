package CookbookApplication;


import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.collections.ObservableList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;



public class WindowMain extends Window
{	
	private final float CONST_HEIGHT = 0.78f;
	private final float CONST_WIDTH_LV = 0.25f;
	private final float CONST_WIDTH_TA_I = 0.25f;
	private final float CONST_WIDTH_TA_C = 0.5f;
	
	private final Main main;
	private final DataManager dataManager;
	private final HtmlManager htmlManager;
	
	private ListView<Recipe> listView = new ListView<Recipe>();
	private ObservableList<Recipe> items = FXCollections.observableArrayList();
	private ComboBox<Cookbook> comboBox = new ComboBox<Cookbook>();
	private ObservableList<Cookbook> options = FXCollections.observableArrayList();	
	
	private TextArea ingredients = new TextArea();
	private TextArea cooking = new TextArea();

	// react to window resize
	private ChangeListener<Number> listener = new ChangeListener<Number>()
											{
												@Override
												public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
												{
													listView.setPrefWidth(xRes * CONST_WIDTH_LV);
													listView.setPrefHeight(yRes * CONST_HEIGHT);
													ingredients.setPrefWidth(xRes * CONST_WIDTH_TA_I);
													ingredients.setPrefHeight(yRes * CONST_HEIGHT);
													cooking.setPrefWidth(xRes * CONST_WIDTH_TA_C);
													cooking.setPrefHeight(yRes * CONST_HEIGHT);
												}
											};
	
	
	public WindowMain(Stage stage, int xRes, int yRes, Main main, DataManager dataManager, HtmlManager htmlManager)
	{
		super(stage, xRes, yRes);
		this.main = main;
		this.dataManager = dataManager;
		this.htmlManager = htmlManager;
	}
	
	
	public ListView<Recipe> getListView()
	{
		return listView;
	}
	
	public Main getMainUI()
	{
		return main;
	}
	
	public DataManager getDataManager()
	{
		return dataManager;
	}
	
	public HtmlManager getHtmlManager()
	{
		return htmlManager;
	}
	
	public ComboBox<Cookbook> getComboBox()
	{
		return comboBox;
	}

	public void buildLayout()
	{	
		initHashMap();
		
		GridPane grid = getGrid();

		stage.widthProperty().addListener(listener);
		stage.heightProperty().addListener(listener);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() 
								{
									@Override
									public void handle(WindowEvent event)
									{
										main.getJSONHandler().setRunning(false);
										//main.getT1().interrupt();
									}
								});
		
		try
		{
			options = FXCollections.observableArrayList(dataManager.getCookbooks());
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		comboBox = new ComboBox<Cookbook>(options);
		comboBox.setOnAction(hmEventHandler.get("comboBox"));	
		comboBox.getSelectionModel().selectFirst();	
		
		// SAVE button
		Button btn_save = new Button (dataManager.getText("tBtn_save"));
		btn_save.setOnAction(hmEventHandler.get("save"));	
		
		// NEW COOKBOOK button
		Button btn_newCB = new Button(dataManager.getText("tBtn_newCB"));
		btn_newCB.setOnAction(hmEventHandler.get("newCB"));

		// DELETE COOKBOOK button
		Button btn_deleteCB = new Button(dataManager.getText("tBtn_delete"));
		btn_deleteCB.setOnAction(hmEventHandler.get("deleteCookbook"));

		// SETTINGS button
		Button btn_settings = new Button (dataManager.getText("tBtn_settings"));		
		btn_settings.setOnAction(hmEventHandler.get("settings"));
				
		// NEW RECIPE button
		Button btn_newRecipe = new Button (dataManager.getText("tBtn_newRecipe"));
		btn_newRecipe.setOnAction(hmEventHandler.get("newRecipe"));	
		
		// IMPORT RECIPE button
		Button btn_importRecipe = new Button(dataManager.getText("tBtn_importRecipe"));
		btn_importRecipe.setOnAction(hmEventHandler.get("importRecipe"));
		
		// EXPORT RECIPE button
		Button btn_exportRecipe = new Button(dataManager.getText("tBtn_exportRecipe"));
		btn_exportRecipe.setOnAction(hmEventHandler.get("exportRecipe"));
		
		// EDIT RECIPE button
		Button btn_editRecipe = new Button(dataManager.getText("tBtn_edit"));
		btn_editRecipe.setDisable(true);
		btn_editRecipe.setOnAction(hmEventHandler.get("editRecipe"));
		
		// DELETE RECIPE button
		Button btn_deleteRecipe = new Button(dataManager.getText("tBtn_delete"));
		btn_deleteRecipe.setDisable(true);
		btn_deleteRecipe.setOnAction(hmEventHandler.get("deleteRecipe"));
		
		// EXIT button
		Button btn_exit = new Button(dataManager.getText("tBtn_exit"));
		btn_exit.setOnAction(hmEventHandler.get("exit"));
		
		// create HBox, set Alignment, add HBox to grid
		HBox hbBtn_row0_left = new HBox(10, comboBox, btn_save, btn_deleteCB);
		grid.add(hbBtn_row0_left, 0, 0);

		HBox hbBtn_row0_center = new HBox(10, btn_newCB);
		hbBtn_row0_center.setAlignment(Pos.CENTER_LEFT);
		grid.add(hbBtn_row0_center, 1, 0);

		HBox hbBtn_row0_right = new HBox(10, btn_settings);
		hbBtn_row0_right.setAlignment(Pos.CENTER_RIGHT);
		grid.add(hbBtn_row0_right, 2, 0);

		HBox hbBtn_row3 = new HBox(10, btn_newRecipe, btn_importRecipe, btn_exportRecipe);
		hbBtn_row3.setAlignment(Pos.CENTER_LEFT);
		grid.add(hbBtn_row3, 0, 2);

		HBox hbR2C1 = new HBox(10);
		hbR2C1.setAlignment(Pos.CENTER_LEFT);
		grid.add(hbR2C1, 1, 2);

		HBox hbR4C0 = new HBox(10, btn_editRecipe, btn_deleteRecipe);
		hbR4C0.setAlignment(Pos.CENTER_LEFT);
		grid.add(hbR4C0, 0, 4);

		HBox hbR4C2 = new HBox(10, btn_exit);
		hbR4C2.setAlignment(Pos.CENTER_RIGHT);
		grid.add(hbR4C2, 2, 4);

		// RECIPE LIST textarea
		listView.setItems(items);
		listView.setOnMouseClicked(new EventHandler<MouseEvent>()
								{
									@Override
									public void handle(MouseEvent event)
									{
										Recipe item = listView.getSelectionModel().getSelectedItem();
										try
										{
											if (item != null)
											{
												dataManager.setWorkingRecipe(dataManager.findRecipe(item));
												btn_editRecipe.setDisable(false);
												btn_deleteRecipe.setDisable(false);
											}
											else
											{
												btn_editRecipe.setDisable(true);
												btn_deleteRecipe.setDisable(true);
											}
										}
										catch (Exception e)
										{
											System.err.println(e);
											e.printStackTrace();
										}
									}
								});	
		listView.setPrefWidth(xRes * CONST_WIDTH_LV);
		listView.setPrefHeight(yRes * CONST_HEIGHT);
		grid.add(listView, 0, 3);
		
		// INGREDIENTS textarea
		ingredients.setPrefWidth(xRes * CONST_WIDTH_TA_I);
		ingredients.setPrefHeight(yRes * CONST_HEIGHT);
		ingredients.setWrapText(true);
		ingredients.setEditable(false);
		grid.add(ingredients, 1, 3);
		
		// INSTRUCTIONS textarea
		cooking.setPrefWidth(xRes * CONST_WIDTH_TA_C);
		cooking.setPrefHeight(yRes * CONST_HEIGHT);	
		cooking.setWrapText(true);
		cooking.setEditable(false);
		grid.add(cooking, 2, 3);

		// create scene with our new grid
		scene = new Scene(grid, xRes, yRes);
		
		// give stage reference to scene
		stage.setScene(scene);	
	}
	
	// updating textfields in case something changed
	public void updateTextFields()
	{
		if (dataManager.getWorkingRecipe() == null)
		{
			ingredients.setText("");
			cooking.setText("");
		}
		else
		{
			ingredients.setText(dataManager.getWorkingRecipe().getIngredients());
			cooking.setText(dataManager.getWorkingRecipe().getCooking());
		}
	}
	
	// update cookbook list routine
	public void updateObsvListCB()
	{
		try
		{
			options = FXCollections.observableArrayList(dataManager.getCookbooks());
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		comboBox.setItems(options);
	}
	
	// update recipe list routine
	public void updateObsvListLV()
	{		
		if (dataManager.getWorkingCB() != null)
		{
			if (dataManager.getWorkingCB().getRecipes().isEmpty())
				items = FXCollections.observableArrayList();
			else
				items = FXCollections.observableArrayList(dataManager.getWorkingCB().getRecipes());
						
		}
		else
			items = FXCollections.observableArrayList();
		
		
		listView.setItems(items);		
	}	
	
	@Override
	protected void initHashMap(Object...args)
	{
		// select a cookbook
		hmEventHandler.put("comboBox", new EventHandler<ActionEvent>() 
								{
						            @Override
						            public void handle(ActionEvent event) 
						            {
						            	if (comboBox.getSelectionModel().getSelectedItem() != null)
							            {
							            	Cookbook cb = new Cookbook();
							            	try
							            	{
							            		cb = dataManager.findCookbook(comboBox.getSelectionModel().getSelectedItem());
							            	}
							            	catch (Exception e)
							            	{
							            		System.err.println(e);
							            	}
							            	
							            	if (cb.getTitle() != null)
							            	{
							            		if (!(cb.getTitle().equals(dataManager.getWorkingCB().getTitle())))
							            		{
							            			dataManager.setWorkingCB(cb);
							            		}
							            	}
							            }
						            }
						        });
		
		// save cookbook
		hmEventHandler.put("save", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									dataManager.saveCookbook();															
								}
							});
		
		// add new cookbook
		hmEventHandler.put("newCB", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									main.showWindowNewCB();
								}
							});	

		// delete cookbook
		hmEventHandler.put("deleteCookbook", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									if (!(dataManager.getUserConfig().getSuppressWarningCookbook()))
										main.showWindowWarning(Cookbook.class);		
									else
										dataManager.deleteCB();
								}
							});
		
		
		// show settings window
		hmEventHandler.put("settings", new EventHandler<ActionEvent>()
								{
									@Override
									public void handle(ActionEvent event)
									{
										main.showWindowSettings(dataManager.getUserConfig().getWorkingLanguage());
									}
								});
		
		// show new recipe window
		hmEventHandler.put("newRecipe", new EventHandler<ActionEvent>()
								{
									@Override
									public void handle(ActionEvent event)
									{
										main.showWindowNewRecipe();
									}
								});
		
		// show import recipe window
		hmEventHandler.put("importRecipe", new EventHandler<ActionEvent>()
									{
										@Override
										public void handle(ActionEvent event)
										{		
											main.showWindowImportRecipe();		
										}
									});	

		// copy encoded recipe to clipboard
		hmEventHandler.put("exportRecipe", new EventHandler<ActionEvent>()
									{
										@Override
										public void handle(ActionEvent event)
										{
											if(dataManager.getWorkingRecipe() != null)
											{
												try
												{
													DataManager.copyToClipboard(DataManager.encodeRecipe(dataManager.getWorkingRecipe()));
												}
												catch (Exception e)
												{
													System.err.println(e);
													e.printStackTrace();
												}
											}							
										}
									});	
		
		// show edit recipe window
		hmEventHandler.put("editRecipe", new EventHandler<ActionEvent>()
									{
										@Override
										public void handle(ActionEvent event)
										{
											if(dataManager.getWorkingRecipe() != null)
												main.showWindowEditRecipe();
										}
									});

		// delete selected recipe
		hmEventHandler.put("deleteRecipe", new EventHandler<ActionEvent>()
									{
										@Override
										public void handle(ActionEvent event)
										{
											if(dataManager.getWorkingRecipe() != null)
											{
												if (!(dataManager.getUserConfig().getSuppressWarningRecipe()))
													main.showWindowWarning(Recipe.class);
												else
												{
													dataManager.getWorkingCB().deleteRecipe(dataManager.getWorkingRecipe());
													dataManager.flipDeletedRecipe();
												}
											}
										}
									});
		
		// close the program
		hmEventHandler.put("exit", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									main.getJSONHandler().setRunning(false);
									stage.close();
								}
							});

	}

}
