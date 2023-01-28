package CookbookApplication;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;


public class WindowNewRecipe extends WindowRecipe
{
	public WindowNewRecipe(Stage stage, int xRes, int yRes)
	{
		super(stage, xRes, yRes);
	}	
		
	@Override
	protected void initHashMap(Object...args)
	{
		hmEventHandler.put("comboBox_recipe", new EventHandler <ActionEvent>()
										{
											@Override
											public void handle(ActionEvent event)
											{
												Recipe item = comboBox_recipe.getSelectionModel().getSelectedItem();
												if (item != null)
												{
													try
													{
														loadRecipeData(windowMain.getDataManager().findRecipe(item));
													}
													catch (Exception e)
													{
														System.err.println(e);
														e.printStackTrace();
													}
												}
											}
										});
		
		hmEventHandler.put("addIngredient", new EventHandler<ActionEvent>()
									{
										@Override
										public void handle(ActionEvent event)
										{
											String value = tf_amount.getText() + comboBox_unit.getSelectionModel().getSelectedItem() + " " + tf_ingredient.getText();
											if (ta_ingredients.getText().isEmpty())
												ta_ingredients.setText(value);
											else
												ta_ingredients.setText(ta_ingredients.getText() + "\n" + value);							
										}
									});
		
		hmEventHandler.put("save", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{	
									Recipe newRecipe = new Recipe();
									Recipe baseRecipe = comboBox_recipe.getSelectionModel().getSelectedItem();
									String title = tf_title.getText();
									String ingredients = ta_ingredients.getText();
									String cooking = ta_cooking.getText();
									
									if (validateInput(title, ingredients, cooking))
									{
										if (baseRecipe == null)
											newRecipe = new Recipe(title, null, ingredients, cooking);
										else
											newRecipe = new Recipe(title, baseRecipe, ingredients, cooking);
										
										windowMain.getDataManager().getWorkingCB().addRecipe(newRecipe);			
										windowMain.getDataManager().setWorkingRecipe(newRecipe);
										windowMain.getDataManager().flipNewRecipe();
		
										stage.close();
										cleanUp();					
										}								
									}
							});	
							
		
		hmEventHandler.put("cancel", new EventHandler<ActionEvent>()
								{
									@Override
									public void handle(ActionEvent event)
									{
										stage.close();
										cleanUp();
									}						
								});
	}
	
}
