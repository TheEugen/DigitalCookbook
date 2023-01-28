package CookbookApplication;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;


public class JSONHandler implements Runnable
{
	private Main main;
	private AtomicBoolean running = new AtomicBoolean(false);
	

	public JSONHandler(Main main)
	{
		this.main = main;
	}
	
	public void setRunning(boolean b)		{	running.set(b);		}

		
	@Override
	public void run()
	{
		// initializing
		running.set(true);
		DataManager dataManager = main.getDataManager();
		HtmlManager html = main.getHtmlManager();
		boolean recipeExists = false;
		JSONObject obj = new JSONObject();
		String msg = "";
		
		try 
		{
			while(running.get())
			{
				while(System.in.available() > 0)
				{
					try 
					{
						msg = readMessage(System.in);							
						msg = msg.replaceAll("\\\"", "");
						
						if(msg.startsWith("http"))
						{
							// process the html document
							html.getAndSearchHTML(msg);
							
							// create new recipe
							Recipe newRecipe = new Recipe();
							try 
							{
								newRecipe = html.buildRecipe();
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
							}			
							
							// check if importedrecipe already exists
							for(Recipe r: dataManager.getWorkingCB().getRecipes())
							{
								if(r.equals(newRecipe))
									recipeExists = true;
							}
							
							// debug
							obj.put("receivedMessage", msg);
							
							if (!(recipeExists))
							{
								// make thread-safe
								// add imported recipe to the cookbook
								dataManager.getWorkingCB().addRecipe(newRecipe);
								
								// set UI focus on new recipe
								dataManager.setWorkingRecipe(newRecipe);
								
								// ??
								dataManager.flipNewRecipe();
								
								// debug
								obj.put("importSuccess", true);
							}
							else
								obj.put("importSuccess", false);
							
							// debug send message to browser
							sendMessage(obj.toString());
							
							// reset flag
							if(recipeExists)
								recipeExists = false;
						}		
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String readMessage(InputStream in) throws Exception
	{
		byte[] b = new byte[4];
		in.read(b);

		int size = getInt(b);

		if (size == 0)
			throw new InterruptedIOException("Blocked communication");

		b = new byte[size];
		in.read(b);
		
		return new String(b, "UTF-8");
	}
	
	private int getInt(byte[] bytes) 
	{
		return (bytes[3] << 24) & 0xff000000 |
				(bytes[2] << 16) & 0x00ff0000 |
				(bytes[1] << 8) & 0x0000ff00 |
				(bytes[0] << 0) & 0x000000ff;
	}
	
	private void sendMessage(String message) throws IOException 
	{
		// give stdout number of incoming characters
		System.out.write(getBytes(message.length()));
		
		// give stdout the message
		System.out.write(message.getBytes("UTF-8"));
		
		// cleanup
		System.out.flush();
	}

	private byte[] getBytes(int length) 
	{
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (length & 0xFF);
		bytes[1] = (byte) ((length >> 8) & 0xFF);
		bytes[2] = (byte) ((length >> 16) & 0xFF);
		bytes[3] = (byte) ((length >> 24) & 0xFF);
		return bytes;
	}
}
