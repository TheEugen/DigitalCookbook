package CookbookApplication;

import java.io.Serializable;

public class Recipe implements Serializable
{
	static final long serialVersionUID = 1;
	
	private String title, ingredients, cooking;
	private Recipe baseRecipe;
	private int hash;
	

	// constructors
	public Recipe() 
	{
		this.title = "";
		this.baseRecipe = null;
		this.ingredients = "";
		this.cooking = "";
	}
	
	public Recipe (String title, Recipe baseRecipe, String ingredients, String cooking)
	{
		this.title = title;		
		this.baseRecipe = baseRecipe;
		this.ingredients = ingredients;
		this.cooking = cooking;
	}

	// getter
	public String getTitle()			{	return title;		}
	public String getIngredients()		{	return ingredients;	}
	public String getCooking()			{	return cooking;		}
	public Recipe getBaseRecipe()		{	return baseRecipe;	}
	
	// setter
	public void setTitle(String title)
	{
		assert(title != null);
		this.title = title;
	}
	
	public void setIngredients(String ingredients)
	{
		assert(ingredients != null);
		this.ingredients = ingredients;
	}
		
	public void setCooking(String cooking)
	{
		assert(cooking != null);
		this.cooking = cooking;
	}
	
	public void setBaseRecipe(Recipe recipe)
	{
		this.baseRecipe = recipe;
	}
	
	// recipe modifications
	public void copy(Recipe r)
	{
		this.title = r.getTitle();
		this.baseRecipe = r.getBaseRecipe();
		this.ingredients = r.getIngredients();
		this.cooking = r.getCooking();
	}
	
	public void edit(String title, Recipe baseRecipe, String ingredients, String cooking)
	{
		this.title = title;		
		this.baseRecipe = baseRecipe;
		this.ingredients = ingredients;
		this.cooking = cooking;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof Recipe)
		{
			Recipe r = (Recipe) obj;
			
			if (baseRecipe == null && r.getBaseRecipe() != null)
				return false;
			else if (baseRecipe != null && r.getBaseRecipe() == null)
				return false;
			else if (baseRecipe == null && r.getBaseRecipe() == null)
				return (title.equals(r.getTitle()) && ingredients.equals(r.getIngredients()) && cooking.equals(r.getCooking()));
			else	
				return (title.equals(r.getTitle()) && baseRecipe.equals(r.getBaseRecipe()) && ingredients.equals(r.getIngredients()) && cooking.equals(r.getCooking()));		
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
            if (baseRecipe != null)
            	h = 31 * h + baseRecipe.hashCode();
            h = 31 * h + ingredients.hashCode();
            h = 31 * h + cooking.hashCode();       
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