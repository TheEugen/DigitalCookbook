package CookbookApplication;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WindowNewCookbook extends Window
{
	private WindowMain windowMain;
	
	private TextField tf = new TextField();
	private Text txt = new Text();
	
	public WindowNewCookbook(Stage stage, int xRes, int yRes) 
	{
		super(stage, xRes, yRes);
	}
	
	public boolean validateInput(String input)
	{
		if (input.length() > 0)
		{
			if (input.length() <= windowMain.getDataManager().getCBMaxTitleLength())
			{
				if ((windowMain.getDataManager().titleExists(Cookbook.class, input)))
				{	
					txt.setText(windowMain.getDataManager().getText("tTxt_cbTitleExists"));
					return false;
				}					
				else
					return true;	
			}	
			else
			{
				txt.setText(windowMain.getDataManager().getText("tTxt_TitleLength"));
				return false;
			}				
		}
		
		txt.setText(windowMain.getDataManager().getText("tTxt_TitleEmpty"));
		
		return false;
	}
	
	public void cleanUp()
	{
		tf.setText("");
		txt.setText("");
	}
	
	public void buildLayout()
	{
		initHashMap();
		
		windowMain = (WindowMain) stage.getOwner().getUserData();
		
		txt.setFill(Color.RED);
		
		GridPane grid = getGrid();
		
		grid.setAlignment(Pos.TOP_CENTER);
		
		VBox vb = new VBox(10);
		vb.setAlignment(Pos.TOP_CENTER);
	
		Button btn_save = new Button (windowMain.getDataManager().getText("tBtn_save"));
		btn_save.setOnAction(hmEventHandler.get("save"));
		Button btn_cancel = new Button (windowMain.getDataManager().getText("tBtn_cancel"));
		btn_cancel.setOnAction(hmEventHandler.get("cancel"));
		tf.setPrefWidth(xRes / 1.5f);
		
		vb.getChildren().add(txt);
		vb.getChildren().add(new Text(windowMain.getDataManager().getText("tTxt_titleOfCB")));
		vb.getChildren().add(tf);
		grid.add(vb, 0, 0);
		
		HBox hb = new HBox(10, btn_save, btn_cancel);
		hb.setAlignment(Pos.BOTTOM_RIGHT);
		
		grid.add(hb, 0, 1);
		
		scene = new Scene(grid, xRes, yRes);
		stage.setScene(scene);	
	}
	
	@Override
	protected void initHashMap(Object...args)
	{
		hmEventHandler.put("save", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent e)
								{
									String input = tf.getText();
									
									if (validateInput(input))
									{
										windowMain.getDataManager().addCookbook(new Cookbook(input));
										windowMain.getDataManager().saveCookbook();		
										stage.close();
										cleanUp();
									}																								
								}
							});
		
		hmEventHandler.put("cancel", new EventHandler<ActionEvent>()
							{
								@Override
								public void handle(ActionEvent e)
								{
									stage.close();
									cleanUp();
								}
							});
	}
	
}
