package CookbookApplication;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class HtmlManager 
{
	private final int SCRIPT_INDEX = 26;
	private final String regex_title = "name\": \"[^\"]*\",";
	private final String regex_rawIngredients = "\\brecipeIngredient\\b[^]]*]";
	private final String regex_cooking = "\\brecipeInstructions\\b\": \".*(\",)";
	
	private Document doc;
	
	private String recipe_title = "";
	private String recipe_ingredients = "";
	private String recipe_cooking = "";
	
	private boolean searchHtmlCalled;
	
	private Pattern pTitle, pIngredients, pCooking;
	
	public HtmlManager()
	{
		// build the regex patterns
		pTitle = Pattern.compile(regex_title, Pattern.DOTALL);
		pIngredients = Pattern.compile(regex_rawIngredients, Pattern.DOTALL);
		pCooking = Pattern.compile(regex_cooking);
	}
	
	public void cleanUp()
	{
		// erase all saved data
		recipe_title = "";
		recipe_ingredients = "";
		recipe_cooking = "";
		
		searchHtmlCalled = false;
	}
	
	public Recipe buildRecipe() throws Exception
	{
		// getAndSearchHTML() must be called first
		if (!(searchHtmlCalled))
			throw new Exception ("HTML Manager: need to call searchAndBuildHtml before buildRecipe");
		
		// create new recipe
		Recipe r = new Recipe(recipe_title.toString(), null, recipe_ingredients.toString(), recipe_cooking.toString());
		
		// cleanup for the next round
		cleanUp();
		
		return r;
	}
	
	public String filterCooking(String cooking)
	{
		return StringEscapeUtils.unescapeJava(cooking).replaceAll("\n\n\n+", "\n\n");//.replaceAll("(.{55,85}) ", "$1\n").replaceAll("\n\n\n+", "\n\n");
	}
	
	public String filterTitle(String title)
	{
		return StringEscapeUtils.unescapeJava(title).replace("name\": ", "").replace("\"", "").replace(",", "");	
	}
	
	// returns null when string isnt a ingredient (empty line etc.)
	public String filterIngredient(String rawIngredient)
	{
		if(!((rawIngredient.startsWith(",")) || (rawIngredient.startsWith("recipe")) || (rawIngredient.startsWith(":")) || (rawIngredient.endsWith("]")) || (rawIngredient.startsWith("   "))))
			return StringEscapeUtils.unescapeJava(rawIngredient).replaceAll("  ", " ").replaceAll(" , ", ", ").replaceAll("([0-9]+) (((TL)|(EL)|g))", "$1$2");
		 
		 return null;
	}
	
	public void getAndSearchHTML(String url)
	{		
		try 
		{			
			doc  = Jsoup.connect(url).get();		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// TODO: find better way to select recipe <script>
		// get <script> element holding the recipe
		Element element = doc.select("script").get(SCRIPT_INDEX);

		// find the recipe title
		Matcher mTitle = pTitle.matcher(element.html());
		mTitle.find();	
		
		// find the ingredients
		Matcher mIngredients = pIngredients.matcher(element.html());	
		mIngredients.find();
		
		// find the instructions
		Matcher mCooking = pCooking.matcher(element.html());
		mCooking.find();
		
		// save the recipe title
		recipe_title = filterTitle(mTitle.group(0));
		
		// save the ingredients
		String[] rawIngredients = mIngredients.group(0).split("\"*\"");	
		String value;	
		for(String s: rawIngredients)
		{	
			if((value = filterIngredient(s)) != null)
				recipe_ingredients += value + "\n";
		}
		
		// save the instructions
		String[] rawCooking = mCooking.group(0).split("\"");
		
		// TODO	?????
		for(int i = 2; i < rawCooking.length; ++i)
			recipe_cooking += filterCooking(rawCooking[i]);
			
		searchHtmlCalled = true;		
	}
	
}
