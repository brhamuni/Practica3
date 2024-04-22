package player;

import java.io.*;
import java.util.*;
import java.net.*;


/**
 * Class MouseLoader detects and load all Mouse implementations
 * packaged under mouserun.mouse
 */
public class PlayerLoader
{
	private static final String PACKAGE = "player.";
	private static ArrayList<Class<?>> detected = null;

	/**
	 * Detects all Player Implementations in the package player.
	 * @return All the GamePlayer Implementation Classes which can be used to seed an instance.
	 */
	public static ArrayList<Class<?>> load()
	{
		detected = new ArrayList<Class<?>>();
		
		try
		{
			File directory = new File(getClassDirectory());
			File[] classFiles = directory.listFiles();
			
			if (classFiles != null)
			{
				for (File file : classFiles)
				{
					if (file.getName().endsWith(".class"))
					{
						String className = PACKAGE + file.getName().replace(".class", "");
						
						Class<?> clz = Class.forName(className);
					
						if (clz.getSuperclass() == GamePlayer.class)
						{						
							//System.out.println("Mouse Detected: " + clz.getSimpleName());
							detected.add(clz);
						}
					}
				}
			}
			//System.out.println();
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return detected;
	}
	
	public static ArrayList<Class<?>> getDetectedMouseClasses()
	{
		if (detected == null)
		{
			detected = new ArrayList<Class<?>>();
		}
		
		return detected;
	}

	// Gets the current directory of the MouseLoader that will be used as an anchor point
	// to get all other Mouse in the same package. This method will throw IllegalStateException
	// if the MouseLoader is not a directory, because this game is only designed to be
	// executed using the java command through the GameStarter.class 
	private static String getClassDirectory()
	{
		String file = "PlayerLoader.class";
		URL location = PlayerLoader.class.getResource(file);
		
		if (!location.getProtocol().equalsIgnoreCase("file"))
		{
			throw new IllegalStateException("MouseRun is not intended to run in this manner.");
		}
		
		String locationPath = location.getPath();
		locationPath = locationPath.substring(0, locationPath.length() - file.length());
		
		try
		{
			locationPath = URLDecoder.decode(locationPath, "UTF-8");
		}
		catch (UnsupportedEncodingException uee)
		{
			// do nothing here.
		}
		
		return locationPath;
	}
	
}
