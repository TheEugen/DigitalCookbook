package CookbookApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Cookbook implements Serializable
{
	static final long serialVersionUID = 1;
	
	private String title = new String();
	private int amountOfRecipes;
	private List<Recipe> list = new ArrayList<Recipe>();
	private List<Recipe> recipes = Collections.synchronizedList(list);
	private int hash;
	
	
	public Cookbook() {}
	
	public Cookbook(String title)
	{
		this.title = title;
	}
		
	public int getAmountOfRecipes()			{	return amountOfRecipes;	}
	public String getTitle()				{	return title; 			}
	public List<Recipe> getRecipes()		{	return recipes;			}
	
	public String[] getRecipeNames()
	{
		if (recipes.isEmpty())
			return null;
		
		String[] titles = new String[recipes.size()];
		
		for (int i = 0; i < titles.length; ++i)
			titles[i] = recipes.get(i).getTitle();
				
		return titles;
	}
	
	public void setTitle(String title)
	{
		assert(title != null);
		this.title = title;
	}
	
	public void deleteRecipe(Recipe recipe)
	{
		assert(recipe != null);
		recipes.remove(recipe);
		amountOfRecipes--;
	}
	
	public void addRecipe(Recipe recipe)
	{
		assert(recipe != null);
		recipes.add(recipe);
		amountOfRecipes++;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Cookbook)
		{
			Cookbook cb = (Cookbook) obj;
			if (title.equals(cb.getTitle()) && amountOfRecipes == cb.getAmountOfRecipes())
				return recipes.equals(cb.getRecipes());
		}
		
		return false;
		
	}
	
	@Override
	public int hashCode()
	{
		int h = hash;
		
        if (h == 0 && title.length() > 0) // TODO check if obj is valid
        {
            h = 31;
            h = 31 * h + title.hashCode();
            h = 31 * h + (amountOfRecipes ^ (amountOfRecipes >>> 32)); // ????
            h = 31 * h + recipes.hashCode();
            hash = h;
        }
        
        return h;
	}
	@Override
	public String toString()
	{
		//return getClass().getName() + '@' + Integer.toHexString(hashCode());
		return title;
	}
	
}