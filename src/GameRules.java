import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class GameRules
{
	public TreeMap<Integer, Boolean> gameRules = new TreeMap<Integer, Boolean>();
	public static final int GRAB_OTHER_PLAYERS_HAND = 0, BACKGROUND_NAUSEA = 1, SLIDY_CARDS = 2, SEPARATE_SIDES = 3, GAME_OF_SPEED = 4;
	public static final String[] descriptions = {
		"grab other players' hands", "nausea background", "slidy cards", "separate sides", "game of speed"
	};
	
	/*
	 * --separate sides--
	 * draw lines on the map.
	 * gray out enemy cards.
	 * --game of speed--
	 * if cards are placed on invalid up-facing cards, they are returned to where they were picked up.
	 * 
	 * 
	 */
	
	public GameRules()
	{
		
	}
	
	public String toSocketString()//gameRules is defined only on the server, and is sent server->client only.
	{
		String out = "new gamerules ";
		out += gameRules.size();
		for (Integer i : gameRules.keySet())//int i = 0; i < gameRules.size(); i++)
		{
			out += " " + i + "-" + gameRules.get(i);
		}
		return out;
	}
	public void proposeChange()//client --> server ? 
	{
		
	}
	
	public boolean get(int i)
	{
		return gameRules.get(i);
	}
	
	public GameRules(String z)//paired of toSocketString()
	{
		gameRules = new TreeMap<Integer, Boolean>();
		String[] s = z.split(" ");
		for (int i = 0+3; i < s.length; i++)
		{
			
			String[] twoParts = s[i].split("-");
			int index = pint(twoParts[0]);
			boolean b = Boolean.parseBoolean(twoParts[1]);
			gameRules.put(index, b);
		}
		
		System.out.println("--GameRules--");
		for (Integer i : gameRules.keySet())
		{
			System.out.println(descriptions[i] + " :: " + gameRules.get(i));
		}
	}
	public static int pint(String s)
	{
		return Integer.parseInt(s);
	}
	
	public static int getIndexOfDesc(String desc)//desciption
	{
		int out = -1;//-1 means not found
		for (int i = 0; i < descriptions.length; i++)
		{
			if (desc.equalsIgnoreCase(descriptions[i]))
			{
				out = i;
				break;
			}
		}
		return out;
	}
	
	public boolean defineSelfFromFile()
	{
		try{
		Scanner scan = new Scanner(new File("gamerules.txt"));
		gameRules = new TreeMap<Integer, Boolean>();
		scan.nextLine();//skip instruction line
		while(scan.hasNextLine())
		{
			
			String line = scan.nextLine();
			System.out.println("line="+line);
			String[] split = line.split(":");
			String desc = split[0];
			try{
			Boolean value = Boolean.parseBoolean(split[1]);
			int index = getIndexOfDesc(desc);
			
			if (index != -1)//if it had a valid description
			gameRules.put(index,value);
			}catch(Exception e){};//this means something retarded happened but it doesn't matter actually
			
		}
		
		return true;
		
		
		}catch(FileNotFoundException e){//will only rewrite the file if it doesnt exist -- so the user must delete the file if they wish for it to be reset to default.
			
			//create gamerules.txt file -- stored on server only
			String lines = "";
			
			String n = "\r\n";
			
			lines += "Put true or false for game rules!";
			lines += n;
			for (String s : descriptions)
			{
				lines += s + ":false" + n;//off by default
			}
			
			
			Panicards.createFile("gamerules.txt", lines);
			return false;
		}
		
		
		/* Put true or false for game rules
		 * grab hands:true
		 * grab other player's cards:true
		 */
	}
	
	
	
}