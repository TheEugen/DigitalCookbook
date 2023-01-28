package CookbookApplication;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import java.util.HashMap;


public abstract class Window 
{
	protected final Stage stage;
	protected Scene scene;
	protected int xRes, yRes;
	protected HashMap<String, EventHandler<ActionEvent>> hmEventHandler = new HashMap<String, EventHandler<ActionEvent>>();
	
	public abstract void buildLayout();
	protected abstract void initHashMap(Object...args);
	
	
	public void show()						{	stage.show();	}
	public Stage getStage()					{	return stage;	}
		
	public Window(Stage stage, int xRes, int yRes)				
	{	
		this.stage = stage;
		this.xRes = xRes;
		this.yRes = yRes;
		//initHashMap();
	}

	public GridPane getGrid(Pos alignment, double hgap, double vgap, Insets insets)
	{
		GridPane grid = new GridPane();
		grid.setAlignment(alignment);
		grid.setHgap(hgap);
		grid.setVgap(vgap);
		grid.setPadding(insets);
		
		return grid;
	}
	
	public GridPane getGrid()
	{
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15, 25, 15, 25));
		
		return grid;
	}
}
