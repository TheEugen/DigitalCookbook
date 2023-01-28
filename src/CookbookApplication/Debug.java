package CookbookApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;


public class Debug 
{
	private static boolean enable = false;
	private static String logfile = "C:\\Users\\Eugen\\Desktop\\log.txt";
	
	
	public static void log(String s)
	{
		if(enable)
			System.out.println("[" + getFormattedTime() + "]" + " DebugLog: " + s);
	}
	
	public static void write(String s)
	{
		if(enable)
		{
			File file = new File(logfile);
			
			if(!(file.isFile()))
			{
				try 
				{
					file.createNewFile();
				} 
				catch (IOException ioe) 
				{
					ioe.printStackTrace();
				}
			}
				
			try(FileWriter fw = new FileWriter(file, true);
					PrintWriter pw = new PrintWriter(fw))
			{
				pw.println("[" + getFormattedTime() + "]" + " DebugLog: " + s);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void enable(boolean b)
	{
		enable = b;
	}

	private static String getFormattedTime()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
									                     .withLocale(Locale.getDefault())
									                     .withZone(ZoneId.systemDefault());
		
		return formatter.format(Instant.now());
	}
}
