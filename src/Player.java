import java.awt.Color;
import java.util.ArrayList;
 //PANICARDS PLAYER
 
public class Player
{
    public int mouseX, mouseY;//are scaled out of normalWidth and normalHeight. so max for x is: normalWidth, minimum is 0. 
    public int ID;
    public String IP;
    public int clientArrayIndex;
    public static final int MODE_EMPTY_HAND = 0, MODE_HOLDING_CARD = 1;
    public int mode = MODE_EMPTY_HAND;
    public Color color;
    public Card carryingCard = null;
    public int otherPlayerGrabbed = -1;
    public int playerGrabbingMe = -1;
    public int rotation = 0;//screen rotation -- what side of the table are you sitting on?
    public static final int ROTATION_DEFAULT = 0, ROTATION_180 = 1;
    
    public String username = "nullName";
    
    public int mistakes = 0;
    
    
    public int getImageIndex()
    {
    	return ImageHelper.IMAGE_PLAYER_DEFAULT;
    }
    
    
    public ArrayList<Card> slidyCards = new ArrayList<Card>();
    /*
     * mouseX, mouseY, card objects will have an int owner, and int carrier,  
     * so the player needs an ID
     * 
     * players will also have traits assigned with cheating and stuff, but that is later.
     * 
     */
     
    public Player()
    {
 
    }
    public Player(String IP, boolean irrelevant)//used in clientdata in server
    {
    this.IP=IP;
    int r = Integer.parseInt(IP.substring(1,IP.indexOf('.')));
    String s2 = IP.substring(1,IP.indexOf('.'));
    String xx = IP.substring(IP.indexOf('.'));
    String x2 = xx.substring(1);
    String x3 = x2.substring(x2.indexOf('.'));
    String x4 = x3.substring(1);
    String x5 = x4.substring(0, x4.indexOf('.'));
    int g = pint(x2.substring(0,x2.indexOf('.')));
    int b = pint(x5);
    color = new Color(r, g, b);
    }
     
    public static int pint(String s)
    {
        return Integer.parseInt(s);
    }
     
    public Player(String z)//tostring from socket
    {
        String[] s = z.split(" ");
        //0 and 1 are "new player " or "new user " 
        this.mouseX = pint(s[2]);
        this.mouseY = pint(s[3]);
        this.color = new Color(pint(s[4]), pint(s[5]),pint(s[6]));
        this.ID = pint(s[7]);
        this.mode = pint(s[8]);
        if (s[9].equals("null"))
        	this.carryingCard = null;
        else
        	this.carryingCard = new Card(s[9]);
        
        this.otherPlayerGrabbed = pint(s[10]);
        this.playerGrabbingMe = pint(s[11]);
        
        this.clientArrayIndex = pint(s[12]);
        
        this.rotation = pint(s[13]);
        this.mistakes = pint(s[14]);
        this.username = s[15];

        int size = pint(s[16]);
        if (size > 0)
        {
        	//s[16] is slidy-card data
        	slidyCards = new ArrayList<Card>();
        	String[] scs = s[17].split(";");
        	for (int i = 0; i < scs.length; i++)
        	{
        		slidyCards.add(new Card(scs[i]));
        	}
        	
        }
        //else s[16] == "null"
        
        
    }
     
    public String toString()
    {
        String out = "new player ";
        out += mouseX + " " + mouseY + " " + this.color.getRed() + " " + this.color.getGreen() + " " + this.color.getBlue();
        out += " " + this.ID;
        out += " " + this.mode + " ";
        if (mode == MODE_HOLDING_CARD && carryingCard != null)
        	out += carryingCard.toSocketString();
        else
        	out += "null";
       
        
        out += " " + otherPlayerGrabbed+ " " + playerGrabbingMe;
        out += " " + clientArrayIndex;
        out += " " + rotation;
        out += " " + mistakes;
        out += " " + username;
        
        out += " " + slidyCards.size() + " ";//used for real-time only
        for (int i = 0; i < slidyCards.size(); i++)
        {
        	out += slidyCards.get(i).toSocketString() + ";";
        }
        if (slidyCards.size() == 0)
        	out += " null";
         
        return out;
    }
     
}

