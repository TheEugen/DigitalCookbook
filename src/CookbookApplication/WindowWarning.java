package CookbookApplication;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WindowWarning extends Window
{
	private Text txt;
	private Class<?> cl;

	public WindowWarning(Stage stage, int xRes, int yRes)
	{
		super(stage, xRes, yRes);
	}
	
	public void show(Class<?> c)
	{
		txt.setText(((WindowMain) stage.getOwner().getUserData()).getDataManager().getText("tTxt_" + c.getSimpleName() + "Warning"));
		cl = c;
		show();
	}
	
	public void buildLayout()
	{
		initHashMap();
		
		WindowMain windowMain = (WindowMain) stage.getOwner().getUserData();
		
		txt = new Text();
		
		HBox hb0 = new HBox(10, txt);
		hb0.setAlignment(Pos.CENTER);
		
		Button btn_yes = new Button(windowMain.getDataManager().getText("tBtn_deleteWarningYes"));
		btn_yes.setOnAction(hmEventHandler.get("yes"));
		
		Button btn_no = new Button(windowMain.getDataManager().getText("tBtn_deleteWarningNo"));
		btn_no.setOnAction(hmEventHandler.get("no"));
		
		HBox hb1 = new HBox(10, btn_yes, btn_no);
		hb1.setAlignment(Pos.CENTER);
		
		GridPane grid = getGrid();
		grid.add(hb0, 0, 0);
		grid.add(hb1, 0, 1);
		grid.setAlignment(Pos.TOP_CENTER);

		Scene scene = new Scene(grid, xRes, yRes);
		stage.setScene(scene);
		
		
	}
	
	@Override
	protected void initHashMap(Object...args)
	{
		hmEventHandler.put("yes", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									WindowMain windowMain = (WindowMain) stage.getOwner().getUserData();
									
									if (cl == Cookbook.class)
										windowMain.getDataManager().deleteCB();
									else if (cl == Recipe.class)
									{
										windowMain.getDataManager().getWorkingCB().deleteRecipe(windowMain.getDataManager().getWorkingRecipe());
										windowMain.getDataManager().flipDeletedRecipe();
									}
									
									stage.close();
								}
							});
		
		hmEventHandler.put("no", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent event)
								{
									stage.close();									
								}
							});
	}
}
