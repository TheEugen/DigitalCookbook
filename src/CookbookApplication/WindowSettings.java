package CookbookApplication;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class WindowSettings extends Window
{
	public class WindowCustomUnit extends WindowSettings
	{	
		private TextField tf = new TextField();
		private Text txt_error = new Text();
		private Text txt;
		
		public WindowCustomUnit(Stage stage, int xRes, int yRes)
		{
			super(stage, xRes, yRes);
		}

		@Override
		public void buildLayout()
		{
			GridPane grid = getGrid();
			
			WindowMain windowMain = (WindowMain) ((WindowSettings) stage.getOwner().getUserData()).getStage().getOwner().getUserData();
			DataManager dataManager = windowMain.getDataManager();
			
			initHashMap(windowMain, dataManager);
			
			// SAVE button
			Button btn_save = new Button(dataManager.getText("tBtn_save"));
			btn_save.setOnAction(hmEventHandler.get("save"));
			
			// CANCEL button
			Button btn_cancel = new Button(dataManager.getText("tBtn_cancel"));
			btn_cancel.setOnAction(hmEventHandler.get("cancel"));
			
			txt = new Text(dataManager.getText("tTxt_addCustomUnit"));
			
			// HBoxes
			HBox hb0 = new HBox(10, txt);
			grid.add(hb0, 0, 0);
			
			HBox hb1 = new HBox(10, tf);
			grid.add(hb1, 0, 1);
		
			HBox hb2 = new HBox(10, btn_save, btn_cancel);
			grid.add(hb2, 0, 2);
			
			Scene scene = new Scene(grid, xRes, yRes);
			stage.setScene(scene);
		}
			
		@Override
		protected void initHashMap(Object...args)
		{
			WindowMain windowMain = (WindowMain) args[0];
			DataManager dataManager = (DataManager) args[1];
			
			// save new custom unit
			hmEventHandler.put("save", new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					if(validateInput())
					{
						dataManager.getUserConfig().getUnits().add(new Unit(tf.getText()));
						windowMain.getMainUI().getWindowNewRecipe().updateObsvListUnit();
						windowMain.getMainUI().getWindowEditRecipe().updateObsvListUnit();
					}
						
					stage.close();
				}
			});
			
			// close the custom unit window
			// TODO: ask discard
			hmEventHandler.put("cancel", new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					stage.close();
				}
			});
		}
		
		public boolean validateInput()
		{
			return true;
		}
		
		public void cleanUp()
		{
			txt_error.setText("");
		}
	}
	
	
	
	private String workingLang;
	
	
	public WindowSettings(Stage stage, int xRes, int yRes)
	{
		super(stage, xRes, yRes);
	}
	
	public void show(String s)
	{
		workingLang = s;
		stage.show();
	}

	public void buildLayout()
	{
		GridPane grid = getGrid();
		stage.setUserData(this);
		
		// windowmain and datamanager reference
		WindowMain windowMain = (WindowMain) stage.getOwner().getUserData();
		DataManager dataManager = windowMain.getDataManager();
		
		// suppress cb delete warning checkbox
		CheckBox check_suppress_cb = new CheckBox();
		check_suppress_cb.setOnAction(hmEventHandler.get("suppress_cb"));
		check_suppress_cb.setSelected(dataManager.getUserConfig().getSuppressWarningCookbook());
		
		// suppress recipe delete warning checkbox
		CheckBox check_suppress_r = new CheckBox();
		check_suppress_r.setOnAction(hmEventHandler.get("suppress_r"));
		check_suppress_r.setSelected(dataManager.getUserConfig().getSuppressWarningRecipe());
		
		// save location directory chooser
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(dataManager.getText("tFC_savePath"));
		
		// language combobox
		ComboBox<String> comboBox_lang = new ComboBox<String>();
		ObservableList<String> options = FXCollections.observableArrayList(dataManager.getUserConfig().getLanguages());	
		comboBox_lang.setItems(options);
		comboBox_lang.getSelectionModel().select(dataManager.getUserConfig().getWorkingLanguage());
		
		// texts
		Text txt_suppress_cb = new Text(dataManager.getText("tTxt_cfgSuppressCookbook"));
		Text txt_suppress_r = new Text(dataManager.getText("tTxt_cfgSuppressRecipe"));
		Text txt_lang = new Text(dataManager.getText("tTxt_lang"));
		
		// ADD UNIT button
		Button btn_addUnit = new Button(dataManager.getText("tBtn_addUnit"));
		btn_addUnit.setOnAction(hmEventHandler.get("addUnit"));
		
		// CHOOSE DIRECTORY button
		Button btn_dirChooser = new Button(dataManager.getText("tBtn_savePath"));
		btn_dirChooser.setOnAction(hmEventHandler.get("dirChooser"));
		
		// SAVE button
		Button btn_save = new Button(dataManager.getText("tBtn_save"));
		btn_save.setOnAction(hmEventHandler.get("save"));
		
		// CANCEL button
		Button btn_cancel = new Button(dataManager.getText("tBtn_cancel"));
		btn_cancel.setOnAction(hmEventHandler.get("cancel"));
		
		// init hashmap
		initHashMap(windowMain, dataManager, check_suppress_cb, check_suppress_r, dirChooser, comboBox_lang);
		
		// create HBox and add to grid
		HBox hbR0C0 = new HBox(10, txt_suppress_cb, check_suppress_cb);	
		grid.add(hbR0C0, 0, 0);
		
		HBox hbR1C0 = new HBox(10, txt_suppress_r, check_suppress_r);
		grid.add(hbR1C0, 0, 1);
		
		HBox hbR2C0 = new HBox(10, btn_addUnit);
		grid.add(hbR2C0, 0, 2);

		HBox hbR3C0 = new HBox(10, txt_lang, comboBox_lang);
		grid.add(hbR3C0, 0, 3);
			
		HBox hbR4C0 = new HBox(10, btn_dirChooser);
		grid.add(hbR4C0, 0, 4);
		
		HBox hbR5C0 = new HBox(10, btn_save, btn_cancel);
		hbR5C0.setAlignment(Pos.CENTER_RIGHT);
		grid.add(hbR5C0, 0, 5);
		
		scene = new Scene(grid, xRes, yRes);
		stage.setScene(scene);	
	}
	
	@Override
	protected void initHashMap(Object...args)
	{
		WindowMain windowMain = (WindowMain) args[0];
		DataManager dataManager = (DataManager) args[1];
		CheckBox c_cb = (CheckBox) args[2];
		CheckBox c_r = (CheckBox) args[3];
		DirectoryChooser dirChooser = (DirectoryChooser) args[4];
		@SuppressWarnings("unchecked")
		ComboBox<String> lang = (ComboBox<String>) args[5];
		
		// suppress delete cookbook warning
		hmEventHandler.put("suppress_cb", new EventHandler<ActionEvent>()
										{
											@Override 
											public void handle(ActionEvent event)
											{
												dataManager.getUserConfig().setSuppressWarningCookbook(c_cb.isSelected());
												
											}
										});
		
		// suppress delete recipe warning
		hmEventHandler.put("suppress_r", new EventHandler<ActionEvent>()
										{
											@Override 
											public void handle(ActionEvent event)
											{
												dataManager.getUserConfig().setSuppressWarningRecipe(c_r.isSelected());
											}
										});
		
		// show add custom unit window
		hmEventHandler.put("addUnit", new EventHandler<ActionEvent>()
								{
									@Override
									public void handle(ActionEvent event)
									{
										windowMain.getMainUI().showWindowCustomUnit();
									}
								});

		// show dialog box to select save location for cookbooks and config files
		hmEventHandler.put("dirChooser", new EventHandler<ActionEvent>()
									{
										@Override
										public void handle(ActionEvent event)
										{
											File file = dirChooser.showDialog(stage);
											if(file.isDirectory())
												dataManager.getUserConfig().setSavePath(file.getAbsolutePath());
										}
									});
		
		// save settings
		hmEventHandler.put("save", new EventHandler<ActionEvent>()
										{
											@Override
											public void handle(ActionEvent event)
											{
												String selected = lang.getSelectionModel().getSelectedItem();
												dataManager.getUserConfig().setWorkingLanguage(selected);
												UserConfig.saveUserConfig(dataManager.getUserConfig(), dataManager.getSavePath() + "\\Rezepte\\");
												stage.close();
												
												if(!(workingLang.equals(selected)))
													windowMain.getMainUI().update(windowMain.getStage());
											}
										});
		
		// cancel
		// TODO: discard
		hmEventHandler.put("cancel", new EventHandler<ActionEvent>()
										{
											@Override
											public void handle(ActionEvent event)
											{
												lang.getSelectionModel().select(dataManager.getUserConfig().getWorkingLanguage());
												stage.close();
											}
										});
	}

	
}
