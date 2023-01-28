package CookbookApplication;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public abstract class WindowRecipe extends Window
{
	protected WindowMain windowMain;
	protected DataManager manager;
	private Text txt_error = new Text();
	
	protected TextField tf_title = new TextField();
	protected TextField tf_amount = new TextField();
	protected TextField tf_ingredient = new TextField();
	protected TextArea ta_ingredients = new TextArea();
	protected TextArea ta_cooking = new TextArea();
	
	private ObservableList<Recipe> options_recipe;
	protected ComboBox<Recipe> comboBox_recipe;
	private ObservableList<String> options_unit;
	protected ComboBox<String> comboBox_unit;
	
	protected abstract void initHashMap(Object...args);
	
	
	public WindowRecipe(Stage stage, int xRes, int yRes)
	{
		super(stage, xRes, yRes);
	}
	
	public void updateObsvList()
	{
		if (windowMain.getDataManager().getWorkingCB() != null)
		{
			if (windowMain.getDataManager().getWorkingCB().getRecipes().isEmpty())
				options_recipe = FXCollections.observableArrayList();
			else
				options_recipe = FXCollections.observableArrayList(windowMain.getDataManager().getWorkingCB().getRecipes());			
		}
		else
			options_recipe = FXCollections.observableArrayList();
			
		comboBox_recipe.setItems(options_recipe);
	}
	
	public boolean validateInput(String title, String ingredients, String cooking)
	{
		if (ingredients.length() == 0 && cooking.length() == 0)
		{
			txt_error.setText(windowMain.getDataManager().getText("tTxt_recipeEmpty"));
			return false;
		}
		
		if (title.length() > 0)
		{
			if (title.length() <= windowMain.getDataManager().getRecipeMaxTitleLength())
			{
				if (getClass() != WindowEditRecipe.class)
				{
					if ((windowMain.getDataManager().titleExists(Recipe.class, title)))
					{	
						txt_error.setText(windowMain.getDataManager().getText("tTxt_recipeTitleExists"));
						return false;
					}					
					else
						return true;	
				}
				else
				{
					// show popup warning overwriting
					return true;
				}
			}	
			else
			{
				txt_error.setText(windowMain.getDataManager().getText("tTxt_TitleLength"));
				return false;
			}				
		}

		txt_error.setText(windowMain.getDataManager().getText("tTxt_TitleEmpty"));	
		return false;
	}
	
	public void loadRecipeData(Recipe r)
	{
		tf_title.setText(r.getTitle());
		ta_ingredients.setText(r.getIngredients());
		ta_cooking.setText(r.getCooking());
		if (r.getBaseRecipe() != null)
			comboBox_recipe.getSelectionModel().select(r);
	}
	
	public void cleanUp()
	{
		List<Field> fields = new ArrayList<Field>();
		if (getClass() == WindowEditRecipe.class)
			fields = DataManager.getAllFields(WindowEditRecipe.class);
		else
			fields = Arrays.asList(getClass().getDeclaredFields());
		
		Object obj = new Object();
		
		for (Field f: fields)
		{		
			try 
			{			
				obj = f.get(this);

				if (obj == null)
					throw new Exception("Uninitialized field");
				
				if (obj.getClass() == TextField.class)
					((TextField) obj).setText("");
				else if (obj.getClass() == TextArea.class)
					((TextArea) obj).setText("");
				else if (obj.getClass() == Text.class)
					((Text) obj).setText("");
				else
					continue;		
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
				
		}

		comboBox_recipe.setValue(null);
		comboBox_unit.getSelectionModel().select(0);
	}
	
	public void buildLayout()
	{	
		initHashMap();
		
		windowMain = (WindowMain) stage.getOwner().getUserData();
		manager = windowMain.getDataManager();
		GridPane grid = getGrid();
		
		txt_error.setFill(Color.RED);
		
		HBox hbR0C0 = new HBox(10);
		hbR0C0.setAlignment(Pos.CENTER);
		hbR0C0.getChildren().add(txt_error);
		grid.add(hbR0C0, 0, 0);
		
		HBox hbR1C0 = new HBox(10);
		hbR1C0.setAlignment(Pos.CENTER_LEFT);
		
		tf_title.setPrefWidth(250);
		
		options_recipe = FXCollections.observableArrayList(windowMain.getDataManager().getWorkingCB().getRecipes());	
		comboBox_recipe = new ComboBox<Recipe>(options_recipe);
		comboBox_recipe.setOnAction(hmEventHandler.get("comboBox_recipe"));
		
		hbR1C0.getChildren().add(new Text(windowMain.getDataManager().getText("tTxt_titleOfRecipe")));
		hbR1C0.getChildren().add(tf_title);
		grid.add(hbR1C0, 0, 1);
		
		HBox hbR1C1 = new HBox(10);
		hbR1C1.setAlignment(Pos.CENTER_LEFT);
		hbR1C1.getChildren().add(new Text(windowMain.getDataManager().getText("tTxt_basedOn")));
		hbR1C1.getChildren().add(comboBox_recipe);
		grid.add(hbR1C1, 1, 1);
		
		HBox hbR2C0 = new HBox(10);
		tf_amount.setPrefWidth(40);
		
		List<String> s_units = new ArrayList<String>();
		// delete exception getUnitStrings()
		try
		{
			s_units = windowMain.getDataManager().getUserConfig().getUnitStrings();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		options_unit = FXCollections.observableArrayList(s_units);	
		comboBox_unit = new ComboBox<String>(options_unit);
		comboBox_unit.getSelectionModel().selectFirst();
		
		tf_ingredient.setPrefWidth(100);
		Button btn_addIngredient = new Button (windowMain.getDataManager().getText("tBtn_add"));
		btn_addIngredient.setOnAction(hmEventHandler.get("addIngredient"));
				
		hbR2C0 .getChildren().add(tf_amount);
		hbR2C0 .getChildren().add(comboBox_unit);
		hbR2C0 .getChildren().add(new Text(windowMain.getDataManager().getText("tTxt_of")));
		hbR2C0 .getChildren().add(tf_ingredient);
		hbR2C0 .getChildren().add(btn_addIngredient);
		grid.add(hbR2C0 , 0, 2);
				
		HBox hbR3C0 = new HBox(10);
		ta_ingredients.setPrefWidth(350);
		ta_ingredients.setPrefHeight(500);
		
		hbR3C0.getChildren().add(ta_ingredients);
		grid.add(hbR3C0, 0, 3);
		
		HBox hbR3C1 = new HBox(10);
		hbR3C1.getChildren().add(ta_cooking);
		grid.add(hbR3C1, 1, 3);
		
		HBox hbR4C1 = new HBox(10);
		hbR4C1.setAlignment(Pos.CENTER_RIGHT);
		Button btn_save = new Button (windowMain.getDataManager().getText("tBtn_save"));
		btn_save.setOnAction(hmEventHandler.get("save"));
		
		Button btn_cancel = new Button (windowMain.getDataManager().getText("tBtn_cancel"));
		btn_cancel.setOnAction(hmEventHandler.get("cancel"));
		
		hbR4C1.getChildren().add(btn_save);
		hbR4C1.getChildren().add(btn_cancel);
		grid.add(hbR4C1, 1, 4);
		
		scene = new Scene(grid, xRes, yRes);
		stage.setScene(scene);
	}
	
	public void updateObsvListUnit()
	{
		List<String> s_units = new ArrayList<String>();
		try
		{
			s_units = windowMain.getDataManager().getUserConfig().getUnitStrings();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}	
		
		try
		{
			options_unit = FXCollections.observableArrayList(s_units);
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		
		comboBox_unit.setItems(options_unit);
	}
}
