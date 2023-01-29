package CookbookApplication;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WindowImportRecipe extends Window
{
	private TextArea ta;
	private Text txt, txt_error;
	
	
	public WindowImportRecipe(Stage stage, int xRes, int yRes)
	{
		super(stage, xRes, yRes);
	}
	
	// cleanup routine
	public void cleanUp()
	{
		ta.setText("");
		txt_error.setText("");
	}
	
	public void buildLayout()
	{
		initHashMap();
		
		GridPane grid = getGrid();
		
		WindowMain windowMain = (WindowMain) stage.getOwner().getUserData();
		DataManager manager = windowMain.getDataManager();		
		
		txt = new Text(manager.getText("tTxt_importLinkRecipe"));
		
		txt_error = new Text();
		txt_error.setFill(Color.RED);
		txt_error.setVisible(false);
		
		HBox hb0 = new HBox(10, txt, txt_error);
		hb0.setAlignment(Pos.CENTER_LEFT);
		grid.add(hb0, 0, 0);
		
		ta = new TextArea();
		ta.setPrefWidth(xRes * 0.8f);
		ta.setPrefHeight(yRes * 0.75f);
		ta.setWrapText(true);
		
		HBox hb1 = new HBox(10, ta);
		hb1.setAlignment(Pos.CENTER);
		grid.add(hb1, 0, 1);
		
		Button btn_import = new Button(manager.getText("tBtn_importRecipe"));
		btn_import.setOnAction(hmEventHandler.get("import"));
		
		Button btn_cancel = new Button(manager.getText("tBtn_cancel"));
		btn_cancel.setOnAction(hmEventHandler.get("cancel"));
									
		HBox hb2 = new HBox(10, btn_import, btn_cancel);
		hb2.setAlignment(Pos.CENTER_RIGHT);
		grid.add(hb2, 0, 2);
			
		scene = new Scene(grid, xRes, yRes);
		stage.setScene(scene);
	}
	
	@Override
	protected void initHashMap(Object...args)
	{
		// import a recipe
		hmEventHandler.put("import", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									DataManager manager = ((WindowMain) stage.getOwner().getUserData()).getDataManager();
									Recipe newRecipe = new Recipe();
									
									if (ta.getText().startsWith("http"))
									{
										HtmlManager html = ((WindowMain) stage.getOwner().getUserData()).getHtmlManager();
										html.getAndSearchHTML(ta.getText());
												
										try 
										{
											newRecipe = html.buildRecipe();
										} 
										catch (Exception e) 
										{
											e.printStackTrace();
										}
										
										manager.getWorkingCB().addRecipe(newRecipe);			
										manager.setWorkingRecipe(newRecipe);
									}
									else
									{
										try
										{					
											manager.getWorkingCB().addRecipe(DataManager.decodeRecipe(DataManager.getDataFromClipboard(), manager.getWorkingCB()));
											manager.setWorkingRecipe(manager.getWorkingCB().getRecipes().get(manager.getWorkingCB().getRecipes().size() - 1));
										}
										catch (Exception e)
										{
											System.err.println(e);
											e.printStackTrace();
										}	
									}
								
									manager.flipNewRecipe();
									
									stage.close();
									cleanUp();
								}
							});		
		
		// cancel
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
