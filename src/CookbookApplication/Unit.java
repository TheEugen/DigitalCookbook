package CookbookApplication;

import java.io.Serializable;

public class Unit implements Serializable
{
	static final long serialVersionUID = 1;
	
	private String title;
	

	public Unit(String title)
	{
		this.title = title;
	}
	
	public String getTitle() {		return title; 		}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
}
